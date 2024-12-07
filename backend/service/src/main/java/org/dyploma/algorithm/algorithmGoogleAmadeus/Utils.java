package org.dyploma.algorithm.algorithmGoogleAmadeus;

import org.dyploma.airport.Airport;
import org.dyploma.algorithm.externalApi.google.dto.GoogleRoutesResponse;
import org.dyploma.transport.TransportMode;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.trip.transfer.Transfer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

    private static final double EARTH_RADIUS = 6371.0;

    public static <T> List<List<T>> generateAllRecursive(int n, List<T> elements, T start, T end) {
        List<List<T>> permutations = new ArrayList<>();

        if (n == 1) {
            elements.add(0, start);
            elements.add(end);
            permutations.add(new ArrayList<>(elements));
        } else {
            for (int i = 0; i < n - 1; i++) {
                permutations.addAll(generateAllRecursive(n - 1, elements, start, end));
                if (n % 2 == 0) {
                    Collections.swap(elements, i, n - 1);
                } else {
                    Collections.swap(elements, 0, n - 1);
                }
            }
            permutations.addAll(generateAllRecursive(n - 1, elements, start, end)); // Dodajemy końcowe permutacje
        }

        return permutations;
    }

    public static Transfer convertStepIntoTransfer(GoogleRoutesResponse.Step step, int transferOrder) {
        LocalDateTime startDateTime = LocalDateTime.parse(step.getTransitDetails().getStopDetails().getDepartureTime(), DateTimeFormatter.ISO_DATE_TIME);
//                                LocalDate startLocalDate = startDateTime.toLocalDate();

        LocalDateTime endDateTime = LocalDateTime.parse(step.getTransitDetails().getStopDetails().getArrivalTime(), DateTimeFormatter.ISO_DATE_TIME);
        String vehicleType = step.getTransitDetails().getTransitLine().getVehicle().getType();
        //todo konwertuj vehicleType na TransportMode
        TransportMode transportMode = mapToTransportMode(vehicleType);

        //todo carrier czasem jest kilka, na razie biore 0
        String carrier = step.getTransitDetails().getTransitLine().getAgencies().get(0).getName();
        String shortName = step.getTransitDetails().getTransitLine().getNameShort();
        if(shortName != null){
            carrier = String.format("%s, %s", carrier, shortName);
        }

        int minutesDifference = (int) Duration.between(startDateTime, endDateTime).toMinutes();

        Double cost = calculateDistance(
                step.getTransitDetails().getStopDetails().getDepartureStop().getLocation().getLatLng().getLatitude(),
                step.getTransitDetails().getStopDetails().getDepartureStop().getLocation().getLatLng().getLongitude(),
                step.getTransitDetails().getStopDetails().getArrivalStop().getLocation().getLatLng().getLatitude(),
                step.getTransitDetails().getStopDetails().getArrivalStop().getLocation().getLatLng().getLongitude());

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
                .transferOrder(transferOrder)
                .build();
    }

    public static PlaceInTrip convertStepIntoPlace(GoogleRoutesResponse.Step step, Boolean isTransfer, int visitOrder, boolean departureOrArrival, int stayDuration) {
        String city;
        if(departureOrArrival){
            city = step.getTransitDetails().getStopDetails().getDepartureStop().getName();
        }else{
            city = step.getTransitDetails().getStopDetails().getArrivalStop().getName();
        }

        return PlaceInTrip.builder()
                .country("no info")
                .city(city)
                .isTransfer(isTransfer)
                .stayDuration(stayDuration)
                .visitOrder(visitOrder)
                .build();
    }



    public static LocalDateTime convertToLocalDateTime(LocalDate date) {

        return date.atStartOfDay();
    }
    public static String convertToFormattedString(LocalDateTime dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        return dateTime.format(formatter);
    }
    public static LocalDateTime addHoursToDate(LocalDateTime dateTime, long hoursToAdd) {
        // Dodajemy godziny do daty
        return dateTime.plusHours(hoursToAdd);
    }

    public static double calculateDistance(double latStart, double longStart, double latEnd, double longEnd) {

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

    public static TransportMode mapToTransportMode(String transportModeString) {
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

    public static int calculateDurationInHours(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        return (int) Duration.between(startLocalDateTime, endLocalDateTime).toHours();
    }

    public static int calculateDurationInMinutes(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        return (int) Duration.between(startLocalDateTime, endLocalDateTime).toMinutes();
    }
}