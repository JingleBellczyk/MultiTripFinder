package org.dyploma.algorithm.algorithmGoogleAmadeus;

import org.dyploma.airport.Airport;
import org.dyploma.airport.AirportService;
import org.dyploma.algorithm.algorithmGoogleAmadeus.comparators.SolutionComparator;
import org.dyploma.algorithm.algorithmGoogleAmadeus.comparators.factory.SolutionComparatorFactory;
import org.dyploma.algorithm.externalApi.amadeus.AmadeusApiClient;
import org.dyploma.algorithm.externalApi.google.dto.GoogleApiClient;
import org.dyploma.search.criteria.CriteriaMode;
import org.dyploma.search.domain.SearchRequest;
import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.transport.TransportMode;
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
import java.util.PriorityQueue;

@Service
public class AlgorithmGoogleAmadeusService {

    private final AirportService airportService;

    private final AmadeusApiClient amadeusApiClient;

    private final GoogleApiClient googleApiClient;

    @Autowired
    public AlgorithmGoogleAmadeusService(AirportService airportService, AmadeusApiClient amadeusApiClient, GoogleApiClient googleApiClient) {
        this.airportService = airportService;
        this.amadeusApiClient = amadeusApiClient;
        this.googleApiClient = googleApiClient;
    }

    public List<Trip> search(SearchRequest searchRequest) {

        List<PlacesWithAirport> placesWithAirportList = prepareListOfPlacesAndAirport(searchRequest);

        int passengerCount = searchRequest.getPassengerCount();
        TransportMode preferredTransport = searchRequest.getPreferredTransport();
        CriteriaMode optimizationCriteria = searchRequest.getOptimizationCriteria();
        LocalDate startDate = searchRequest.getTripStartDate();
        int maxTripDurationHours = searchRequest.getMaxTripDuration();

        try {
            return findBestTrips(placesWithAirportList, startDate, preferredTransport, optimizationCriteria, passengerCount, maxTripDurationHours);
        } catch (InterruptedException e) {
            return null;
        }
    }

