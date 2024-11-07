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
@Table(name = "trip_place")
public class PlaceInTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String country;
    private String city;
    private boolean isTransfer;
    private int stayDuration;
    private int visitOrder;
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;
}
