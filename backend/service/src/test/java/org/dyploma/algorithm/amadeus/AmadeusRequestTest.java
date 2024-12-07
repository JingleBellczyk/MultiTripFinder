package org.dyploma.algorithm.amadeus;

import org.dyploma.EngineeringWorkApplication;
import org.dyploma.algorithm.externalApi.amadeus.AmadeusApiClient;
import org.dyploma.algorithm.externalApi.amadeus.dto.AmadeusRequest;
import org.dyploma.algorithm.externalApi.amadeus.dto.AmadeusResponse;
import org.dyploma.algorithm.externalApi.amadeus.dto.TravelSegment;
import org.dyploma.exception.AmadeusErrorException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {EngineeringWorkApplication.class})
public class AmadeusRequestTest {

    class Airport {
        private String iataCode;
        private int hoursNumber;

        public Airport(String iataCode, int hoursNumber) {
            this.iataCode = iataCode;
            this.hoursNumber = hoursNumber;
        }

        public String getIataCode() {
            return iataCode;
        }

        public void setIataCode(String iataCode) {
            this.iataCode = iataCode;
        }

        public int getHoursNumber() {
            return hoursNumber;
        }

        public void setHoursNumber(int hoursNumber) {
            this.hoursNumber = hoursNumber;
        }
    }

    @Autowired
    private AmadeusApiClient amadeusApiClient;

    private int previousTime = 1000000;
    private int previousCost = 1000000;

    public static List<List<Airport>> generatePermutations(List<Airport> airports) {
        List<List<Airport>> result = new ArrayList<>();
        permute(airports, 0, result);
        return result;
    }

    private static void permute(List<Airport> airports, int index, List<List<Airport>> result) {
        if (index == airports.size()) {
            result.add(new ArrayList<Airport>(airports));
            return;
        }

        for (int i = index; i < airports.size(); i++) {
            swap(airports, index, i);
            permute(airports, index + 1, result);
            swap(airports, index, i);
        }
    }

    private static void swap(List<Airport> airports, int i, int j) {
        Airport temp = airports.get(i);
        airports.set(i, airports.get(j));
        airports.set(j, temp);
    }

    @Test
    void testAlgorithm() throws InterruptedException {
        List<Airport> airports = new ArrayList<>(Arrays.asList(
                new Airport("LON", 72),
                new Airport("WRO", 96),
                new Airport("WAW", 120),
                new Airport("OPO", 0)
        ));

        List<List<Airport>> permutations = generatePermutations(airports);
        long startTime = System.nanoTime();

        // Wydrukowanie wszystkich permutacji
        for (List<Airport> perm : permutations) {
            System.out.println("----------------------------------------------------------------------------");
            System.out.println(perm);
            countConnection(perm, "2024-11-24");
            System.out.println("current best time and cost " + this.previousTime + " minutes, cost: " + this.previousCost + " euro");
            Thread.sleep(2000);
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // konwersja z nanosekund na milisekundy
        System.out.println(permutations.size());
        System.out.println("current best time and cost " + this.previousTime + " min, cost: " + this.previousCost + " euro");
        System.out.println("Czas trwania pętli: " + duration + " ms");
    }

    @Test
    public void testDate() {
        System.out.println(countDate("2021-10-21", 6));
    }

    public static String countDate(String startDate, int hoursNumber) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate date = LocalDate.parse(startDate, formatter);

        int daysNumber = hoursNumber / 24;
        LocalDate newDate = date.plusDays(daysNumber);

        return newDate.format(formatter);
    }

    public static int convertDurationToMinutes(String duration) {
        // Sprawdzamy, czy ciąg zaczyna się od "PT", co jest wymagane w ISO 8601
        if (duration == null || !duration.startsWith("PT")) {
            throw new IllegalArgumentException("Invalid duration format");
        }

        int totalMinutes = 0;

        // Usuwamy prefix "PT"
        duration = duration.substring(2);

        // Jeśli są godziny (H), konwertujemy je na minuty
        if (duration.contains("H")) {
            String[] parts = duration.split("H");
            int hours = Integer.parseInt(parts[0]);
            totalMinutes += hours * 60;
            duration = parts.length > 1 ? parts[1] : "";  // pozostała część po godzinach
        }

        // Jeśli są minuty (M), dodajemy je do sumy
        if (duration.contains("M")) {
            String[] parts = duration.split("M");
            int minutes = Integer.parseInt(parts[0]);
            totalMinutes += minutes;
        }

        return totalMinutes;
    }

