package org.dyploma.algorithm.amadeus;

import org.dyploma.airport.Airport;
import org.dyploma.airport.AirportRepository;
import org.dyploma.airport.AirportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@SpringBootTest
public class AirportTest {

/*    @Autowired
    private AirportService airportService;

    @Test
    void findAirportsInCity() {
        testFindAirportsByCity("Wrocław", 1, "WRO");
        testFindAirportsByCity("London", 6, "LON");
        testFindAirportsByCity("Çukurova", 1, "COV");
        testFindAirportsByCity("Düsseldorf", 1, "DUS");
    }

    private void testFindAirportsByCity(String city, int expectedSize, String expectedCityCode) {
        List<Airport> airports = airportService.getAirportsByCity(city);

        assertThat(airports).hasSize(expectedSize);
        assertThat(airports.get(0).getCityCode()).isEqualTo(expectedCityCode);
        System.out.println(airports);
    }

    @Test
    void findAirportsWithCodePAD() {
        testFindAirportsByAirportCode("PAD", 2, "Germany");
        testFindAirportsByAirportCode("CIA", 1, "Italy");
    }

    private void testFindAirportsByAirportCode(String airportCode, int expectedSize, String expectedCountry) {
        List<Airport> airports = airportService.getAirportsByCode(airportCode);

        assertThat(airports).hasSize(expectedSize);
        assertThat(airports.get(0).getCountry()).isEqualTo(expectedCountry);
        System.out.println(airports);
    }*/
}
