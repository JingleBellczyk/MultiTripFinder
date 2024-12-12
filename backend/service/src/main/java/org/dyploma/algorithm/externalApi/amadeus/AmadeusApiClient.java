package org.dyploma.algorithm.externalApi.amadeus;

import org.dyploma.algorithm.algorithmGoogleAmadeus.AmadeusTokenResponse;
import org.dyploma.algorithm.externalApi.amadeus.dto.AmadeusRequest;
import org.dyploma.algorithm.externalApi.amadeus.dto.AmadeusResponse;
import org.dyploma.algorithm.externalApi.amadeus.dto.TravelSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class AmadeusApiClient {

    @Value("${amadeus.api.url}")
    private String apiUrl;

    @Value("${amadeus.client.id}")
    private String clientId;

    @Value("${amadeus.client.secret}")
    private String clientSecret;

    private final String AUTH_URL = "https://test.api.amadeus.com/v1/security/oauth2/token";

    private String apiKey;

    private final RestTemplate restTemplate;

    public AmadeusApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AmadeusResponse sendAmadeusRequest(List<AmadeusRequest.OriginDestination> originDestinations, int maxFlightOffers) {

        return sendAmadeusRequest(originDestinations,
                maxFlightOffers, null, null);
    }

    public AmadeusResponse sendAmadeusRequest(List<AmadeusRequest.OriginDestination> originDestinations,
                                              int maxFlightOffers, Integer maxPrice, Integer maxFlightTime) {

        AmadeusRequest requestBody = AmadeusRequest.builder()
                .originDestinations(originDestinations)
                .searchCriteria(
                        AmadeusRequest.SearchCriteria.builder()
                                .maxFlightOffers(maxFlightOffers)
                                .maxPrice(maxPrice != null ? maxPrice : AmadeusRequest.DEFAULT_MAX_PRICE)
                                .flightFilters(
                                        AmadeusRequest.SearchCriteria.FlightFilters.builder()
                                                .maxFlightTime(maxFlightTime != null ? maxFlightTime : AmadeusRequest.DEFAULT_MAX_FLIGHT_TIME)
                                                .build())
                                .build())
                .build();

        // Wysyłamy zapytanie POST
        return sendPostRequest(requestBody);
    }

    @Scheduled(fixedRate = 1800000)
    public void refreshApiKey() {
        Logger logger = LoggerFactory.getLogger(AmadeusApiClient.class);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String body = "grant_type=client_credentials" +
                    "&client_id=" + clientId +
                    "&client_secret=" + clientSecret;

            HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<AmadeusTokenResponse> response = restTemplate.exchange(
                    AUTH_URL,
                    HttpMethod.POST,
                    requestEntity,
                    AmadeusTokenResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                this.apiKey = response.getBody().getAccess_token();
            } else {
                logger.error("Failed to refresh Amadeus API key: " + response.getStatusCode());
            }
        } catch (Exception ex) {
            logger.error("Error refreshing Amadeus API key: " + ex.getMessage());
        }
    }


    public AmadeusResponse sendPostRequest(AmadeusRequest requestBody) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<AmadeusRequest> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<AmadeusResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                AmadeusResponse.class
        );

        return response.getBody();
    }



    public List<AmadeusRequest.OriginDestination> createOriginDestinations(List<TravelSegment> segments) {
        if(segments.size() > 2){
            return null;
        }

        List<AmadeusRequest.OriginDestination> originDestinations = new ArrayList<>();

        for (TravelSegment segment : segments) {
            originDestinations.add(
                    AmadeusRequest.OriginDestination.builder()
                            .id(segment.getId())
                            .originLocationCode(segment.getOrigin())
                            .destinationLocationCode(segment.getDestination())
                            .departureDateTimeRange(
                                    AmadeusRequest.OriginDestination.DepartureDateTimeRange.builder()
                                            .date(segment.getDepartureDate())
                                            .time(segment.getDepartureTime() + ":00") // Ensure HH:MM:SS format
                                            .build())
                            .build()
            );
        }

        return originDestinations;
    }
}