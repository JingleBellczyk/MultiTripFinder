package org.dyploma.trip.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import org.dyploma.tag.trip_tag.domain.TripTag;
import org.dyploma.useraccount.UserAccount;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;
import java.util.List;

public class TripSpecification {

    public static Specification<Trip> belongsToUserAccount(UserAccount userAccount) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userAccount"), userAccount);
    }

    public static Specification<Trip> withTags(List<String> tags) {
        return (root, query, criteriaBuilder) -> {
            if (tags == null || tags.isEmpty()) {
                return null;
            }
            Join<Trip, TripTag> tagJoin = root.join("tags");
            CriteriaBuilder.In<String> inClause = criteriaBuilder.in(tagJoin.get("name"));
            tags.forEach(inClause::value);
            return inClause;
        };
    }

    public static Specification<Trip> withDateRange(Date fromDate, Date toDate) {
        return (root, query, criteriaBuilder) -> {
            if (fromDate == null && toDate == null) {
                return null;
            }
            if (fromDate != null && toDate != null) {
                return criteriaBuilder.between(root.get("saveDate"), fromDate, toDate);
            } else if (fromDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("saveDate"), fromDate);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(root.get("saveDate"), toDate);
            }
        };
    }

}

