package org.dyploma.algorithm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.dyploma.search.criteria.CriteriaMode;
import org.dyploma.transport.TransportMode;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class AlgorithmRequest {
    private PlaceInSearchRequest start_place;
    private PlaceInSearchRequest end_place;
    private List<PlaceInSearchRequest> places_to_visit;
    private int passenger_count;
    private String trip_start_date;
    private int max_trip_duration;
    private TransportMode preferred_transport;
    private CriteriaMode optimization_criteria;
}
