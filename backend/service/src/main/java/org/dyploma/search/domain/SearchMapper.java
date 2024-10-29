package org.dyploma.search.domain;


import com.openapi.model.TripListResponse;
import org.dyploma.criteria.CriteriaType;
import org.dyploma.search.dto.request.PlaceRequest;
import org.dyploma.search.dto.request.SearchRequest;
import org.dyploma.search.dto.response.Transfer;
import org.dyploma.search.dto.response.TripResponse;
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
            tripResponseResult.setPlaces(tripResponse.getPlaces());
            tripResponseResult.setTotalTransferDuration(tripResponse.getTotalDuration().toString());
            tripResponseResult.setPassengersNumber(tripResponse.getPassengersNumber());
            tripResponseResult.setTotalPrice(tripResponse.getTotalPrice());
            tripResponseResult.setTransfers(tripResponse.getTransfers().stream()
                    .map(SearchMapper::mapToTransfer)
                    .collect(Collectors.toList()));
            tripListResponse.addTripsItem(tripResponseResult);
        }

        return tripListResponse;
    }


    public static SearchRequest mapToSearchRequest(com.openapi.model.SearchRequest searchRequest) {
        return SearchRequest.builder()
                .places(searchRequest.getPlaces().stream()
                        .map(SearchMapper::mapToPlaceRequest)
                        .collect(Collectors.toList()))
                .maxHoursToSpend(searchRequest.getMaxHoursToSpend())
                .passengersNumber(searchRequest.getPassengersNumber())
                .preferredCriteria(mapToCriteriaTypeRequest(searchRequest.getPreferredCriteria()))
                .preferredTransport(mapToTransportTypeRequest(searchRequest.getPreferredTransport()))
                .startDate(parseStartDate(searchRequest.getStartDate()))
                .build();
    }


    private static PlaceRequest mapToPlaceRequest(com.openapi.model.PlaceRequest placeRequest) {
        return PlaceRequest.builder()
                .place(placeRequest.getPlace())
                .hoursToSpend(placeRequest.getHoursToSpend())
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

    private static com.openapi.model.Transfer mapToTransfer(Transfer transfer) {
        com.openapi.model.Transfer transferResult = new com.openapi.model.Transfer();

        transferResult.setStartPlace(transfer.getStartPlace());
        transferResult.setEndPlace(transfer.getEndPlace());
        transferResult.setStartDate(transfer.getStartDate().toString());
        transferResult.setEndDate(transfer.getEndDate().toString());
        transferResult.setTransitLine(transfer.getTransitLine());
        transferResult.setTransport(mapToTransportTypeResponse(transfer.getTransport()));
        transferResult.setPrice(transfer.getPrice());
        transferResult.setDuration(transfer.getDuration().toString());

        return transferResult;
    }
}
