package org.dyploma.search.validator;

import org.dyploma.algorithm.AlgorithmRequestCreator;
import org.dyploma.exception.ValidationException;
import org.dyploma.search.domain.Search;
import org.dyploma.search.domain.SearchRequest;
import org.dyploma.search.place.PlaceInSearch;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static java.time.ZoneOffset.UTC;

@Component
public class SearchValidator {

    public void validateSearch(Search search) {
        validatePlaces(search.getPlacesToVisit());
        validateSearchDuration(search);
        validateTripStartDate(search.getTripStartDate());
        validatePlacesOrder(search.getPlacesToVisit());
    }

    public void validateSearchRequest(SearchRequest searchRequest) {
        validatePlaces(searchRequest.getPlacesToVisit());
        validateSearchRequestDuration(searchRequest);
        validateTripStartDate(searchRequest.getTripStartDate());
        validatePlacesOrder(searchRequest.getPlacesToVisit());
    }

    private void validatePlaces(List<PlaceInSearch> places) {
        PlaceInSearch startPlace = places.get(0);
        PlaceInSearch endPlace = places.get(places.size() - 1);
        List<PlaceInSearch> placesToVisit = places.subList(1, places.size() - 1);
        if (placesToVisit.stream().anyMatch(place -> (place.getCity().equals(startPlace.getCity()) && place.getCountry().equals(startPlace.getCountry())) || (place.getCity().equals(endPlace.getCity()) && place.getCountry().equals(endPlace.getCountry())))) {
            throw new ValidationException("Start and end place should not be in places to visit");
        }
        if (placesToVisit.stream().map(place -> place.getCity() + place.getCountry()).distinct().count() != placesToVisit.size()) {
            throw new ValidationException("Places to visit should not contain duplicates");
        }
    }

    private void validateSearchDuration(Search search) {
        int stayDurationInPlacesSum = 0;
        for (int i = 1; i < search.getPlacesToVisit().size() - 1; i++) {
            int placeStayDuration = validateAndExtractPlaceDuration(search.getPlacesToVisit().get(i));
            stayDurationInPlacesSum += placeStayDuration;
        }
        if (stayDurationInPlacesSum > search.getMaxTripDuration()) {
            throw new ValidationException("Sum of hours to spend at each place to visit should not exceed max trip duration");
        }
    }

    private void validateSearchRequestDuration(SearchRequest searchRequest) {
        int stayDurationInPlacesSum = 0;
        int maxAvailableTripDuration = 24;
        for (int i = 1; i < searchRequest.getPlacesToVisit().size() - 1; i++) {
            int placeStayDuration = validateAndExtractPlaceDuration(searchRequest.getPlacesToVisit().get(i));
            stayDurationInPlacesSum += placeStayDuration;
            maxAvailableTripDuration += AlgorithmRequestCreator.getHoursMax(placeStayDuration);
        }
        int maxTripDuration = searchRequest.getMaxTripDuration();
        if (stayDurationInPlacesSum > maxTripDuration) {
            throw new ValidationException("Sum of hours to spend at each place to visit should not exceed max trip duration");
        }
        searchRequest.setMaxTripDuration(Math.min(maxTripDuration, maxAvailableTripDuration));
    }

    private static Integer validateAndExtractPlaceDuration(PlaceInSearch place) {
        Integer placeStayDuration = place.getStayDuration();
        if (placeStayDuration == null) {
            throw new ValidationException("Stay duration at each place to visit should be specified");
        }
        else if (placeStayDuration < 10) {
            throw new ValidationException("Stay duration at each place to visit should be at least 10 hours");
        }
        else if (placeStayDuration > 240) {
            throw new ValidationException("Stay duration at each place to visit should be at most 10 days");
        }
        return placeStayDuration;
    }

    private void validateTripStartDate(LocalDate tripStartDate) {
        if (tripStartDate.isAfter(Instant.now().plusSeconds(60 * 60 * 24 * 365).atZone(UTC).toLocalDate())) {
            throw new ValidationException("Trip start date couldn't be more than a year in the future");
        }
    }

    private void validatePlacesOrder(List<PlaceInSearch> places) {
        if (places.stream().anyMatch(place -> place.getEntryOrder() == null)) {
            throw new ValidationException("Order number of places to visit should be specified");
        }
        if (places.stream().map(PlaceInSearch::getEntryOrder).distinct().count() != places.size()) {
            throw new ValidationException("Order number of places to visit should be unique");
        }
        if (places.stream().anyMatch(place -> place.getEntryOrder() < 1 || place.getEntryOrder() > places.size())) {
            throw new ValidationException("Order number of places to visit should be in range from 1 to number of places");
        }
    }

}
