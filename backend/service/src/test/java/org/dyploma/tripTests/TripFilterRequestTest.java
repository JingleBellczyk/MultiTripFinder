package org.dyploma.tripTests;

import org.dyploma.trip.domain.TripFilterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TripFilterRequestTest {

    private List<String> tags;
    private Date fromDate;
    private Date toDate;

    @BeforeEach
    void setUp() {
        tags = List.of("business", "vacation");

        fromDate = Date.valueOf(LocalDate.of(2024, 1, 1));
        toDate = Date.valueOf(LocalDate.of(2024, 12, 31));
    }

    @Test
    void testTripFilterRequestBuilderValid() {
        TripFilterRequest tripFilterRequest = TripFilterRequest.builder()
                .tags(tags)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        assertNotNull(tripFilterRequest);
        assertEquals(tags, tripFilterRequest.getTags());
        assertEquals(fromDate, tripFilterRequest.getFromDate());
        assertEquals(toDate, tripFilterRequest.getToDate());
    }

    @Test
    void testBuilderWithNullDates() {
        TripFilterRequest tripFilterRequest = TripFilterRequest.builder()
                .tags(tags)
                .build();

        assertNull(tripFilterRequest.getFromDate());
        assertNull(tripFilterRequest.getToDate());
    }
}
