package org.dyploma.search.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.criteria.CriteriaType;
import org.dyploma.place.Place;
import org.dyploma.tag.SearchTag;
import org.dyploma.transport.TransportType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "search")
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private Instant creationDate;
    @ManyToMany
    @JoinTable(
            name = "search_tag",
            joinColumns = @JoinColumn(name = "search_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<SearchTag> tags;
    @OneToMany(mappedBy = "search")
    private List<Place> placesToVisit;
    private String startPlace;
    private String endPlace;
    private int maxHoursToSpend;
    private Instant startDate;
    private int passengersNumber;
    private TransportType preferredTransport;
    private CriteriaType preferredCriteria;
}
