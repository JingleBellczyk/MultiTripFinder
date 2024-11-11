package org.dyploma.tag.trip_tag.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripTagRepository extends JpaRepository<TripTag, Integer> {
    List<TripTag> findByNameIn(List<String> names);
}
