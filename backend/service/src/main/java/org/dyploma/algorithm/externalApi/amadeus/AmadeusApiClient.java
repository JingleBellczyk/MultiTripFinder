package org.dyploma.algorithm.externalApi.amadeus;

import org.dyploma.algorithm.externalApi.amadeus.dto.AmadeusRequest;
import org.dyploma.algorithm.externalApi.amadeus.dto.AmadeusResponse;
import org.dyploma.algorithm.externalApi.amadeus.dto.TravelSegment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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