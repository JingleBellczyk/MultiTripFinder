package org.dyploma.search.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.criteria.CriteriaMode;
import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.tag.Tag;
import org.dyploma.transport.TransportMode;

import java.sql.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "search")
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "start_place_id")
    private PlaceInSearch startPlace;

    @ManyToOne
    @JoinColumn(name = "end_place_id")
    private PlaceInSearch endPlace;

    private String name;
    private Date searchDate;
    private int passengerCount;
    private TransportMode preferredTransport;
    private CriteriaMode optimizationCriteria;
    private Date tripStartDate;
    private int maxTripDuration;

    @OneToMany(mappedBy = "search")
    private List<PlaceInSearch> placesToVisit;

    @ManyToMany
    @JoinTable(
            name = "tag_search",
            joinColumns = @JoinColumn(name = "search_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;
}
