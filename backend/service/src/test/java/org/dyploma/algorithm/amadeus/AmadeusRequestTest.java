package org.dyploma.algorithm.amadeus;

import org.dyploma.algorithm.amadeus.AmadeusApiClient;
import org.dyploma.algorithm.amadeus.dto.AmadeusResponse;
import org.dyploma.exception.AmadeusErrorException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AmadeusRequestTest {

    @Autowired
    private AmadeusApiClient amadeusApiClient;

    @Test
    void sendNormalAmadeusRequest(){

        String origin = "WRO";
        String destination = "LON";
        String date = "2024-11-25";
        String time = "10:00:00";
        int maxFlightOffers = 3;

        AmadeusResponse response = amadeusApiClient.sendAmadeusRequest(origin, destination, date, time, maxFlightOffers);

        System.out.println(response);
    }

    @Test
    void sendAmadeusRequestPriceTime(){

        String origin = "WRO";
        String destination = "LON";
        String date = "2024-11-25";
        String time = "10:00:00";
        int maxFlightOffers = 3;

        int maxPrice = 10;
        int maxFlightTime = 200;

        AmadeusResponse response = amadeusApiClient.sendAmadeusRequest(origin, destination, date, time, maxFlightOffers, maxPrice, maxFlightTime);

        System.out.println(response);
    }

    @Test
    void sendAmadeusRequestPastDate() {
        String origin = "WRO";
        String destination = "LON";
        String date = "2024-10-25";
        String time = "10:00:00";
        int maxFlightOffers = 3;

        AmadeusErrorException thrownException = assertThrows(AmadeusErrorException.class, () -> {
            amadeusApiClient.sendAmadeusRequest(origin, destination, date, time, maxFlightOffers);
        });

        assertEquals(400, thrownException.getStatusCode());

        String responseBody = thrownException.getErrorMessage();
        System.out.println("Response Body: " + responseBody);
        assertTrue(responseBody.contains("Date/Time is in the past"));
    }
}
