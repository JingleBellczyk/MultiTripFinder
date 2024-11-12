package org.dyploma.search.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface SearchRepository extends JpaRepository<Search, Integer>, JpaSpecificationExecutor<Search> {
    Boolean existsByName(String name);
    Optional<Search> findByName(String name);
}
