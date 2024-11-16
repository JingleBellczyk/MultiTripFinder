package org.dyploma.tag.search_tag.domain;

import org.dyploma.useraccount.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SearchTagRepository extends JpaRepository<SearchTag, Integer> {
    List<SearchTag> findByNameInAndUserAccount(List<String> names, UserAccount userAccount);
    List<SearchTag> findByUserAccount(UserAccount userAccount);
    Optional<SearchTag> findByIdAndUserAccount(Integer id, UserAccount userAccount);
    Boolean existsByNameAndUserAccount(String name, UserAccount userAccount);
}
