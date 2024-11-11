package org.dyploma.tag.search_tag.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchTagRepository extends JpaRepository<SearchTag, Integer> {
    List<SearchTag> findByNameIn(List<String> names);
}
