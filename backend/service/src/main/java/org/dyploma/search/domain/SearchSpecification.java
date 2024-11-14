package org.dyploma.search.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import org.dyploma.search.criteria.CriteriaMode;
import org.dyploma.tag.search_tag.domain.SearchTag;
import org.dyploma.transport.TransportMode;
import org.dyploma.useraccount.UserAccount;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class SearchSpecification {

    public static Specification<Search> belongsToUserAccount(UserAccount userAccount) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userAccount"), userAccount);
    }

    public static Specification<Search> withOptimizationCriteria(CriteriaMode criteria) {
        return (root, query, criteriaBuilder) ->
                criteria != null ? criteriaBuilder.equal(root.get("optimizationCriteria"), criteria) : null;
    }

    public static Specification<Search> withTransportModes(List<TransportMode> transports) {
        return (root, query, criteriaBuilder) -> {
            if (transports == null || transports.isEmpty()) {
                return null;
            }
            return root.get("preferredTransport").in(transports);
        };
    }


    public static Specification<Search> withSaveDate(Date date) {
        return (root, query, criteriaBuilder) ->
                date != null ? criteriaBuilder.equal(root.get("saveDate"), date) : null;
    }

    public static Specification<Search> withTags(List<String> tags) {
        return (root, query, criteriaBuilder) -> {
            if (tags == null || tags.isEmpty()) {
                return null;
            }
            Join<Search, SearchTag> tagJoin = root.join("tags");
            CriteriaBuilder.In<String> inClause = criteriaBuilder.in(tagJoin.get("name"));
            tags.forEach(inClause::value);
            return inClause;
        };
    }
}

