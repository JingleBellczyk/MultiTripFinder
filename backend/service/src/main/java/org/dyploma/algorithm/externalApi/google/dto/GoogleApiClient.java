package org.dyploma.algorithm.externalApi.google.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GoogleApiClient {
    private static final String API_KEY = "secret-key";
    private static final String BASE_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";

    public GoogleRoutesResponse fetchRoutes(
            String originAddress,
            String destinationAddress,
            String departureTime,
            List<String> allowedTravelModes
    ) throws Exception {

        String requestBody = String.format(
                "{\"origin\":{\"address\":\"%s\"}," +
                        "\"destination\":{\"address\":\"%s\"}," +
                        "\"travelMode\":\"TRANSIT\"," +
                        "\"departureTime\":\"%s\"," +
                        "\"computeAlternativeRoutes\":false," +
                        "\"transitPreferences\":{\"routingPreference\":\"LESS_WALKING\",\"allowedTravelModes\":%s}}",
                originAddress,
                destinationAddress,
                departureTime,
                new ObjectMapper().writeValueAsString(allowedTravelModes)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", API_KEY)
                .header("X-Goog-FieldMask", "routes.legs.steps.transitDetails")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), GoogleRoutesResponse.class);
    }

//    public static void main(String[] args) {
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
