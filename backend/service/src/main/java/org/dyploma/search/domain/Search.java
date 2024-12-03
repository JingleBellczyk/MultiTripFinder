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
import org.dyploma.useraccount.UserAccount;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "search", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "user_account_id"})
})
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;
    private String name;
    private LocalDate saveDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    @OneToMany(mappedBy = "search", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("entryOrder ASC")
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
    private LocalDate tripStartDate;
    private int maxTripDuration;

    public void addPlaceToVisit(PlaceInSearch place) {
        if (placesToVisit == null) {
            placesToVisit = new ArrayList<>();
        }
        placesToVisit.add(place);
        place.setSearch(this);
    }
}