    public int countConnection(List<Airport> permutation, String startDate) {
        System.out.println("New trip");
        int totalCost = 0;
        int totalTime = 0;

        for (int i = 0; i < permutation.size() - 1; i++) {
            List<TravelSegment> travelSegments = Arrays.asList(
                    new TravelSegment("1", permutation.get(i).getIataCode(), permutation.get(i + 1).getIataCode(), startDate, "08:00")
            );

            List<AmadeusRequest.OriginDestination> originDestinations = amadeusApiClient.createOriginDestinations(travelSegments);
            System.out.println(permutation.get(i) + " " + permutation.get(i + 1) + startDate);
            AmadeusResponse response = amadeusApiClient.sendAmadeusRequest(originDestinations, 1);
            totalCost += response.getData().get(0).getGrandTotal();
            totalTime += convertDurationToMinutes(response.getData().get(0).getItineraries().get(0).getDuration());
            System.out.println(response);

            if (totalCost > this.previousCost || totalTime > this.previousTime) {
                System.out.println("current best time and cost " + this.previousTime + " min, cost: " + this.previousCost + " euro");
                System.out.println("this time and cost " + totalTime + " min, cost:  " + totalCost + " euro");

                return totalCost;
            }
            startDate = countDate(startDate, permutation.get(i).getHoursNumber());
        }
        this.previousCost = totalCost;
        this.previousTime = totalTime;
        return totalCost;
    }

    @Test
    void sendNormalAmadeusRequest() {

        List<TravelSegment> travelSegments = Arrays.asList(
                new TravelSegment("1", "LON", "WRO", "2024-12-21", "10:00")
        );

        List<AmadeusRequest.OriginDestination> originDestinations = amadeusApiClient.createOriginDestinations(travelSegments);

        int maxFlightOffers = 1;

        AmadeusResponse response = amadeusApiClient.sendAmadeusRequest(originDestinations, maxFlightOffers, 999, 999);

        System.out.println(response);
    }

    @Test
    void sendNormalAmadeusRequest2Transfers() {

        List<TravelSegment> travelSegments = Arrays.asList(
                new TravelSegment("1", "NYC", "BOS", "2024-11-21", "08:00"),
                new TravelSegment("2", "LON", "AMS", "2024-12-02", "12:00")
        );

        List<AmadeusRequest.OriginDestination> originDestinations = amadeusApiClient.createOriginDestinations(travelSegments);

        int maxFlightOffers = 3;

        AmadeusResponse response = amadeusApiClient.sendAmadeusRequest(originDestinations, maxFlightOffers);

        System.out.println(response);
    }

    @Test
    void sendAmadeusRequestPriceTime() {

        List<TravelSegment> travelSegments = Arrays.asList(
                new TravelSegment("1", "NYC", "BOS", "2024-11-21", "08:00"),
                new TravelSegment("2", "LON", "AMS", "2024-12-02", "12:00")
        );

        List<AmadeusRequest.OriginDestination> originDestinations = amadeusApiClient.createOriginDestinations(travelSegments);

        int maxFlightOffers = 3;
        int maxPrice = 10;
        int maxFlightTime = 200;

        AmadeusResponse response = amadeusApiClient.sendAmadeusRequest(originDestinations, maxFlightOffers, maxPrice, maxFlightTime);

        System.out.println(response);
    }

    @Test
    void sendAmadeusRequestPastDate() {
        List<TravelSegment> travelSegments = Arrays.asList(
                new TravelSegment("1", "NYC", "BOS", "2024-10-21", "08:00"),
                new TravelSegment("2", "LON", "AMS", "2024-12-02", "12:00")
        );

        List<AmadeusRequest.OriginDestination> originDestinations = amadeusApiClient.createOriginDestinations(travelSegments);
        if (originDestinations == null) {

        }
        int maxFlightOffers = 3;

        AmadeusErrorException thrownException = assertThrows(AmadeusErrorException.class, () -> {
            amadeusApiClient.sendAmadeusRequest(originDestinations, maxFlightOffers);
        });

        assertEquals(400, thrownException.getStatusCode());

        String responseBody = thrownException.getErrorMessage();
        System.out.println("Response Body: " + responseBody);
        assertTrue(responseBody.contains("Date/Time is in the past"));
    }

    @Test
    void sendAmadeusRequestTooManySegments() {
        List<TravelSegment> travelSegments = Arrays.asList(
                new TravelSegment("1", "NYC", "BOS", "2024-12-21", "08:00"),
                new TravelSegment("2", "LON", "AMS", "2024-12-02", "12:00"),
                new TravelSegment("3", "BOS", "NYC", "2024-12-02", "12:00")
        );

        List<AmadeusRequest.OriginDestination> originDestinations = amadeusApiClient.createOriginDestinations(travelSegments);
        assertNull(originDestinations, "Expected null when there are more than 2 segments");
    }
}
