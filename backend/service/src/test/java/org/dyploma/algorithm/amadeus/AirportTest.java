package org.dyploma.algorithm.amadeus;

import org.dyploma.airport.Airport;
import org.dyploma.airport.AirportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AirportTest {

    @Autowired
    private AirportService airportService;

//    public AirportTest(AirportService airportService) {
//        this.airportService = airportService;
//    }

    @Test
    void findAirportsInCity() {
        testFindAirportsByCity("Poland","Wrocław", 1, "WRO");
        testFindAirportsByCity("United Kingdom", "London", 6, "LON");
        testFindAirportsByCity("Turkey", "Çukurova", 1, "COV");
        testFindAirportsByCity("Germany", "Düsseldorf", 1, "DUS");
    }

    private void testFindAirportsByCity(String country, String city, int expectedSize, String expectedCityCode) {
        Airport airport = airportService.getAirportByCountryAndCity(country, city);

        assertThat(airport.getCityCode()).isEqualTo(expectedCityCode);
        System.out.println(airport);
    }

    @Test
    void findAirportsWithCodePAD() {
        testFindAirportsByAirportCode("PAD", 2, "Germany");
        testFindAirportsByAirportCode("CIA", 1, "Italy");
    }

    private void testFindAirportsByAirportCode(String airportCode, int expectedSize, String expectedCountry) {
        Airport airport = airportService.getAirportByAirportCode(airportCode);

        assertThat(airport.getCountry()).isEqualTo(expectedCountry);
        System.out.println(airport);
    }
}
