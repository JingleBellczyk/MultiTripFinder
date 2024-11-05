package org.dyploma.tag;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dyploma.search.domain.Search;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "search_tag")
public class SearchTag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String tag;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "Tag_Search",
            joinColumns = { @JoinColumn(name = "tag_id") },
            inverseJoinColumns = { @JoinColumn(name = "search_id") }
    )
    private List<Search> searches;
}