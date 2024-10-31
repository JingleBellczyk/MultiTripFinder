package org.dyploma.transfer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.place.Place;
import org.dyploma.transport.TransportType;
import org.dyploma.trip.Trip;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Table(name = "transfer")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    //private Place startPlace;
    private String endPlace;
    private Instant startDate;
    private Instant endDate;
    private String transitLine;
    private TransportType transport;
    private BigDecimal price;
    private Instant duration;
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;
}
