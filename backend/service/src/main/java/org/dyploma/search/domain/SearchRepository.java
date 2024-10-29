package org.dyploma.search.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SearchRepository extends JpaRepository<Search, UUID> {
    Page<Search> findAll(Pageable pageable);
    Optional<Search> findById(UUID id);
}
