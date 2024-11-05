package org.dyploma.place;

public class PlaceMapper {
    public static PlaceDto mapToPlaceDto(com.openapi.model.Place place) {
        return PlaceDto.builder()
                .name(place.getName())
                .hoursToSpend(place.getHoursToSpend())
                .order(place.getOrder())
                .isChange(place.getIsChange())
                .build();
    }

    public static Place mapToPlaceDomain(com.openapi.model.Place place) {
        return Place.builder()
                .name(place.getName())
                .hoursToSpend(place.getHoursToSpend())
                .order(place.getOrder())
                .isChange(place.getIsChange())
                .build();
    }
    public static com.openapi.model.Place mapPlaceDtoToPlace(PlaceDto placeDto) {
        com.openapi.model.Place place = new com.openapi.model.Place();
        place.setName(placeDto.getName());
        place.setHoursToSpend(placeDto.getHoursToSpend());
        place.setOrder(placeDto.getOrder());
        place.setIsChange(placeDto.isChange());
        return place;
    }

    public static com.openapi.model.Place mapPlaceDomainToPlace(Place place) {
        com.openapi.model.Place placeResult = new com.openapi.model.Place();
        placeResult.setName(place.getName());
        placeResult.setHoursToSpend(place.getHoursToSpend());
        placeResult.setOrder(place.getOrder());
        placeResult.setIsChange(place.isChange());
        return placeResult;
    }
}
