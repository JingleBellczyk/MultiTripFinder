package org.dyploma.algorithm.amadeus.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class TravelSegment {
    private String id;
    private String origin;
    private String destination;
    private String departureDate;
    private String departureTime;

    public TravelSegment(String id, String origin, String destination, String departureDate, String departureTime) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
    }
}
