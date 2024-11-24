package org.dyploma.algorithm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.dyploma.search.criteria.CriteriaMode;
import org.dyploma.transport.TransportMode;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class AlgorithmRequest {
    private PlaceInSearchRequest startPlace;
    private PlaceInSearchRequest endPlace;
    private List<PlaceInSearchRequest> placesToVisit;
    private int passengerCount;
    private Date tripStartDate;
    private int maxTripDuration;
    private TransportMode preferredTransport;
    private CriteriaMode optimizationCriteria;
}
