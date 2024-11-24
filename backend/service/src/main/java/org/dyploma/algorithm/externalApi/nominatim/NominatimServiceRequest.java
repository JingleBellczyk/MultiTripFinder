package org.dyploma.algorithm.externalApi.nominatim;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NominatimServiceRequest {
    private String country;
    private String city;
}
