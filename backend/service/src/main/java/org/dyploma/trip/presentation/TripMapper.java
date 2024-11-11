package org.dyploma.trip.presentation;

import org.dyploma.tag.trip_tag.domain.TripTag;
import org.dyploma.trip.domain.Trip;
import org.dyploma.trip.place.PlaceInTripMapper;
import org.dyploma.trip.transfer.TransferMapper;

import java.math.BigDecimal;
import java.sql.Date;

public class TripMapper {
    public static Trip mapToTrip(com.openapi.model.Trip tripApi) {
        Trip trip = Trip.builder()
                .name(tripApi.getName().toLowerCase())
                .startDate(Date.valueOf(tripApi.getStartDate()))
                .endDate(Date.valueOf(tripApi.getEndDate()))
                .passengerCount(tripApi.getPassengerCount())
                .totalCost(tripApi.getTotalCost().doubleValue())
                .totalTransferTime(tripApi.getTotalTransferTime())
                .duration(tripApi.getDuration())
                .build();
        return trip;
    }

    public static com.openapi.model.Trip mapToTripApi(Trip trip) {
        com.openapi.model.Trip tripApi = new com.openapi.model.Trip()
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
}
