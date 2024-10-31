package org.dyploma.search.domain;


import com.openapi.model.TripListResponse;
import org.dyploma.criteria.CriteriaType;
import org.dyploma.place.PlaceDto;
import org.dyploma.search.dto.request.SearchRequest;
import org.dyploma.search.dto.response.TripResponse;
import org.dyploma.transfer.TransferResponse;
import org.dyploma.transport.TransportType;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class SearchMapper {
    public static com.openapi.model.TripListResponse mapToTripListResponse(List<TripResponse> tripResponses) {
        TripListResponse tripListResponse = new TripListResponse();

        for (TripResponse tripResponse : tripResponses) {
            com.openapi.model.TripResponse tripResponseResult = new com.openapi.model.TripResponse();
            tripResponseResult.setPlaces(tripResponse.getPlaces().stream()
                    .map(SearchMapper::mapToPlaceResponse)
                    .collect(Collectors.toList()));
            tripResponseResult.setStartPlace(tripResponse.getStartPlace());
            tripResponseResult.setEndPlace(tripResponse.getEndPlace());
            tripResponseResult.setStartTime(tripResponse.getStartTime().toString());
            tripResponseResult.setEndTime(tripResponse.getEndTime().toString());
            tripResponseResult.setTotalDuration(tripResponse.getTotalDuration());
            tripResponseResult.setTotalTransferDuration(tripResponse.getTotalDuration());
            tripResponseResult.setTotalPrice(tripResponse.getTotalPrice());
            tripResponseResult.setPassengersNumber(tripResponse.getPassengersNumber());
            tripResponseResult.setTransfers(tripResponse.getTransfers().stream()
                    .map(SearchMapper::mapToTransferResponse)
                    .collect(Collectors.toList()));
            tripListResponse.addTripsItem(tripResponseResult);
        }

        return tripListResponse;
    }


    public static SearchRequest mapToSearchRequest(com.openapi.model.SearchRequest searchRequest) {
        return SearchRequest.builder()
                .placesToVisit(
                        searchRequest.getPlacesToVisit().stream()
                        .map(SearchMapper::mapToPlaceRequest)
                        .collect(Collectors.toList()))
                .startPlace(searchRequest.getStartPlace())
                .endPlace(searchRequest.getEndPlace())
                .maxHoursToSpend(searchRequest.getMaxHoursToSpend())
                .startDate(parseStartDate(searchRequest.getStartDate()))
                .passengersNumber(searchRequest.getPassengersNumber())
                .preferredTransport(mapToTransportTypeRequest(searchRequest.getPreferredTransport()))
                .preferredCriteria(mapToCriteriaTypeRequest(searchRequest.getPreferredCriteria()))
                .build();
    }


    private static CriteriaType mapToCriteriaTypeRequest(com.openapi.model.CriteriaType criteriaType) {
        return switch (criteriaType) {
            case PRICE -> CriteriaType.PRICE;
            case DURATION -> CriteriaType.DURATION;
        };
    }


    private static TransportType mapToTransportTypeRequest(com.openapi.model.TransportType transportType) {
        return switch (transportType) {
            case BUS -> TransportType.BUS;
            case TRAIN -> TransportType.TRAIN;
            case PLANE -> TransportType.PLANE;
        };
    }


    private static com.openapi.model.TransportType mapToTransportTypeResponse(TransportType transportType) {
        return switch (transportType) {
            case BUS -> com.openapi.model.TransportType.BUS;
            case TRAIN -> com.openapi.model.TransportType.TRAIN;
            case PLANE -> com.openapi.model.TransportType.PLANE;
        };
    }


    private static Instant parseStartDate(String startDate) {
        try {
            return Instant.parse(startDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid startDate format. Expected ISO-8601 format.", e);
        }
    }

    private static com.openapi.model.TransferResponse mapToTransferResponse(TransferResponse transfer) {
        com.openapi.model.TransferResponse transferResult = new com.openapi.model.TransferResponse();

        transferResult.setStartPlace(mapToPlaceResponse(transfer.getStartPlace()));
        transferResult.setEndPlace(mapToPlaceResponse(transfer.getEndPlace()));
        transferResult.setStartDate(transfer.getStartDate().toString());
        transferResult.setEndDate(transfer.getEndDate().toString());
        transferResult.setTransitLine(transfer.getTransitLine());
        transferResult.setTransport(mapToTransportTypeResponse(transfer.getTransport()));
        transferResult.setPrice(transfer.getPrice());
        transferResult.setDuration(transfer.getDuration());
        transferResult.setOrder(transfer.getOrder());

        return transferResult;
    }

    private static com.openapi.model.Place mapToPlaceResponse(PlaceDto placeDto) {
        com.openapi.model.Place place = new com.openapi.model.Place();
        place.setName(placeDto.getName());
        place.setHoursToSpend(placeDto.getHoursToSpend());
        place.setOrder(placeDto.getOrder());
        place.setIsChange(placeDto.isChange());
        return place;
    }

    private static PlaceDto mapToPlaceRequest(com.openapi.model.Place place) {
        return PlaceDto.builder()
                .name(place.getName())
                .hoursToSpend(place.getHoursToSpend())
                .order(place.getOrder())
                .isChange(place.getIsChange())
                .build();
    }
}
