package org.dyploma.airport;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.dyploma.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Service
public class AirportService {

    private final AirportRepository airportRepository;

    @PostConstruct
    public void init() {
        // Check if the table is empty before loading data
        if (airportRepository.count() == 0) {
            try {
                loadAirportsFromCSV();
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
    }

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

    public void loadAirportsFromCSV() throws IOException, CsvException {
        Resource resource = new ClassPathResource("airports.csv");
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(resource.getInputStream()))) {
            List<String[]> rows = csvReader.readAll();

            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                Airport airport = new Airport();
                airport.setAirportCode(row[0]);
                airport.setCityCode(row[1]);
                airport.setAirportName(row[2]);
                airport.setCity(row[3]);
                airport.setCountry(row[4]);
                airportRepository.save(airport);
            }
        }
    }
}
