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
    private NominatimServiceResponse stationCoordinates;
    private String cityCode;
    private int stayHoursMin;
    private int stayHoursMax;
}
