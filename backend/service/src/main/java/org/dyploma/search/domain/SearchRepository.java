package org.dyploma.search.domain;

import org.dyploma.useraccount.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface SearchRepository extends JpaRepository<Search, Integer>, JpaSpecificationExecutor<Search> {
    Optional<Search> findByIdAndUserAccount(Integer id, UserAccount userAccount);
    Boolean existsByNameAndUserAccount(String name, UserAccount userAccount);
    Optional<Search> findByNameAndUserAccount(String name, UserAccount userAccount);
    Boolean existsByIdAndUserAccount(Integer id, UserAccount userAccount);
}
