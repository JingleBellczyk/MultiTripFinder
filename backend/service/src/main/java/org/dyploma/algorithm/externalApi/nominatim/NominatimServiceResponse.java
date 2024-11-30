package org.dyploma.algorithm.externalApi.nominatim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NominatimServiceResponse {
    private NominatimApiResponse railway_station_coordinates;
    private NominatimApiResponse bus_station_coordinates;
}
