package org.dyploma.search.presentation;

import org.dyploma.search.domain.Search;
import org.dyploma.search.domain.SearchRequest;
import org.dyploma.search.place.PlaceInSearchMapper;
import org.dyploma.tag.search_tag.domain.SearchTag;
import org.dyploma.trip.domain.Trip;
import org.dyploma.trip.presentation.TripMapper;

import java.sql.Date;
import java.util.List;

import static org.dyploma.search.criteria.CriteriaModeMapper.mapToCriteriaMode;
import static org.dyploma.search.criteria.CriteriaModeMapper.mapToCriteriaModeApi;
import static org.dyploma.search.place.PlaceInSearchMapper.mapToPlaceInSearch;
import static org.dyploma.transport.TransportModeMapper.mapToTransportMode;
import static org.dyploma.transport.TransportModeMapper.mapToTransportModeApi;

public class SearchMapper {

    public static Search mapToSearch(com.openapi.model.Search searchApi) {
        Search search = Search.builder()
                .name(searchApi.getName().toLowerCase())
                .startPlace(mapToPlaceInSearch(searchApi.getStartPlace()))
                .endPlace(mapToPlaceInSearch(searchApi.getEndPlace()))
                .passengerCount(searchApi.getPassengerCount())
                .preferredTransport(mapToTransportMode(searchApi.getPreferredTransport()))
                .optimizationCriteria(mapToCriteriaMode(searchApi.getOptimizationCriteria()))
                .tripStartDate(Date.valueOf(searchApi.getTripStartDate()))
                .maxTripDuration(searchApi.getMaxTripDuration())
                .build();
        for (com.openapi.model.PlaceInSearch placeInSearchApi : searchApi.getPlacesToVisit()) {
            search.addPlaceToVisit(PlaceInSearchMapper.mapToPlaceInSearch(placeInSearchApi));
        }
        return search;
    }

    public static com.openapi.model.Search mapToSearchApi(Search search) {
        com.openapi.model.Search searchApi = new com.openapi.model.Search()
                .id(search.getId())
                .name(search.getName())
                .saveDate(search.getSaveDate().toLocalDate())
                .startPlace(PlaceInSearchMapper.mapToPlaceInSearchApi(search.getStartPlace()))
                .endPlace(PlaceInSearchMapper.mapToPlaceInSearchApi(search.getEndPlace()))
                .placesToVisit(search.getPlacesToVisit().stream()
                        .map(PlaceInSearchMapper::mapToPlaceInSearchApi)
                        .toList())
                .passengerCount(search.getPassengerCount())
                .preferredTransport(mapToTransportModeApi(search.getPreferredTransport()))
                .optimizationCriteria(mapToCriteriaModeApi(search.getOptimizationCriteria()))
                .tripStartDate(search.getTripStartDate().toLocalDate())
                .maxTripDuration(search.getMaxTripDuration());
        if (search.getTags() != null) {
            searchApi.setTags(search.getTags().stream()
                    .map(SearchTag::getName)
                    .toList());
        }
        return searchApi;
    }

    public static SearchRequest mapToSearchRequest(com.openapi.model.SearchRequest searchRequestApi) {
        return SearchRequest.builder()
                .startPlace(mapToPlaceInSearch(searchRequestApi.getStartPlace()))
                .endPlace(mapToPlaceInSearch(searchRequestApi.getEndPlace()))
                .placesToVisit(searchRequestApi.getPlacesToVisit().stream()
                        .map(PlaceInSearchMapper::mapToPlaceInSearch)
                        .toList())
                .passengerCount(searchRequestApi.getPassengerCount())
                .preferredTransport(mapToTransportMode(searchRequestApi.getPreferredTransport()))
                .optimizationCriteria(mapToCriteriaMode(searchRequestApi.getOptimizationCriteria()))
                .tripStartDate(Date.valueOf(searchRequestApi.getTripStartDate()))
                .maxTripDuration(searchRequestApi.getMaxTripDuration())
                .build();
    }

    public static com.openapi.model.SearchResponse mapToSearchResponseApi(List<Trip> trips) {
        com.openapi.model.SearchResponse searchResponse = new com.openapi.model.SearchResponse();
        searchResponse.setContent(trips.stream()
                        .map(TripMapper::mapToTripApi)
                        .toList());
        return searchResponse;
    }
}
