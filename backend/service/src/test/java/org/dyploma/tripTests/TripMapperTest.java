package org.dyploma.tripTests;

import com.openapi.model.Trip;
import org.dyploma.tag.trip_tag.domain.TripTag;
import org.dyploma.trip.domain.TripFilterRequest;
import org.dyploma.trip.presentation.TripMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TripMapperTest {
    @Test
    void shouldMapToTrip() {
        Trip tripApi = new Trip()
                .name(" My  Trip ")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 10))
                .passengerCount(3)
                .totalCost(BigDecimal.valueOf(1000.50))
                .totalTransferTime(300)
                .duration(9);

        org.dyploma.trip.domain.Trip trip = TripMapper.mapToTrip(tripApi);

        assertEquals("my trip", trip.getName());
        assertEquals(LocalDate.of(2024, 1, 1), trip.getStartDate());
        assertEquals(LocalDate.of(2024, 1, 10), trip.getEndDate());
        assertEquals(3, trip.getPassengerCount());
        assertEquals(1000.50, trip.getTotalCost());
        assertEquals(300, trip.getTotalTransferTime());
        assertEquals(9, trip.getDuration());
    }

    @Test
    void testMapToTripApi() {
        org.dyploma.trip.domain.Trip trip = org.dyploma.trip.domain.Trip.builder()
                .id(1)
                .name("my trip")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 10))
                .saveDate(LocalDate.of(2024, 1, 5))
                .passengerCount(3)
                .totalCost(1000.50)
                .totalTransferTime(300)
                .duration(9)
                .tags(List.of(
                        TripTag.builder().name("tag1").build(),
                        TripTag.builder().name("tag2").build()
                ))
                .transfers(List.of())
                .places(List.of())
                .build();

        Trip tripApi = TripMapper.mapToTripApi(trip);

        assertEquals(1, tripApi.getId());
        assertEquals("my trip", tripApi.getName());
        assertEquals(LocalDate.of(2024, 1, 1), tripApi.getStartDate());
        assertEquals(LocalDate.of(2024, 1, 10), tripApi.getEndDate());
        assertEquals(LocalDate.of(2024, 1, 5), tripApi.getSaveDate());
        assertEquals(3, tripApi.getPassengerCount());
        assertEquals(BigDecimal.valueOf(1000.50), tripApi.getTotalCost());
        assertEquals(300, tripApi.getTotalTransferTime());
        assertEquals(9, tripApi.getDuration());
        assertNotNull(tripApi.getTags());
        assertEquals(List.of("tag1", "tag2"), tripApi.getTags());
    }

    @Test
    void testMapToTripFilterRequest() {
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 10);
        List<String> tags = List.of("tag1", "tag2");

        TripFilterRequest filterRequest = TripMapper.mapToTripFilterRequest(fromDate, toDate, tags);

        assertEquals(Date.valueOf(LocalDate.of(2024, 1, 1)), filterRequest.getFromDate());
        assertEquals(Date.valueOf(LocalDate.of(2024, 1, 10)), filterRequest.getToDate());
        assertEquals(tags, filterRequest.getTags());
    }

    @Test
    void testMapToTripPageApi() {
        org.dyploma.trip.domain.Trip trip = org.dyploma.trip.domain.Trip.builder()
                .id(1)
                .name("my trip")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 10))
                .totalCost(500.0)
                .transfers(List.of())
                .places(List.of())
                .build();

        Page<org.dyploma.trip.domain.Trip> tripPage = new PageImpl<>(
                List.of(trip),
                PageRequest.of(0, 1),
                1
        );

        com.openapi.model.TripPage tripPageApi = TripMapper.mapToTripPageApi(tripPage);

        assertNotNull(tripPageApi);
        assertEquals(1, tripPageApi.getContent().size());
        assertEquals("my trip", tripPageApi.getContent().get(0).getName());
        assertEquals(1, tripPageApi.getTotalPages());
        assertEquals(1, tripPageApi.getTotalElements());
        assertNotNull(tripPageApi.getPage());
        assertEquals(0, tripPageApi.getPage().getNumber());
        assertEquals(1, tripPageApi.getPage().getSize());
    }

}
