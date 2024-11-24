package org.dyploma.algorithm.externalApi.nominatim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NominatimServiceResponse {
    private NominatimApiResponse railwayStationCoordinates;
    private NominatimApiResponse busStationCoordinates;
}
