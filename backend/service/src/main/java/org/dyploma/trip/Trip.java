package org.dyploma.trip;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.search.dto.response.Transfer;
import org.dyploma.search.dto.response.TripResponse;
import org.dyploma.tag.TripTag;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "trip")
@AllArgsConstructor
@NoArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToMany(mappedBy = "trips")
    private List<TripTag> tags;

    private Instant totalTransferDuration;

    private Instant totalDuration;

    private BigDecimal totalPrice;

    private int passengersNumber;

    @ElementCollection
    @CollectionTable(name = "trip_places", joinColumns = @JoinColumn(name = "trip_id"))
    private List<String> places;

    @OneToMany(mappedBy="trip")
    private List<Transfer> transfers;

/*    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;*/
}
