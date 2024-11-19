package org.dyploma.airport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportService {

    private final AirportRepository airportRepository;

    @Autowired
    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public List<Airport> getAirportsByCity(String city) {
        String cityInLatin = NameTransliterator.toLatinAlphabet(city);
        return airportRepository.findByCity(cityInLatin);
    }

    public List<Airport> getAirportsByCode(String airportCode) {
        return airportRepository.findByAirportCode(airportCode);
    }
}
