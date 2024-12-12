package org.dyploma.trip.transfer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.transport.TransportMode;
import org.dyploma.trip.domain.Trip;

import java.time.LocalDateTime;

@Data
@Builder
@Table(name = "Transfer")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "transport_mode")
    private TransportMode transportMode;
    private String carrier;
    @Column(name = "start_date_time")
    private LocalDateTime startDateTime;
    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;
    private int duration;
    private double cost;
    @Column(name = "start_address")
    private String startAddress;
    @Column(name = "end_address")
    private String endAddress;
    @Column(name = "transfer_order")
    private int transferOrder;
}
