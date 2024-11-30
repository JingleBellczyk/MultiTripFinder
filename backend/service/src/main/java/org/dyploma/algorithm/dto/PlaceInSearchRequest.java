package org.dyploma.algorithm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.dyploma.algorithm.externalApi.nominatim.NominatimServiceResponse;

@Data
@Builder
@AllArgsConstructor
public class PlaceInSearchRequest {
    private String country;
    private String city;
    private NominatimServiceResponse station_coordinates;
    private String city_code;
    private int stay_hours_min;
    private int stay_hours_max;
}
