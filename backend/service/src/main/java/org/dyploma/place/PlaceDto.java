package org.dyploma.place;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaceDto {
    private String name;
    private int hoursToSpend;
    private int order;
    private boolean isChange;
}
