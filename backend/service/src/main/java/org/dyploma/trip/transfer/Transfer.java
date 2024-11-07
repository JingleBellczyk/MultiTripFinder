package org.dyploma.trip.transfer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.transport.TransportMode;
import org.dyploma.trip.domain.Trip;

import java.sql.Timestamp;

@Data
@Builder
@Table(name = "transfer")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    private TransportMode transportMode;
    private String carrier;
    private Timestamp startDateTime;
    private Timestamp endDateTime;
    private int duration;
    private double cost;
    private String startAddress;
    private String endAddress;
    private int transferOrder;
}
