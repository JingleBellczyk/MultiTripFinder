package org.dyploma.algorithm.amadeus;

import org.dyploma.algorithm.amadeus.dto.AmadeusRequest;
import org.dyploma.algorithm.amadeus.dto.AmadeusResponse;
import org.dyploma.algorithm.amadeus.dto.TravelSegment;
import org.dyploma.exception.AmadeusErrorException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AmadeusRequestTest {

    @Autowired
    private AmadeusApiClient amadeusApiClient;

    @Test
    void sendNormalAmadeusRequest(){

        List<TravelSegment> travelSegments = Arrays.asList(
                new TravelSegment("1","NYC", "BOS", "2024-11-21", "08:00")
        );

        List<AmadeusRequest.OriginDestination> originDestinations = amadeusApiClient.createOriginDestinations(travelSegments);

        int maxFlightOffers = 3;

        AmadeusResponse response = amadeusApiClient.sendAmadeusRequest(originDestinations, maxFlightOffers);

        System.out.println(response);
    }

    @Test
    void sendNormalAmadeusRequest2Transfers(){

        List<TravelSegment> travelSegments = Arrays.asList(
                new TravelSegment("1","NYC", "BOS", "2024-11-21", "08:00"),
                new TravelSegment("2", "LON", "AMS", "2024-12-02", "12:00")
        );

        List<AmadeusRequest.OriginDestination> originDestinations = amadeusApiClient.createOriginDestinations(travelSegments);

        int maxFlightOffers = 3;

        AmadeusResponse response = amadeusApiClient.sendAmadeusRequest(originDestinations, maxFlightOffers);

        System.out.println(response);
    }

    @Test
    void sendAmadeusRequestPriceTime(){

        List<TravelSegment> travelSegments = Arrays.asList(
                new TravelSegment("1","NYC", "BOS", "2024-11-21", "08:00"),
                new TravelSegment("2","LON", "AMS", "2024-12-02", "12:00")
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
                new TravelSegment("1","NYC", "BOS", "2024-10-21", "08:00"),
                new TravelSegment("2","LON", "AMS", "2024-12-02", "12:00")
        );

        List<AmadeusRequest.OriginDestination> originDestinations = amadeusApiClient.createOriginDestinations(travelSegments);
        int maxFlightOffers = 3;

        AmadeusErrorException thrownException = assertThrows(AmadeusErrorException.class, () -> {
            amadeusApiClient.sendAmadeusRequest(originDestinations, maxFlightOffers);
        });

        assertEquals(400, thrownException.getStatusCode());

        String responseBody = thrownException.getErrorMessage();
        System.out.println("Response Body: " + responseBody);
        assertTrue(responseBody.contains("Date/Time is in the past"));
    }
}
