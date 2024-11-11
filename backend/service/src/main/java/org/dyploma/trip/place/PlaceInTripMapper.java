package org.dyploma.trip.place;

public class PlaceInTripMapper {
    public static PlaceInTrip mapToPlaceInTrip(com.openapi.model.PlaceInTrip placeInTripApi) {
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
