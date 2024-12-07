package org.dyploma.algorithm.algorithmGoogleAmadeus;

import org.dyploma.airport.Airport;
import org.dyploma.airport.AirportService;
import org.dyploma.algorithm.externalApi.amadeus.AmadeusApiClient;
import org.dyploma.search.domain.SearchRequest;
import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.trip.domain.Trip;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.trip.transfer.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AlgorithmGoogleAmadeusService {

    private final AirportService airportService;

    private final AmadeusApiClient amadeusApiClient;

    @Autowired
    public AlgorithmGoogleAmadeusService(AirportService airportService, AmadeusApiClient amadeusApiClient) {
        this.airportService = airportService;
        this.amadeusApiClient = amadeusApiClient;
    }

    public List<Trip> searchAmadeus(SearchRequest searchRequest) {
        List<PlacesWithAirport> placesWithAirportList = prepareListOfPlacesAndAirport(searchRequest);
        LocalDate startDate = searchRequest.getTripStartDate();
        List<Trip> tripList = new ArrayList<>();

        List<List<PlacesWithAirport>> permutations = permutePlacesWithAirport(placesWithAirportList);
        List<PlacesWithAirport> permutation = permutations.get(0);

        AmadeusAlgorithmUtils amadeusAlgorithmUtils = new AmadeusAlgorithmUtils(airportService, amadeusApiClient);

        List<PlaceInTrip> placeInTripList = new ArrayList<>();
        List<Transfer> transferList = new ArrayList<>();

        amadeusAlgorithmUtils.processRequest(placeInTripList, transferList, Utils.convertToLocalDateTime(startDate),
                permutation.get(0).getAirport(), permutation.get(1).getAirport());
        return null;
    }













    public List<Trip> search(SearchRequest searchRequest) {
        List<PlacesWithAirport> placesWithAirportList = prepareListOfPlacesAndAirport(searchRequest);
        LocalDate startDate = searchRequest.getTripStartDate();
        List<Trip> tripList = new ArrayList<>();
        tripList.add(multipleTrips(placesWithAirportList, startDate));
        return tripList;
    }

    public List<PlacesWithAirport> prepareListOfPlacesAndAirport(SearchRequest searchRequest) {
        List<PlacesWithAirport> placesWithAirportList = new ArrayList<>();

        for (PlaceInSearch placeInSearch : searchRequest.getPlacesToVisit()) {
            Airport airport = airportService.getAirportByCountryAndCity(placeInSearch.getCountry(), placeInSearch.getCity());
            placesWithAirportList.add(new PlacesWithAirport(placeInSearch.getCountry(), placeInSearch.getCity(), placeInSearch.getStayDuration(), placeInSearch.getEntryOrder(), airport));
        }

        System.out.println(placesWithAirportList.toString());
        System.out.println("-----------------------------");
        return placesWithAirportList;
    }

    public List<List<PlacesWithAirport>> permutePlacesWithAirport(List<PlacesWithAirport> placesWithAirportTable) {

        if (placesWithAirportTable.size() == 2) {
            List<List<PlacesWithAirport>> permutations = new ArrayList<>();
            permutations.add(placesWithAirportTable);
            return permutations;
        }

        PlacesWithAirport startPlace = placesWithAirportTable.get(0);
        PlacesWithAirport endPlace = placesWithAirportTable.get(placesWithAirportTable.size() - 1);
        List<PlacesWithAirport> middleSection = placesWithAirportTable.subList(1, placesWithAirportTable.size() - 1);

        return Utils.generateAllRecursive(middleSection.size(), middleSection, startPlace, endPlace);
    }

    public Trip multipleTrips(List<PlacesWithAirport> placesWithAirportList, LocalDate localDate) {
        GoogleAlgorithmUtils googleAlgorithmUtils = new GoogleAlgorithmUtils();
        List<PlaceInTrip> placeInTripList = new ArrayList<>();
        List<Transfer> transferList = new ArrayList<>();

        List<List<PlacesWithAirport>> permutations = permutePlacesWithAirport(placesWithAirportList);
        List<PlacesWithAirport> permutation = permutations.get(0);

        LocalDateTime startDate = Utils.convertToLocalDateTime(localDate);

        for (int i = 0; i < permutation.size() - 1; i++) {
            googleAlgorithmUtils.send1GoogleRequest(placeInTripList,
                    transferList,
                    permutation.get(i).getCity(),
                    permutation.get(i).getCountry(),
                    permutation.get(i + 1).getCity(),
                    permutation.get(i + 1).getCountry(),
                    startDate,
                    Arrays.asList("TRAIN", "BUS", "RAIL")
            );


            LocalDateTime endDateTime = transferList.get(transferList.size() - 1).getEndDateTime();
            int stayDuration = permutation.get(i + 1).getStayDuration();
            startDate = Utils.addHoursToDate(endDateTime, stayDuration);
            System.out.println("-----------start date------------------");
            System.out.println(startDate);
        }
        System.out.println("-----------------------------");
        System.out.println(placeInTripList);
        System.out.println(transferList);
        System.out.println("-----------------------------");

        return prepareTrip(placeInTripList, transferList);
    }
    public Trip prepareTrip(List<PlaceInTrip> placeInTripList, List<Transfer> transferList){
        //        todo passengersCount
        int passengerCount = 1;

        LocalDateTime startLocalDateTime = transferList.get(0).getStartDateTime();
        LocalDateTime endLocalDateTime = transferList.get(transferList.size() - 1).getEndDateTime();

        LocalDate startLocalDate = startLocalDateTime.toLocalDate();
        LocalDate endLocalDate = endLocalDateTime.toLocalDate();

        Double totalCost = transferList.stream()
                .mapToDouble(Transfer::getCost)
                .sum();

        int totalTransferTime = transferList.stream()
                .mapToInt(Transfer::getDuration)
                .sum();

        int totalDuration = Utils.calculateDurationInHours(startLocalDateTime, endLocalDateTime);

        return Trip.builder()
                .name(null)
                .saveDate(null)
                .tags(null)
                .transfers(transferList)
                .places(placeInTripList)
                .startDate(startLocalDate)
                .endDate(endLocalDate)
                .passengerCount(passengerCount)
                .totalCost(totalCost)
                .totalTransferTime(totalTransferTime)
                .duration(totalDuration)
                .build();
    }

}
