package org.dyploma.search.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SearchRepository extends JpaRepository<Search, Integer> {
    Page<Search> findAll(Pageable pageable);
    @Query("SELECT s FROM Search s "
            + "LEFT JOIN FETCH s.startPlace "
            + "LEFT JOIN FETCH s.endPlace "
            + "LEFT JOIN FETCH s.placesToVisit "
            + "WHERE s.name = :name")
    Optional<Search> findByNameWithPlaces(@Param("name") String name);

    @Query("SELECT s FROM Search s "
            + "LEFT JOIN FETCH s.tags "
            + "WHERE s IN :searches")
    List<Search> findWithTags(@Param("searches") List<Search> searches);

    //Optional<Search> findByName(String name);
    Boolean existsByName(String name);
}
