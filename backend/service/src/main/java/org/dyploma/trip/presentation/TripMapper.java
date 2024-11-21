package org.dyploma.trip.presentation;

import com.openapi.model.PageInfo;
import com.openapi.model.TransportMode;
import org.dyploma.search.domain.Search;
import org.dyploma.search.domain.SearchFilterRequest;
import org.dyploma.search.presentation.SearchMapper;
import org.dyploma.tag.trip_tag.domain.TripTag;
import org.dyploma.trip.domain.Trip;
import org.dyploma.trip.domain.TripFilterRequest;
import org.dyploma.trip.place.PlaceInTripMapper;
import org.dyploma.trip.transfer.TransferMapper;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class TripMapper {
    public static Trip mapToTrip(com.openapi.model.Trip tripApi) {
        return Trip.builder()
                .name(tripApi.getName().trim().replaceAll("\\s+", " ").toLowerCase())
                .startDate(Date.valueOf(tripApi.getStartDate()))
                .endDate(Date.valueOf(tripApi.getEndDate()))
                .passengerCount(tripApi.getPassengerCount())
                .totalCost(tripApi.getTotalCost().doubleValue())
                .totalTransferTime(tripApi.getTotalTransferTime())
                .duration(tripApi.getDuration())
                .build();
    }

    public static com.openapi.model.Trip mapToTripApi(Trip trip) {
        com.openapi.model.Trip tripApi = new com.openapi.model.Trip()
                .id(trip.getId())
                .name(trip.getName())
                .transfers(trip.getTransfers().stream()
                        .map(TransferMapper::mapToTransferApi)
                        .toList())
                .places(trip.getPlaces().stream()
                        .map(PlaceInTripMapper::mapToPlaceInTripApi)
                        .toList())
                .startDate(trip.getStartDate().toLocalDate())
                .endDate(trip.getEndDate().toLocalDate())
                .saveDate(trip.getSaveDate() != null ? trip.getSaveDate().toLocalDate() : null)
                .passengerCount(trip.getPassengerCount())
                .totalCost(BigDecimal.valueOf(trip.getTotalCost()))
                .totalTransferTime(trip.getTotalTransferTime())
                .duration(trip.getDuration());
        if (trip.getTags() != null) {
            tripApi.setTags(trip.getTags().stream()
                    .map(TripTag::getName)
                    .toList());
        }
        return tripApi;
    }

    public static TripFilterRequest mapToTripFilterRequest(LocalDate fromDate, LocalDate toDate, List<String> tags) {
        return TripFilterRequest.builder()
                .fromDate(fromDate != null ? Date.valueOf(fromDate) : null)
                .toDate(toDate != null ? Date.valueOf(toDate) : null)
                .tags(tags)
                .build();
    }

    public static com.openapi.model.TripPage mapToTripPageApi(Page<Trip> tripPage) {
        com.openapi.model.TripPage tripPageApi = new com.openapi.model.TripPage();
        tripPageApi.setContent(tripPage.getContent().stream()
                .map(TripMapper::mapToTripApi)
                .toList());
        tripPageApi.setTotalPages(tripPage.getTotalPages());
        tripPageApi.setTotalElements((int) tripPage.getTotalElements());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setNumber(tripPage.getNumber());
        pageInfo.setSize(tripPage.getSize());
        tripPageApi.setPage(pageInfo);
        return tripPageApi;
    }
}
