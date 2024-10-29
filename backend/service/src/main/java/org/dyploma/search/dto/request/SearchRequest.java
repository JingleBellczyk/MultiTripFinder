package org.dyploma.search.dto.request;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Builder;
import lombok.Data;
import org.dyploma.criteria.CriteriaType;
import org.dyploma.transport.TransportType;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class SearchRequest {
    private List<PlaceRequest> places;
    private int maxHoursToSpend;
    private Instant startDate;
    private int passengersNumber;
    private TransportType preferredTransport;
    private CriteriaType preferredCriteria;
}
