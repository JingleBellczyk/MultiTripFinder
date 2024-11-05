package org.dyploma.place;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.search.domain.Search;
import org.dyploma.trip.Trip;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private int hoursToSpend;
    private int order;
    private boolean isChange;
    @ManyToOne
    @JoinColumn(name = "search_id")
    private Search search;
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;
}
