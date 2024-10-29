package org.dyploma.search.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaceRequest {
    private String place;
    private int hoursToSpend;
}
