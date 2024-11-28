package org.dyploma.airport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Integer> {
    Optional<Airport> findFirstByCountryAndCity(String country,  String city);
    Optional<Airport> findFirstByAirportCode(String airportCode);
}