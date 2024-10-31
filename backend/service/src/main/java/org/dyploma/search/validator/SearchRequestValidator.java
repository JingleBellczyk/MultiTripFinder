package org.dyploma.search.validator;

import org.dyploma.exception.ValidationException;
import org.dyploma.place.PlaceDto;
import org.dyploma.search.dto.request.SearchRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class SearchRequestValidator {
    public void validate(SearchRequest searchRequest) {
        validatePlacesNames(searchRequest.getStartPlace(), searchRequest.getEndPlace(), searchRequest.getPlacesToVisit());
        validateHoursToSpend(searchRequest.getMaxHoursToSpend(), searchRequest.getPlacesToVisit());
        validatePlacesOrder(searchRequest.getPlacesToVisit());
        validatePassengersNumber(searchRequest.getPassengersNumber());
        validateStartDate(searchRequest.getStartDate());
    }

    private void validatePlacesNames(String startPlace, String endPlace, List<PlaceDto> places) {
        if (startPlace.isEmpty() ||
                endPlace.isEmpty() ||
                places.stream().anyMatch(place -> place.getName().isEmpty())) {
            throw new ValidationException("Place name should not be empty");
        }

        if (places.stream().anyMatch(place -> place.getName().equals(startPlace) || place.getName().equals(endPlace))) {
            throw new ValidationException("Start and end place should not be in places to visit");
        }

        if (places.stream().map(PlaceDto::getName).distinct().count() != places.size()) {
            throw new ValidationException("Places to visit should not contain duplicates");
        }
    }

    private void validateHoursToSpend(int maxHoursToSpend, List<PlaceDto> places) {
        if (places.stream().anyMatch(place -> place.getHoursToSpend() <= 0)) {
            throw new ValidationException("Hours to spend at each place should be greater than 0");
        }
        if (maxHoursToSpend <= 0) {
            throw new ValidationException("Max hours to spend should be greater than 0");
        }
        int hoursToSpend = places.stream()
                .mapToInt(PlaceDto::getHoursToSpend)
                .sum();
        if (hoursToSpend > maxHoursToSpend) {
            throw new ValidationException("Sum of hours to spend at each place should not exceed max hours to spend");
        }
    }

    private void validateStartDate(Instant startDate) {
        if (startDate.isBefore(Instant.now())) {
            throw new ValidationException("Start date should be in the future");
        }
        if (startDate.isAfter(Instant.now().plusSeconds(60 * 60 * 24 * 365))) {
            throw new ValidationException("Start date couldn't be more than a year in the future");
        }
    }

    private void validatePassengersNumber(int passengersNumber) {
        if (passengersNumber <= 0) {
            throw new ValidationException("Passengers number should be greater than 0");
        }
    }

    private void validatePlacesOrder(List<PlaceDto> places) {
        if (places.stream().anyMatch(place -> place.getOrder() < 0)) {
            throw new ValidationException("Place order should be greater than 0");
        }
    }
}
