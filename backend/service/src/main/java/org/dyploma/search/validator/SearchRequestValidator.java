package org.dyploma.search.validator;

import org.dyploma.exception.ValidationException;
import org.dyploma.search.dto.request.PlaceRequest;
import org.dyploma.search.dto.request.SearchRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchRequestValidator {
    public void validate(SearchRequest searchRequest) {
        validatePlaces(searchRequest.getPlaces());
        validateHoursToSpend(searchRequest.getMaxHoursToSpend(), searchRequest.getPlaces());
    }

    private void validatePlaces(List<PlaceRequest> places) {
        PlaceRequest startPlace = places.get(0);
        PlaceRequest endPlace = places.get(places.size() - 1);
        List<PlaceRequest> middlePlaces = places.subList(1, places.size() - 1);
        if (middlePlaces.contains(startPlace) || middlePlaces.contains(endPlace)) {
            throw new ValidationException("Start and end places should not be in the places to visit");
        }
    }

    private void validateHoursToSpend(int maxHoursToSpend, List<PlaceRequest> places) {
        int hoursToSpend = places.stream()
                .mapToInt(PlaceRequest::getHoursToSpend)
                .sum();
        if (hoursToSpend > maxHoursToSpend) {
            throw new ValidationException("Sum of hours to spend at each place should not exceed max hours to spend");
        }
    }
}
