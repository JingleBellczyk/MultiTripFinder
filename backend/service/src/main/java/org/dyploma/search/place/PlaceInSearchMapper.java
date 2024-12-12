package org.dyploma.search.place;

import java.util.List;

public class PlaceInSearchMapper {
    public static List<PlaceInSearch> mapToPlacesInSearch(List<com.openapi.model.PlaceInSearch> placesInSearchApi) {
        return placesInSearchApi.stream()
                .map(PlaceInSearchMapper::mapToPlaceInSearch)
                .toList();
    }

    public static PlaceInSearch mapToPlaceInSearch(com.openapi.model.PlaceInSearch placeInSearchApi) {
        return PlaceInSearch.builder()
                .country(placeInSearchApi.getCountry())
                .city(placeInSearchApi.getCity())
                .stayDuration(placeInSearchApi.getStayDuration())
                .entryOrder(placeInSearchApi.getEntryOrder())
                .build();
    }

    public static com.openapi.model.PlaceInSearch mapToPlaceInSearchApi(PlaceInSearch placeInSearch) {
        return new com.openapi.model.PlaceInSearch()
                .country(placeInSearch.getCountry())
                .city(placeInSearch.getCity())
                .stayDuration(placeInSearch.getStayDuration())
                .entryOrder(placeInSearch.getEntryOrder());
    }
}
