package org.dyploma.algorithm.algorithmGoogleAmadeus;

import org.dyploma.algorithm.externalApi.google.dto.GoogleApiClient;
import org.dyploma.algorithm.externalApi.google.dto.GoogleRoutesResponse;
import org.dyploma.transport.TransportMode;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.trip.transfer.Transfer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class GoogleAlgorithmUtils {

    private static final double EARTH_RADIUS = 6371.0;

    public boolean send1GoogleRequest(List<PlaceInTrip> placeInTripList, List<Transfer> transferList,
                                      String cityStart, String countryStart, String cityEnd,
                                      String countryEnd, LocalDateTime date, List<String> prefferedTransport,
                                      int passengerCount, GoogleApiClient googleApiClient) {

        return send1GoogleRequest(placeInTripList,
                transferList,
                cityStart + ", " + countryStart,
                cityEnd + ", " + countryEnd,
                convertToFormattedString(date),
                prefferedTransport,
                passengerCount,
                googleApiClient);
    }

    /**
     * This method sends one request to google
     * and processes response into objects of type: PriceInTrip and Transfer
     * Then adds them to param lists: placeInTripList, transferList
     *
     * @return true if transfer was found and was successfully added to lists
     */
    private boolean send1GoogleRequest(List<PlaceInTrip> placeInTripList, List<Transfer> transferList,
                                       String originAddress, String destinationAddress, String departureTime,
                                       List<String> prefferedTransport, int passengerCount, GoogleApiClient googleApiClient) {

        try {
            GoogleRoutesResponse routesResponse = googleApiClient.fetchRoutes(
                    originAddress,
                    destinationAddress,
                    departureTime,
                    prefferedTransport
            );
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (routesResponse != null && routesResponse.getRoutes() != null) {
                routesResponse.getRoutes().forEach(route -> {
                    route.getLegs().forEach(leg -> {

                        List<GoogleRoutesResponse.Step> stepList = leg.getSteps();

                        //filtering steps without empty values
                        List<GoogleRoutesResponse.Step> filteredSteps = stepList.stream()
                                .filter(step -> step.getTransitDetails() != null)
                                .toList();

                        for (int i = 0; i < filteredSteps.size(); i++) {
                            Transfer transfer = convertStepIntoTransfer(filteredSteps.get(i), transferList.size(), passengerCount);

                            transferList.add(transfer);

                            //transfer places are those, in which there is change of transport

                            //start place
                            if (i == 0 && transferList.size() == 1) {
                                PlaceInTrip placeInTrip = convertStepIntoPlace(filteredSteps.get(i), false, placeInTripList.size(), true, 0);
                                placeInTripList.add(placeInTrip);
                            }

                            if (i == (filteredSteps.size() - 1)) {
                                //places to visit and last place
                                PlaceInTrip placeInTrip = convertStepIntoPlace(filteredSteps.get(i), false, placeInTripList.size(), false, 0);
                                placeInTripList.add(placeInTrip);
                            } else {
                                //changes
                                PlaceInTrip placeInTrip = convertStepIntoPlace(filteredSteps.get(i), true, placeInTripList.size(), false, 0);
                                placeInTripList.add(placeInTrip);
                            }

                            //counting duration of stay in place
                            if (transferList.size() > 1) {
                                int duration = Utils.calculateDurationInMinutes(transferList.get(transferList.size() - 2).getEndDateTime(), transferList.get(transferList.size() - 1).getStartDateTime());
                                placeInTripList.get(placeInTripList.size() - 2).setStayDuration(duration);
                            }
                        }
                    });
                });
            } else {
                System.out.println("No routes found.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * This method is converter of part of Google into Transfer
     *
     * @param step is format of transfer information provided by google
     * @param transferOrder is
     * @return Transfer
     */
    private static Transfer convertStepIntoTransfer(GoogleRoutesResponse.Step step, int transferOrder, int passengerCount) {

        String vehicleType = step.getTransitDetails().getTransitLine().getVehicle().getType();
        TransportMode transportMode = mapToTransportMode(vehicleType);

        String carrier = step.getTransitDetails().getTransitLine().getAgencies().get(0).getName();
        String shortName = step.getTransitDetails().getTransitLine().getNameShort();
        if (shortName != null) {
            carrier = String.format("%s, %s", carrier, shortName);
        }

        LocalDateTime startDateTime = LocalDateTime.parse(step.getTransitDetails().getStopDetails().getDepartureTime(), DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime endDateTime = LocalDateTime.parse(step.getTransitDetails().getStopDetails().getArrivalTime(), DateTimeFormatter.ISO_DATE_TIME);

        int minutesDifference = (int) Duration.between(startDateTime, endDateTime).toMinutes();

        double cost = calculateCost(
                step.getTransitDetails().getStopDetails().getDepartureStop().getLocation().getLatLng().getLatitude(),
                step.getTransitDetails().getStopDetails().getDepartureStop().getLocation().getLatLng().getLongitude(),
                step.getTransitDetails().getStopDetails().getArrivalStop().getLocation().getLatLng().getLatitude(),
                step.getTransitDetails().getStopDetails().getArrivalStop().getLocation().getLatLng().getLongitude());
        cost *= passengerCount;

        String startAddress = step.getTransitDetails().getStopDetails().getDepartureStop().getName();

        String endAddress = step.getTransitDetails().getStopDetails().getArrivalStop().getName();

        return Transfer.builder()
                .transportMode(transportMode)
                .carrier(carrier)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .duration(minutesDifference)
                .cost(cost)
                .startAddress(startAddress)
                .endAddress(endAddress)
                .transferOrder(transferOrder + 1)//order from 1
                .build();
    }

    /**
     * This method is converter of part of Google into place
     *
     * @param step is format of google transfer in response
     * @param isTransfer is a flag if in place is change of transport
     * @param visitOrder is order of visit
     * @param departureOrArrival true means departure, false means arrival
     * @param stayDuration time of beeing in place
     * @return PlaceInTrip
     */
    private static PlaceInTrip convertStepIntoPlace(GoogleRoutesResponse.Step step, Boolean isTransfer, int visitOrder, boolean departureOrArrival, int stayDuration) {

        String city;
        if (departureOrArrival) {
            city = step.getTransitDetails().getStopDetails().getDepartureStop().getName();
        } else {
            city = step.getTransitDetails().getStopDetails().getArrivalStop().getName();
        }

        return PlaceInTrip.builder()
                .country("no info")
                .city(city)
                .isTransfer(isTransfer)
                .stayDuration(stayDuration)
                .visitOrder(visitOrder + 1) //order from 1
                .build();
    }

    /**
     * Estimates the cost of ground transportation based on the distance between two points.
     * This method is used as a fallback when Google does not provide price information.
     * The cost is calculated using a tiered pricing model:
     * - Base fee: 1 euro
     * - 0.50 euro per km for the first 10 km
     * - 0.30 euro per km for the next 90 km (10-100 km)
     * - 0.15 euro per km for distances beyond 100 km
     * <p>
     * The result is rounded to two decimal places.
     */
    private static double calculateCost(double latStart, double longStart, double latEnd, double longEnd) {

        double baseFee = 1;
        double distance = calculateDistance(latStart, longStart, latEnd, longEnd);
        double cost;

        if (distance <= 10) {
            cost = baseFee + distance * 0.50; // 0.50 euro for km
        } else if (distance <= 100) {
            cost = baseFee + (10 * 0.50) + ((distance - 10) * 0.30); // 0.30 euro for km after 10 km
        } else {
            cost = baseFee + (10 * 0.50) + (90 * 0.30) + ((distance - 100) * 0.15); // 0.15 euro for km after 100 km
        }

        return Math.round(cost * 100.0) / 100.0;
    }

    private static double calculateDistance(double latStart, double longStart, double latEnd, double longEnd) {

        double latStartRad = Math.toRadians(latStart);
        double longStartRad = Math.toRadians(longStart);
        double latEndRad = Math.toRadians(latEnd);
        double longEndRad = Math.toRadians(longEnd);

        double deltaLat = latEndRad - latStartRad;
        double deltaLong = longEndRad - longStartRad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(latStartRad) * Math.cos(latEndRad)
                * Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * This method converts google types of transport to type of enum TransportMode
     *
     * @param transportModeString
     * @return TransportMode
     */
    private static TransportMode mapToTransportMode(String transportModeString) {

        if (transportModeString == null) {
            return null;
        }

        return switch (transportModeString) {
            case "HIGH_SPEED_TRAIN", "TRAIN", "RAIL", "HEAVY_RAIL", "LIGHT_RAIL" -> TransportMode.TRAIN;
            case "BUS" -> TransportMode.BUS;
            case "PLANE" -> TransportMode.PLANE;
            default -> null;
        };
    }

    private static String convertToFormattedString(LocalDateTime dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        return dateTime.format(formatter);
    }
}
