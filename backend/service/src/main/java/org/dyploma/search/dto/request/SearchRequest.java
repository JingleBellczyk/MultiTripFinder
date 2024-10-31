package org.dyploma.search.dto.request;

import lombok.Builder;
import lombok.Data;
import org.dyploma.criteria.CriteriaType;
import org.dyploma.place.PlaceDto;
import org.dyploma.transport.TransportType;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class SearchRequest {
    private List<PlaceDto> placesToVisit;
    private String startPlace;
    private String endPlace;
    private int maxHoursToSpend;
    private Instant startDate;
    private int passengersNumber;
    private TransportType preferredTransport;
    private CriteriaType preferredCriteria;
}
