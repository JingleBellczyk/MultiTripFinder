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
@Table(name = "transfer")
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

    private TransportMode transportMode;
    private String carrier;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int duration;
    private double cost;
    private String startAddress;
    private String endAddress;
    private int transferOrder;
}
