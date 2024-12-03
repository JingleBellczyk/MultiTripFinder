package org.dyploma.search.domain;

import lombok.Builder;
import lombok.Data;
import org.dyploma.search.criteria.CriteriaMode;
import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.transport.TransportMode;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class SearchRequest {
    private List<PlaceInSearch> placesToVisit;
    private int passengerCount;
    private TransportMode preferredTransport;
    private CriteriaMode optimizationCriteria;
    private LocalDate tripStartDate;
    private int maxTripDuration;
}
