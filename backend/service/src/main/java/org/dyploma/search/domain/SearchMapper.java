package org.dyploma.search.domain;


import org.dyploma.criteria.CriteriaMode;
import org.dyploma.place.PlaceMapper;
import org.dyploma.search.algorithm.request.SearchRequest;
import org.dyploma.search.algorithm.response.SearchResponseElement;
import org.dyploma.tag.Tag;
import org.dyploma.tag.TagMapper;
import org.dyploma.trip.transfer.TransferMapper;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.dyploma.transport.TransportMapper.mapToTransportTypeRequest;
import static org.dyploma.transport.TransportMapper.mapToTransportTypeResponse;

public class SearchMapper {

    public static SearchRequest mapToSearchRequest(com.openapi.model.SearchRequest searchRequest) {
        return SearchRequest.builder()
                .placesToVisit(
                        searchRequest.getPlacesToVisit().stream()
                                .map(PlaceMapper::mapToPlaceDto)
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
                    .map(TransferMapper::mapTransferResponseToTransfer).toList());
            searchResponseResult.addTripsItem(searchResponseElementResult);
        }

        return searchResponseResult;
    }

    public static Search mapToSearchDomain(com.openapi.model.Search search) {
        return Search.builder()
                .name(search.getName())
                .tags(search.getTags().stream()
                        .map(TagMapper::mapToSearchTagDomain)
                        .toList())
                .placesToVisit(
                        search.getPlacesToVisit().stream()
                        .map(PlaceMapper::mapToPlaceDomain)
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
                .map(Tag::getTag)
                .toList());
        searchResult.setPlacesToVisit(search.getPlacesToVisit().stream()
                .map(PlaceMapper::mapPlaceDomainToPlace)
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


    private static CriteriaMode mapToCriteriaTypeRequest(com.openapi.model.CriteriaType criteriaType) {
        return switch (criteriaType) {
            case PRICE -> CriteriaMode.PRICE;
            case DURATION -> CriteriaMode.DURATION;
        };
    }

    private static com.openapi.model.CriteriaType mapToCriteriaTypeResponse(CriteriaMode criteriaType) {
        return switch (criteriaType) {
            case PRICE -> com.openapi.model.CriteriaType.PRICE;
            case DURATION -> com.openapi.model.CriteriaType.DURATION;
        };
    }


    private static Instant parseStartDate(String startDate) {
        try {
            return Instant.parse(startDate);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid startDate format. Expected ISO-8601 format.", e);
        }
    }
}
