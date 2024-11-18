package org.dyploma.tag.trip_tag.domain;

import org.dyploma.useraccount.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripTagRepository extends JpaRepository<TripTag, Integer> {
    List<TripTag> findByNameInAndUserAccount(List<String> names, UserAccount userAccount);
    List<TripTag> findByUserAccount(UserAccount userAccount);
    Optional<TripTag> findByIdAndUserAccount(Integer id, UserAccount userAccount);
    Boolean existsByNameAndUserAccount(String name, UserAccount userAccount);
}