    private List<Trip> findBestTrips(List<PlacesWithAirport> placesWithAirportList, LocalDate localDate,
                                     TransportMode preferredTransport, CriteriaMode optimizationCriteria,
                                     int passengerCount, int maxTripDurationHours) throws InterruptedException {

        GoogleAlgorithmUtils googleAlgorithmUtils = new GoogleAlgorithmUtils();
        AmadeusAlgorithmUtils amadeusAlgorithmUtils = new AmadeusAlgorithmUtils(airportService, amadeusApiClient);

        SolutionComparator comparator = SolutionComparatorFactory.getComparator(optimizationCriteria);
        List<List<PlacesWithAirport>> permutations = Utils.permutePlacesWithAirport(placesWithAirportList);
        int maxTripDurationMinutes = maxTripDurationHours * 60;
        LocalDateTime startDateOriginal = Utils.convertToLocalDateTime(localDate);

        PriorityQueue<Solution> bestSolutionsQueue = new PriorityQueue<>(comparator);

        for (List<PlacesWithAirport> permutation : permutations) {

            System.out.println("Permutation " + permutation);

            LocalDateTime startDate = startDateOriginal;

            List<PlaceInTrip> placeInTripList = new ArrayList<>();
            List<Transfer> transferList = new ArrayList<>();

            int i = 0;
            boolean continuePermutationChecking = true;

            while (i < permutation.size() - 1 && continuePermutationChecking) {
                boolean transferWasFound = true;

                Airport airportStart = permutation.get(i).getAirport();
                Airport airportEnd = permutation.get(i + 1).getAirport();

                if (preferredTransport == TransportMode.PLANE || preferredTransport == null) {
                    if (airportStart != null && airportEnd != null) {
                        transferWasFound = amadeusAlgorithmUtils.send1AmadeusRequest(
                                placeInTripList,
                                transferList,
                                startDate,
                                airportStart,
                                airportEnd,
                                passengerCount
                        );
                    } else {
                        transferWasFound = false;
                    }

                    if (!transferWasFound) {
                        transferWasFound = googleAlgorithmUtils.send1GoogleRequest(
                                placeInTripList,
                                transferList,
                                permutation.get(i).getCity(),
                                permutation.get(i).getCountry(),
                                permutation.get(i + 1).getCity(),
                                permutation.get(i + 1).getCountry(),
                                startDate,
                                Arrays.asList("TRAIN", "BUS", "RAIL"),
                                passengerCount,
                                googleApiClient
                        );
                    }
                } else {
                    transferWasFound = googleAlgorithmUtils.send1GoogleRequest(
                            placeInTripList,
                            transferList,
                            permutation.get(i).getCity(),
                            permutation.get(i).getCountry(),
                            permutation.get(i + 1).getCity(),
                            permutation.get(i + 1).getCountry(),
                            startDate,
                            Arrays.asList("TRAIN", "BUS", "RAIL"),
                            passengerCount,
                            googleApiClient
                    );

                    if (!transferWasFound && airportStart != null && airportEnd != null) {
                        transferWasFound = amadeusAlgorithmUtils.send1AmadeusRequest(
                                placeInTripList,
                                transferList,
                                startDate,
                                airportStart,
                                airportEnd,
                                passengerCount
                        );
                    }
                }

                //checking pruning conditions(if trip is promising)
                if (transferWasFound) {

                    //checking if current trip is longer than maximum total time
                    if (countTotalDuration(placeInTripList, transferList) <= maxTripDurationMinutes) {

                        /*
                            we want only 5 best solutions, therefore current solution has to be
                            compared to current worst solution from 5 best solutions(if they exist)
                         */
                        if (bestSolutionsQueue.size() >= 5) {

                            //comparing current solution, depending on optimizationCriteria
                            if (optimizationCriteria.equals(CriteriaMode.DURATION)) {

                                if (Utils.getCurrentWorstTransferTime(bestSolutionsQueue) < countTotalTransferTime(transferList)) {
                                    continuePermutationChecking = false;
                                }
                            } else {

                                if (Utils.getCurrentWorstCost(bestSolutionsQueue) < countTotalCost(transferList)) {
                                    continuePermutationChecking = false;
                                }
                            }
                        }
                    } else {
                        continuePermutationChecking = false;
                    }

                    //count next start date: (next start date) = (end date of last transport) + (duration of stay in this place)
                    LocalDateTime endDateTime = transferList.get(transferList.size() - 1).getEndDateTime();
                    int stayDuration = permutation.get(i + 1).getStayDuration();
                    startDate = Utils.addHoursToDate(endDateTime, stayDuration);
                } else {
                    continuePermutationChecking = false;
                }

                i++;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (continuePermutationChecking) {
                Trip trip = prepareTrip(placeInTripList, transferList, passengerCount);
                Utils.addSolution(bestSolutionsQueue, new Solution(trip.getTotalTransferTime(), trip.getTotalCost(), trip, comparator));
            }
        }
        return Utils.getTripsFromQueue(bestSolutionsQueue);
    }

    /**
     * preparing Trip object, that will be added to solutions list
     */
    private Trip prepareTrip(List<PlaceInTrip> placeInTripList, List<Transfer> transferList, int passengerCount) {

        LocalDateTime startLocalDateTime = transferList.get(0).getStartDateTime();
        LocalDateTime endLocalDateTime = transferList.get(transferList.size() - 1).getEndDateTime();

        LocalDate startLocalDate = startLocalDateTime.toLocalDate();
        LocalDate endLocalDate = endLocalDateTime.toLocalDate();

        double totalCost = countTotalCost(transferList);

        int totalTransferTime = countTotalTransferTime(transferList);

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

    /**
     * Prepares a list of objects of type PlacesWithAirport, used in generating permutations.
     * The method searches for airports in the provided places (if they exist) and associates
     * the airports with the corresponding places.
     *
     * @param searchRequest
     * @return
     */
    private List<PlacesWithAirport> prepareListOfPlacesAndAirport(SearchRequest searchRequest) {
        List<PlacesWithAirport> placesWithAirportList = new ArrayList<>();

        for (PlaceInSearch placeInSearch : searchRequest.getPlacesToVisit()) {
            Airport airport = airportService.getAirportByCountryAndCity(placeInSearch.getCountry(), placeInSearch.getCity());
            placesWithAirportList.add(new PlacesWithAirport(placeInSearch.getCountry(), placeInSearch.getCity(), placeInSearch.getStayDuration(), placeInSearch.getEntryOrder(), airport));
        }

        return placesWithAirportList;
    }

    //in euro
    private double countTotalCost(List<Transfer> transferList) {
        return transferList.stream()
                .mapToDouble(Transfer::getCost)
                .sum();
    }

    // in minutes
    private int countTotalTransferTime(List<Transfer> transferList) {
        return transferList.stream()
                .mapToInt(Transfer::getDuration)
                .sum();
    }

    // in minutes
    private int countTotalDuration(List<PlaceInTrip> placeInTripList, List<Transfer> transferList) {
        LocalDateTime startLocalDateTime = transferList.get(0).getStartDateTime();
        LocalDateTime endLocalDateTime = transferList.get(transferList.size() - 1).getEndDateTime();

        return Utils.calculateDurationInMinutes(startLocalDateTime, endLocalDateTime);
    }
}
