package org.dyploma.search.dto.response;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class TripResponse {
    private Instant totalTransferDuration;
    private Instant totalDuration;
    private BigDecimal totalPrice;
    private int passengersNumber;
    private List<String> places;
    private List<Transfer> transfers;
}
