package org.dyploma.noweTesty;

import org.dyploma.algorithm.algorithmGoogleAmadeus.AlgorithmGoogleAmadeusService;
import org.dyploma.search.criteria.CriteriaMode;
import org.dyploma.search.domain.SearchRequest;
import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.transport.TransportMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

//@SpringBootTest(classes = {EngineeringWorkApplication.class})
@SpringBootTest
public class AlgorithmAmadeusGoogleTests {

//    @Autowired
//    private AirportService airportService;

    @Autowired
    private AlgorithmGoogleAmadeusService algorithmGoogleAmadeusService;

    SearchRequest searchRequest;

    @BeforeEach
    void setUp() {
        List<PlaceInSearch> placesToVisit = Arrays.asList(
                PlaceInSearch.builder()
                        .country("Poland")
                        .city("Wroclaw")
                        .stayDuration(0)
                        .entryOrder(0)
                        .build(),
                PlaceInSearch.builder()
                        .country("Germany")
                        .city("Düsseldorf")
                        .stayDuration(72)
                        .entryOrder(2)
                        .build(),
                PlaceInSearch.builder()
                        .country("Germany")
                        .city("Berlin")
                        .stayDuration(108)
                        .entryOrder(2)
                        .build(),
                PlaceInSearch.builder()
                        .country("Poland")
                        .city("Złoty stok")
                        .stayDuration(0)
                        .entryOrder(3)
                        .build()
        );

        // Tworzenie instancji SearchRequest
        this.searchRequest = SearchRequest.builder()
                .placesToVisit(placesToVisit)
                .passengerCount(2)
                .preferredTransport(TransportMode.TRAIN)
                .optimizationCriteria(CriteriaMode.DURATION)
                .tripStartDate(LocalDate.of(2024, 12, 15))
                .maxTripDuration(7)
                .build();
    }

    @Test
    public void test1(){
        System.out.println("---------------- ostateczny wynik -----------------\n" + algorithmGoogleAmadeusService.searchAmadeus(this.searchRequest));
    }
}
