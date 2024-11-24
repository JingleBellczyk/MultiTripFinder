package org.dyploma.algorithm.externalApi.nominatim;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Objects;

@Service
public class NominatimService {
    private final RestTemplate restTemplate;
    private final String apiRequestPrefix = "https://nominatim.openstreetmap.org/search?q=";
    private final String apiRequestSuffix = "&format=json&limit=1";
    private final String railwayStationRequest = "railway+station";
    private final String busStationRequest = "bus+station";

    public NominatimService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NominatimServiceResponse getBusAndRailwayStationCoordinates(NominatimServiceRequest request) {
        String cityRequest = apiRequestPrefix + request.getCountry() + "+" + request.getCity();
        String railwayStationFullRequest = cityRequest + "+" + railwayStationRequest + apiRequestSuffix;
        String busStationFullRequest = cityRequest + "+" + busStationRequest + apiRequestSuffix;
        NominatimApiResponse railwayStationCoordinates = sendRequest(railwayStationFullRequest);
        NominatimApiResponse busStationCoordinates = sendRequest(busStationFullRequest);
        return NominatimServiceResponse.builder()
                .railwayStationCoordinates(railwayStationCoordinates)
                .busStationCoordinates(busStationCoordinates)
                .build();
    }

    private NominatimApiResponse sendRequest(String request) {
        NominatimApiResponse[] response = (Objects.requireNonNull(restTemplate.getForObject(request, NominatimApiResponse[].class)));
        return Arrays.stream(response)
                .findFirst()
                .orElse(null);
    }
}
