package org.dyploma.search.presentation;

import com.openapi.model.PageInfo;
import org.dyploma.search.domain.Search;
import org.dyploma.search.domain.SearchFilterRequest;
import org.dyploma.search.domain.SearchRequest;
import org.dyploma.search.place.PlaceInSearchMapper;
import org.dyploma.tag.search_tag.domain.SearchTag;
import org.dyploma.transport.TransportModeMapper;
import org.dyploma.trip.domain.Trip;
import org.dyploma.trip.presentation.TripMapper;
import org.springframework.data.domain.Page;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.dyploma.search.criteria.CriteriaModeMapper.mapToCriteriaMode;
import static org.dyploma.search.criteria.CriteriaModeMapper.mapToCriteriaModeApi;
import static org.dyploma.transport.TransportModeMapper.mapToTransportMode;
import static org.dyploma.transport.TransportModeMapper.mapToTransportModeApi;

public class SearchMapper {

    public static Search mapToSearch(com.openapi.model.Search searchApi) {
        return Search.builder()
                .name(searchApi.getName().trim().replaceAll("\\s+", " ").toLowerCase())
                .passengerCount(searchApi.getPassengerCount())
                .preferredTransport(searchApi.getPreferredTransport() != null ? mapToTransportMode(searchApi.getPreferredTransport()) : null)
                .optimizationCriteria(mapToCriteriaMode(searchApi.getOptimizationCriteria()))
                .tripStartDate(Date.valueOf(searchApi.getTripStartDate()))
                .maxTripDuration(searchApi.getMaxTripDuration())
                .build();
    }

    public static com.openapi.model.Search mapToSearchApi(Search search) {
        com.openapi.model.Search searchApi = new com.openapi.model.Search()
                .id(search.getId())
                .name(search.getName())
                .saveDate(search.getSaveDate().toLocalDate())
                .placesToVisit(search.getPlacesToVisit().stream()
                        .map(PlaceInSearchMapper::mapToPlaceInSearchApi)
                        .toList())
                .passengerCount(search.getPassengerCount())
                .preferredTransport(search.getPreferredTransport() != null ? mapToTransportModeApi(search.getPreferredTransport()) : null)
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

    public static SearchFilterRequest mapToSearchFilterRequest(com.openapi.model.CriteriaMode optimizationCriteria, List<com.openapi.model.TransportMode> preferredTransports, LocalDate fromDate, LocalDate toDate, List<String> tags) {
        return SearchFilterRequest.builder()
                .optimizationCriteria(optimizationCriteria != null ? mapToCriteriaMode(optimizationCriteria) : null)
                .transportModes(preferredTransports != null ? preferredTransports.stream()
                        .map(TransportModeMapper::mapToTransportMode)
                        .toList() : null)
                .fromDate(fromDate != null ? Date.valueOf(fromDate) : null)
                .toDate(toDate != null ? Date.valueOf(toDate) : null)
                .tags(tags)
                .build();
    }

    public static com.openapi.model.SearchPage mapToSearchPageApi(Page<Search> searchPage) {
        com.openapi.model.SearchPage searchPageApi = new com.openapi.model.SearchPage();
        searchPageApi.setContent(searchPage.getContent().stream()
                .map(SearchMapper::mapToSearchApi)
                .toList());
        searchPageApi.setTotalPages(searchPage.getTotalPages());
        searchPageApi.setTotalElements((int) searchPage.getTotalElements());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setNumber(searchPage.getNumber());
        pageInfo.setSize(searchPage.getSize());
        searchPageApi.setPage(pageInfo);
        return searchPageApi;
    }
}
