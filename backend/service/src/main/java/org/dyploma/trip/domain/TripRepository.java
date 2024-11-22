package org.dyploma.trip.domain;

import org.dyploma.trip.domain.Trip;
import org.dyploma.useraccount.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TripRepository extends JpaRepository<Trip, Integer>, JpaSpecificationExecutor<Trip> {
    Optional<Trip> findByIdAndUserAccount(Integer id, UserAccount userAccount);
    Boolean existsByNameAndUserAccount(String name, UserAccount userAccount);
    Optional<Trip> findByNameAndUserAccount(String name, UserAccount userAccount);
    Boolean existsByIdAndUserAccount(Integer id, UserAccount userAccount);
    @Query("SELECT s.name FROM Trip s WHERE s.userAccount = :userAccount ORDER BY s.name ASC")
    List<String> findAllNamesByUserAccount(@Param("userAccount") UserAccount userAccount);
}
