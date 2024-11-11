package org.dyploma.search.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.search.criteria.CriteriaMode;
import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.tag.search_tag.domain.SearchTag;
import org.dyploma.transport.TransportMode;

import java.sql.Date;
import java.util.ArrayList;
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

    private Integer id;
    private String name;
    private Date saveDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "start_place_id", referencedColumnName = "id")
    private PlaceInSearch startPlace;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "end_place_id", referencedColumnName = "id")
    private PlaceInSearch endPlace;

    @OneToMany(mappedBy = "search", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaceInSearch> placesToVisit;

    @ManyToMany
    @JoinTable(
            name = "search_search_tag",
            joinColumns = @JoinColumn(name = "search_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<SearchTag> tags;

    private int passengerCount;
    private TransportMode preferredTransport;
    private CriteriaMode optimizationCriteria;
    private Date tripStartDate;
    private int maxTripDuration;

    public void addPlaceToVisit(PlaceInSearch place) {
        if (placesToVisit == null) {
            placesToVisit = new ArrayList<>();
        }
        placesToVisit.add(place);
        place.setSearch(this);
    }
}
