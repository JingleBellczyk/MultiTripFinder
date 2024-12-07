package org.dyploma.algorithm.amadeus;

import org.dyploma.airport.Airport;
import org.dyploma.airport.AirportService;
import org.dyploma.algorithm.externalApi.google.dto.GoogleApiClient;
import org.dyploma.algorithm.externalApi.google.dto.GoogleRoutesResponse;
import org.dyploma.search.domain.SearchRequest;
import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.trip.domain.Trip;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.trip.transfer.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.dyploma.transport.TransportMode;
import org.dyploma.search.criteria.CriteriaMode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

@SpringBootTest
public class GoogleRoutesTest {

//    SearchRequest searchRequest;
//
//    List<PlacesWithAirport> placesWithAirportList;
//
//    @Autowired
//    private AirportService airportService;

//    @BeforeEach
//    void setUp() {
//        //        definiuję argumenty
//        List<PlaceInSearch> placesToVisit = Arrays.asList(
//                PlaceInSearch.builder()
//                        .country("Poland")
//                        .city("Wroclaw")
//                        .stayDuration(0)
//                        .entryOrder(1)
//                        .build(),
//                PlaceInSearch.builder()
//                        .country("Germany")
//                        .city("Düsseldorf")
//                        .stayDuration(3)
//                        .entryOrder(2)
//                        .build(),
//                PlaceInSearch.builder()
//                        .country("Poland")
//                        .city("Złoty stok")
//                        .stayDuration(0)
//                        .entryOrder(3)
//                        .build()
//        );
//
//        // Tworzenie instancji SearchRequest
//        SearchRequest searchRequest = SearchRequest.builder()
//                .placesToVisit(placesToVisit)
//                .passengerCount(2)
//                .preferredTransport(TransportMode.TRAIN)
//                .optimizationCriteria(CriteriaMode.DURATION)
//                .tripStartDate(LocalDate.of(2024, 12, 5))
//                .maxTripDuration(7)
//                .build();
//
//        this.searchRequest = searchRequest;
//
//        this.placesWithAirportList = new ArrayList<>();
//    }


//    public Airport getAirportByCountryAndCity(String city, String country) {
//
//        for (PlacesWithAirport placesWithAirport : this.placesWithAirportList) {
//            if (placesWithAirport.getCountry().equals(country)
//                    && placesWithAirport.getCity().equals(city)) {
//                return placesWithAirport.getAirport();
//            }
//        }
//        return null;
//    }

//    public List<PlacesWithAirport> prepareListOfPlacesAndAirport() {
//
//        for (PlaceInSearch placeInSearch : searchRequest.getPlacesToVisit()) {
//            Airport airport = airportService.getAirportByCountryAndCity(placeInSearch.getCountry(), placeInSearch.getCity());
//            this.placesWithAirportList.add(new PlacesWithAirport(placeInSearch.getCountry(), placeInSearch.getCity(), placeInSearch.getStayDuration(), placeInSearch.getEntryOrder(), airport));
//        }
//
//        System.out.println(this.placesWithAirportList.toString());
//        return this.placesWithAirportList;
//    }

//    public List<List<PlacesWithAirport>> permutePlacesWithAirport(List<PlacesWithAirport> placesWithAirportTable) {
//
//        if (placesWithAirportTable.size() == 2) {
//            List<List<PlacesWithAirport>> permutations = new ArrayList<>();
//            permutations.add(placesWithAirportTable);
//            return permutations;
//        }
//
//        PlacesWithAirport startPlace = placesWithAirportTable.get(0);
//        PlacesWithAirport endPlace = placesWithAirportTable.get(placesWithAirportTable.size() - 1);
//        List<PlacesWithAirport> middleSection = placesWithAirportTable.subList(1, placesWithAirportTable.size() - 1);
//
//        return PlacesPermutationUtils.generateAllRecursive(middleSection.size(), middleSection, startPlace, endPlace);
//    }
//
//    @Test
//    void test1() {
//        prepareListOfPlacesAndAirport();
//        System.out.println(permutePlacesWithAirport(this.placesWithAirportList));
//    }

//    public Transfer convertStepIntoTransfer(GoogleRoutesResponse.Step step, int transferOrder) {
//        LocalDateTime startDateTime = LocalDateTime.parse(step.getTransitDetails().getStopDetails().getDepartureTime(), DateTimeFormatter.ISO_DATE_TIME);
////                                LocalDate startLocalDate = startDateTime.toLocalDate();
//
//        LocalDateTime endDateTime = LocalDateTime.parse(step.getTransitDetails().getStopDetails().getArrivalTime(), DateTimeFormatter.ISO_DATE_TIME);
//        String vehicleType = step.getTransitDetails().getTransitLine().getVehicle().getType();
////                              //todo konwertuj vehicleType na TransportMode
//
//        //todo carrier czasem jest kilka, na razie biore 0
//        String carrier = step.getTransitDetails().getTransitLine().getAgencies().get(0).getName();
//
//        int minutesDifference = (int) Duration.between(startDateTime, endDateTime).toMinutes();
//        //todo policz koszt wg.współrzędnych
//
//        String startAddress = step.getTransitDetails().getStopDetails().getDepartureStop().getName();
//        String endAddress = step.getTransitDetails().getStopDetails().getArrivalStop().getName();
//
//        Transfer transfer = Transfer.builder()
//                .transportMode(TransportMode.TRAIN)
//                .carrier(carrier)
//                .startDateTime(startDateTime)
//                .endDateTime(endDateTime)
//                .duration(minutesDifference)
//                .cost(0.0)
//                .startAddress(startAddress)
//                .endAddress(endAddress)
//                .transferOrder(transferOrder)
//                .build();
//
//        return transfer;
//    }
//
//    public PlaceInTrip convertStepIntoPlace(GoogleRoutesResponse.Step step, Boolean isTransfer, int visitOrder) {
//        String city;
//        if(isTransfer){
//            city = step.getTransitDetails().getStopDetails().getDepartureStop().getName();
//        }else{
//            city = step.getTransitDetails().getStopDetails().getArrivalStop().getName();
//        }
//
//        PlaceInTrip place = PlaceInTrip.builder()
//                .country("no info")
//                .city(city)
//                .isTransfer(isTransfer)
//                .stayDuration(0)
//                .visitOrder(visitOrder)
//                .build();
//        return place;
//    }

//    @Test
//    public void multipleTrips(){
//        List<PlaceInTrip> placeInTripList = new ArrayList<>();
//        List<Transfer> transferList = new ArrayList<>();
//        prepareListOfPlacesAndAirport();
//
//        List<List<PlacesWithAirport>> permutations =  permutePlacesWithAirport(this.placesWithAirportList);
//        List<PlacesWithAirport> permutation = permutations.get(0);
//        String[] date = {"2024-12-05T15:01:23Z", "2024-12-10T10:01:23Z", "2024-12-15T17:01:23Z", "2024-12-05T15:01:23Z"};
//
//        for(int i = 0; i < permutation.size() - 1; i++){
//            send1GoogleRequest(placeInTripList,
//                    transferList,
//                    permutation.get(i).getCity(),
//                    permutation.get(i).getCountry(),
//                    permutation.get(i+1).getCity(),
//                    permutation.get(i+1).getCountry(),
//                    date[i],
//                    Arrays.asList("TRAIN", "BUS", "RAIL")
//            );
//        }
//        System.out.println(placeInTripList);
//        System.out.println(transferList);
//
//    }
//
//    public void oneTrip(List<PlaceInTrip> placeInTripList, List<Transfer> transferList ){
////        send1GoogleRequest(placeInTripList, transferList, "Wrocław", "Poland","Szczecin",  "Poland");
////        System.out.println(" -------------------------------");
////        System.out.println(placeInTripList);
//    }

//    public void send1GoogleRequest(List<PlaceInTrip> placeInTripList, List<Transfer> transferList,
//                                   String cityStart, String countryStart, String cityEnd,
//                                   String countryEnd, String date, List<String> prefferedTransport) {
////        String city1 = "Wrocław";
////        String country1 = "Poland";
////        String city2 = "Szczecin";
////        String country2 = "Poland";
//
//
//        send1GoogleRequest(placeInTripList,
//                transferList,
//                cityStart + ", " + countryStart,
//                cityEnd + ", " + countryEnd,
//                date,
//                prefferedTransport);
//
//        int passengerCount = 1;
//
//        LocalDate startLocalDate = transferList.get(0).getStartDateTime().toLocalDate();
//        LocalDate endLocalDate = transferList.get(transferList.size()-1).getEndDateTime().toLocalDate();
//
//        Trip trip = Trip.builder()
//                .transfers(transferList)
//                .places(placeInTripList)
//                .startDate(startLocalDate)
//                .endDate(endLocalDate)
//                .passengerCount(passengerCount)
//                .totalCost(0)
//                .totalTransferTime(0)
//                .duration(0)
//                .build();
//    }


//    //todo zmień typ na Trip
//    public void send1GoogleRequest(
//
//            List<PlaceInTrip> placeInTripList, List<Transfer> transferList,
//            String originAddress, String destinationAddress, String departureTime, List<String> prefferedTransport) {
//        GoogleApiClient service = new GoogleApiClient();
//
//
//        try {
//            GoogleRoutesResponse routesResponse = service.fetchRoutes(
//                    originAddress,
//                    destinationAddress,
//                    departureTime,
//                    prefferedTransport
//            );
//
//            if (routesResponse != null && routesResponse.getRoutes() != null) {
//                routesResponse.getRoutes().forEach(route -> {
//                    route.getLegs().forEach(leg -> {
//                        List<GoogleRoutesResponse.Step> stepList = leg.getSteps();
//
//                        List<GoogleRoutesResponse.Step> filteredSteps = stepList.stream()
//                                .filter(step -> step.getTransitDetails() != null)
//                                .toList();
//
//                        for (int i = 0; i < filteredSteps.size(); i++) {
//                            Transfer transfer = convertStepIntoTransfer(filteredSteps.get(i), transferList.size());
//
//                            transferList.add(transfer);
//                            if (i == 0) {
//                                PlaceInTrip placeInTrip = convertStepIntoPlace(filteredSteps.get(i), false, placeInTripList.size());
//                                placeInTripList.add(placeInTrip);
//                            }
//                            if (i == (filteredSteps.size() - 1)) {
//                                PlaceInTrip placeInTrip = convertStepIntoPlace(filteredSteps.get(i), false, placeInTripList.size());
//                                placeInTripList.add(placeInTrip);
//
//                            } else {
//                                PlaceInTrip placeInTrip = convertStepIntoPlace(filteredSteps.get(i), true, placeInTripList.size());
//                                placeInTripList.add(placeInTrip);
//                            }
//                        }
//                    });
//                });
//            } else {
//                System.out.println("No routes found.");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    tego nie potrzbuje
//    @Test
//    void findAirportsInCity() {
//        GoogleApiClient service = new GoogleApiClient();
//        try {
//            GoogleRoutesResponse routesResponse = service.fetchRoutes(
//                    "Wrocław, Polska",
//                    "Złoty Stok, Polska",
//                    "2024-12-05T15:01:23Z",
//                    Arrays.asList("TRAIN", "BUS", "RAIL")
//            );
//
//            // Przykład pracy z odpowiedzią
//            if (routesResponse != null && routesResponse.getRoutes() != null) {
//                routesResponse.getRoutes().forEach(route -> {
//                    route.getLegs().forEach(leg -> {
//                        leg.getSteps().forEach(step -> {
//                            if (step.getTransitDetails() != null) {
//                                System.out.println("Headsign: " + step.getTransitDetails().getHeadsign());
//                                System.out.println("Departure Stop: " + step.getTransitDetails().getStopDetails().getDepartureStop().getName());
//                                System.out.println("Departure Time: " + step.getTransitDetails().getStopDetails().getDepartureTime());
//
//                                System.out.println("Arrival Stop: " + step.getTransitDetails().getStopDetails().getArrivalStop().getName());
//                                System.out.println("Arrival Time: " + step.getTransitDetails().getStopDetails().getArrivalTime());
//                            } else {
//                                System.out.println("Step without transit details found.");
//                            }
//                            System.out.println("-----------------------------------------");
//                        });
//                    });
//                });
//            } else {
//                System.out.println("No routes found.");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
