package org.dyploma.trip.place;

import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.search.place.PlaceInSearchMapper;

import java.util.List;

public class PlaceInTripMapper {
    public static List<PlaceInTrip> mapToPlacesInTrip(List<com.openapi.model.PlaceInTrip> placesInTripApi) {
        return placesInTripApi.stream()
                .map(PlaceInTripMapper::mapToPlaceInTrip)
                .toList();
    }

    private static PlaceInTrip mapToPlaceInTrip(com.openapi.model.PlaceInTrip placeInTripApi) {
        return PlaceInTrip.builder()
                .country(placeInTripApi.getCountry())
                .city(placeInTripApi.getCity())
                .stayDuration(placeInTripApi.getStayDuration())
                .visitOrder(placeInTripApi.getVisitOrder())
                .isTransfer(placeInTripApi.getIsTransfer())
                .build();
    }

    public static com.openapi.model.PlaceInTrip mapToPlaceInTripApi(PlaceInTrip placeInTrip) {
        return new com.openapi.model.PlaceInTrip()
                .country(placeInTrip.getCountry())
                .city(placeInTrip.getCity())
                .stayDuration(placeInTrip.getStayDuration())
                .visitOrder(placeInTrip.getVisitOrder())
                .isTransfer(placeInTrip.isTransfer());
    }
}
