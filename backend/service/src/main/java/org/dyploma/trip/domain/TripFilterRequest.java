package org.dyploma.trip.domain;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@Builder
public class TripFilterRequest {
    private List<String> tags;
    private Date fromDate;
    private Date toDate;
}
