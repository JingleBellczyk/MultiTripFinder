package org.dyploma.algorithm.externalApi.google.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class GoogleApiClient {

    @Value("${googleTrips.api.key}")
    private String API_KEY;

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
}
