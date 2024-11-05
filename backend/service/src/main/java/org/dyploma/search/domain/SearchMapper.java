package org.dyploma.search.domain;


import org.dyploma.criteria.CriteriaType;
import org.dyploma.place.Place;
import org.dyploma.place.PlaceDto;
import org.dyploma.search.algorithm.request.SearchRequest;
import org.dyploma.search.algorithm.response.SearchResponseElement;
import org.dyploma.tag.SearchTag;
import org.dyploma.tag.TripTag;
import org.dyploma.transfer.TransferResponse;
import org.dyploma.transport.TransportType;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SearchMapper {

    public static SearchRequest mapToSearchRequest(com.openapi.model.SearchRequest searchRequest) {
        return SearchRequest.builder()
                .placesToVisit(
                        searchRequest.getPlacesToVisit().stream()
                                .map(SearchMapper::mapToPlaceRequest)
                                .toList())
                .startPlace(searchRequest.getStartPlace())
                .endPlace(searchRequest.getEndPlace())
                .maxHoursToSpend(searchRequest.getMaxHoursToSpend())
                .startDate(parseStartDate(searchRequest.getStartDate()))
                .passengersNumber(searchRequest.getPassengersNumber())
                .preferredTransport(mapToTransportTypeRequest(searchRequest.getPreferredTransport()))
                .preferredCriteria(mapToCriteriaTypeRequest(searchRequest.getPreferredCriteria()))
                .build();
    }
    public static com.openapi.model.SearchResponse mapToSearchResponse(List<SearchResponseElement> searchResponse) {
        com.openapi.model.SearchResponse searchResponseResult = new com.openapi.model.SearchResponse();

        for (SearchResponseElement searchResponseElement : searchResponse) {
            com.openapi.model.SearchResponseElement searchResponseElementResult = new com.openapi.model.SearchResponseElement();
            searchResponseElementResult.setStartTime(searchResponseElement.getStartTime().toString());
            searchResponseElementResult.setEndTime(searchResponseElement.getEndTime().toString());
            searchResponseElementResult.setTotalDuration(searchResponseElement.getTotalDuration());
            searchResponseElementResult.setTotalTransferDuration(searchResponseElement.getTotalDuration());
            searchResponseElementResult.setTotalPrice(searchResponseElement.getTotalPrice());
            searchResponseElementResult.setPassengersNumber(searchResponseElement.getPassengersNumber());
            searchResponseElementResult.setTransfers(searchResponseElement.getTransfers().stream()
                    .map(SearchMapper::mapTranferResponseToTransfer).toList());
            searchResponseResult.addTripsItem(searchResponseElementResult);
        }

        return searchResponseResult;
    }

    public static Search mapToSearchDomain(com.openapi.model.Search search) {
        return Search.builder()
                .name(search.getName())
                .tags(search.getTags().stream()
                        .map(SearchMapper::mapToSearchTagDomain)
                        .toList())
                .placesToVisit(
                        search.getPlacesToVisit().stream()
                        .map(SearchMapper::mapToPlaceDomain)
                        .toList())
                .startPlace(search.getStartPlace())
                .endPlace(search.getEndPlace())
                .maxHoursToSpend(search.getMaxHoursToSpend())
                .startDate(parseStartDate(search.getStartDate()))
                .passengersNumber(search.getPassengersNumber())
                .preferredTransport(mapToTransportTypeRequest(search.getPreferredTransport()))
                .preferredCriteria(mapToCriteriaTypeRequest(search.getPreferredCriteria()))
                .build();
    }

    public static com.openapi.model.Search mapToSearch(Search search) {
        com.openapi.model.Search searchResult = new com.openapi.model.Search();

        searchResult.setName(search.getName());
        searchResult.setTags(search.getTags().stream()
                .map(SearchTag::getTag)
                .toList());
        searchResult.setPlacesToVisit(search.getPlacesToVisit().stream()
                .map(SearchMapper::mapPlaceDomainToPlace)
                .toList());
        searchResult.setStartPlace(search.getStartPlace());
        searchResult.setEndPlace(search.getEndPlace());
        searchResult.setMaxHoursToSpend(search.getMaxHoursToSpend());
        searchResult.setStartDate(search.getStartDate().toString());
        searchResult.setPassengersNumber(search.getPassengersNumber());
        searchResult.setPreferredTransport(mapToTransportTypeResponse(search.getPreferredTransport()));
        searchResult.setPreferredCriteria(mapToCriteriaTypeResponse(search.getPreferredCriteria()));

        return searchResult;
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

    private static com.openapi.model.Transfer mapTranferResponseToTransfer(TransferResponse transfer) {
        com.openapi.model.Transfer transferResult = new com.openapi.model.Transfer();

        transferResult.setStartPlace(mapPlaceDtoToPlaceResponse(transfer.getStartPlace()));
        transferResult.setEndPlace(mapPlaceDtoToPlaceResponse(transfer.getEndPlace()));
        transferResult.setStartDate(transfer.getStartDate().toString());
        transferResult.setEndDate(transfer.getEndDate().toString());
        transferResult.setTransitLine(transfer.getTransitLine());
        transferResult.setTransport(mapToTransportTypeResponse(transfer.getTransport()));
        transferResult.setPrice(transfer.getPrice());
        transferResult.setDuration(transfer.getDuration());
        transferResult.setOrder(transfer.getOrder());

        return transferResult;
    }

    private static com.openapi.model.Place mapPlaceDtoToPlace(PlaceDto placeDto) {
        com.openapi.model.Place place = new com.openapi.model.Place();
        place.setName(placeDto.getName());
        place.setHoursToSpend(placeDto.getHoursToSpend());
        place.setOrder(placeDto.getOrder());
        place.setIsChange(placeDto.isChange());
        return place;
    }

    private static com.openapi.model.Place mapPlaceDomainToPlace(Place place) {
        com.openapi.model.Place placeResult = new com.openapi.model.Place();
        placeResult.setName(place.getName());
        placeResult.setHoursToSpend(place.getHoursToSpend());
        placeResult.setOrder(place.getOrder());
        placeResult.setIsChange(place.isChange());
        return placeResult;
    }

    private static PlaceDto mapToPlaceRequest(com.openapi.model.Place place) {
        return PlaceDto.builder()
                .name(place.getName())
                .hoursToSpend(place.getHoursToSpend())
                .order(place.getOrder())
                .isChange(place.getIsChange())
                .build();
    }

    private static Place mapToPlaceDomain(com.openapi.model.Place place) {
        return Place.builder()
                .name(place.getName())
                .hoursToSpend(place.getHoursToSpend())
                .order(place.getOrder())
                .isChange(place.getIsChange())
                .build();
    }

    private static SearchTag mapToSearchTagDomain(String searchTag) {
        return SearchTag.builder()
                .tag(searchTag)
                .build();
    }

    private static TripTag mapToTripTagDomain(String tripTag) {
        return TripTag.builder()
                .tag(tripTag)
                .build();
    }
}
