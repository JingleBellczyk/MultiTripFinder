package org.dyploma.trip.place;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.trip.domain.Trip;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Trip_Place")
public class PlaceInTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String country;
    private String city;
    @Column(name = "is_transfer")
    private boolean isTransfer;
    @Column(name = "stay_duration")
    private int stayDuration;
    @Column(name = "visit_order")
    private int visitOrder;
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;
}
