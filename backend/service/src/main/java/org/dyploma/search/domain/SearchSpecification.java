package org.dyploma.search.domain;

import jakarta.persistence.criteria.Join;
import org.dyploma.tag.search_tag.domain.SearchTag;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class SearchSpecification {

    public static Specification<Search> hasTags(List<String> tagNames) {
        return (root, query, builder) -> {
            if (tagNames == null || tagNames.isEmpty()) {
                return null;
            }
            Join<Search, SearchTag> tags = root.join("tags");
            return tags.get("name").in(tagNames);
        };
    }

    public static Specification<Search> hasCreationDate(LocalDate creationDate) {
        return (root, query, builder) -> creationDate == null ? null : builder.equal(root.get("creationDate"), creationDate);
    }

    public static Specification<Search> hasTransport(String transport) {
        return (root, query, builder) -> transport == null ? null : builder.equal(root.get("transport"), transport);
    }

    public static Specification<Search> hasPreferredCriteria(String preferredCriteria) {
        return (root, query, builder) -> preferredCriteria == null ? null : builder.equal(root.get("preferredCriteria"), preferredCriteria);
    }
}
