package org.dyploma.airport;

import org.dyploma.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AirportService {

    private final AirportRepository airportRepository;

    @Autowired
    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public Airport getAirportByAirportCode(String airportCode) {
        return airportRepository.findFirstByAirportCode(airportCode).orElseThrow(() -> new NotFoundException("Airport with code " + airportCode + " not found"));
    }

    public Airport getAirportByCountryAndCity(String country, String city) {
        String cityInLatin = NameTransliterator.toLatinAlphabet(city);
        Optional<Airport> airport = airportRepository.findFirstByCountryAndCity(country, cityInLatin);
        return airport.orElse(null);
    }
}
