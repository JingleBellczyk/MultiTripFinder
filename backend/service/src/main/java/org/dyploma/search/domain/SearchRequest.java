package org.dyploma.search.domain;

import lombok.Builder;
import lombok.Data;
import org.dyploma.search.criteria.CriteriaMode;
import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.transport.TransportMode;

import java.sql.Date;
import java.util.List;

@Data
@Builder
public class SearchRequest {
    private PlaceInSearch startPlace;
    private PlaceInSearch endPlace;
    private List<PlaceInSearch> placesToVisit;
    private int passengerCount;
    private TransportMode preferredTransport;
    private CriteriaMode optimizationCriteria;
    private Date tripStartDate;
    private int maxTripDuration;
}
