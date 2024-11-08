package org.dyploma.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.place.PlaceDto;
import org.dyploma.transport.TransportType;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
    private PlaceDto startPlace;
    private PlaceDto endPlace;
    private Instant startDate;
    private Instant endDate;
    private String transitLine;
    private TransportType transport;
    private BigDecimal price;
    private int duration;
    private int order;
}
