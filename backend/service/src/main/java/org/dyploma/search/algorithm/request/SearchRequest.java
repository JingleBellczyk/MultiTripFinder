package org.dyploma.search.algorithm.request;

import lombok.Builder;
import lombok.Data;
import org.dyploma.criteria.CriteriaMode;
import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.transport.TransportMode;

import java.sql.Date;
import java.util.List;

@Data
@Builder
public class SearchRequest {
    private List<PlaceInSearch> placesToVisit;
    private PlaceInSearch startPlace;
    private PlaceInSearch endPlace;
    private int maxTripDuration;
    private Date tripStartDate;
    private int passengerCount;
    private TransportMode preferredTransport;
    private CriteriaMode preferredCriteria;
}
