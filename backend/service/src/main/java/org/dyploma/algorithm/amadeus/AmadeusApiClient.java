package org.dyploma.algorithm.amadeus;

import org.dyploma.algorithm.amadeus.dto.AmadeusRequest;
import org.dyploma.algorithm.amadeus.dto.AmadeusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AmadeusApiClient {

    @Value("${amadeus.api.url}")
    private String apiUrl;

    @Value("${amadeus.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public AmadeusApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public AmadeusResponse sendAmadeusRequest(String originLocationCode, String destinationLocationCode,
                                              String departureDate, String departureTime, int maxFlightOffers) {

        return sendAmadeusRequest(originLocationCode, destinationLocationCode, departureDate, departureTime,
                maxFlightOffers, null, null);
    }

    public AmadeusResponse sendAmadeusRequest(String originLocationCode, String destinationLocationCode,
                                              String departureDate, String departureTime, int maxFlightOffers,
                                              Integer maxPrice, Integer maxFlightTime) {

        AmadeusRequest requestBody = AmadeusRequest.builder()
                .originDestinations(List.of(
                        AmadeusRequest.OriginDestination.builder()
                                .originLocationCode(originLocationCode)
                                .destinationLocationCode(destinationLocationCode)
                                .departureDateTimeRange(
                                        AmadeusRequest.OriginDestination.DepartureDateTimeRange.builder()
                                                .date(departureDate)
                                                .time(departureTime)
                                                .build())
                                .build()))
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

        return sendPostRequest(requestBody);
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
}