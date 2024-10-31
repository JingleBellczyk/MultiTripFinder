package org.dyploma.search.dto.response;
import lombok.Builder;
import lombok.Data;
import org.dyploma.place.PlaceDto;
import org.dyploma.transfer.TransferResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class TripResponse {
    private List<PlaceDto> places;
    private String startPlace;
    private String endPlace;
    private Instant startTime;
    private Instant endTime;
    private int totalDuration;
    private int totalTransferDuration;
    private BigDecimal totalPrice;
    private int passengersNumber;
    private List<TransferResponse> transfers;
}
