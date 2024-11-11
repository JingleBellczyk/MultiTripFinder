package org.dyploma.search.validator;

import org.dyploma.exception.ValidationException;
import org.dyploma.search.domain.Search;
import org.dyploma.search.domain.SearchRepository;
import org.dyploma.search.domain.SearchRequest;
import org.dyploma.search.place.PlaceInSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

import static java.time.ZoneOffset.UTC;

@Component
public class SearchValidator {
    private final SearchRepository searchRepository;

    @Autowired
    public SearchValidator(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    public void validateSearch(Search search) {
        validatePlaces(search.getStartPlace(), search.getEndPlace(), search.getPlacesToVisit());
        validateDuration(search.getMaxTripDuration(), search.getPlacesToVisit());
        validateTripStartDate(search.getTripStartDate());
        validatePlacesOrder(search.getPlacesToVisit());
        validateName(search.getName());
    }

    public void validateSearchRequest(SearchRequest searchRequest) {
        validatePlaces(searchRequest.getStartPlace(), searchRequest.getEndPlace(), searchRequest.getPlacesToVisit());
        validateDuration(searchRequest.getMaxTripDuration(), searchRequest.getPlacesToVisit());
        validateTripStartDate(searchRequest.getTripStartDate());
        validatePlacesOrder(searchRequest.getPlacesToVisit());
    }

    private void validateName(String name) {
        if (searchRepository.existsByName(name)) {
            throw new ValidationException("Search with this name already exists");
        }
    }

    private void validatePlaces(PlaceInSearch startPlace, PlaceInSearch endPlace, List<PlaceInSearch> places) {
        if (places.stream().anyMatch(place -> (place.getCity().equals(startPlace.getCity()) && place.getCountry().equals(startPlace.getCountry())) || (place.getCity().equals(endPlace.getCity()) && place.getCountry().equals(endPlace.getCountry())))) {
            throw new ValidationException("Start and end place should not be in places to visit");
        }
        if (places.stream().map(place -> place.getCity() + place.getCountry()).distinct().count() != places.size()) {
            throw new ValidationException("Places to visit should not contain duplicates");
        }
    }

    private void validateDuration(int maxTripDuration, List<PlaceInSearch> places) {
        if (places.stream().anyMatch(place -> place.getStayDuration() == null)) {
            throw new ValidationException("Stay duration at each place to visit should be specified");
        }
        int stayDurationSum = places.stream()
                .mapToInt(PlaceInSearch::getStayDuration)
                .sum();
        if (stayDurationSum > maxTripDuration) {
            throw new ValidationException("Sum of hours to spend at each place to visit should not exceed max trip duration");
        }
    }

    private void validateTripStartDate(Date tripStartDate) {
        if (tripStartDate.toLocalDate().isAfter(Instant.now().plusSeconds(60 * 60 * 24 * 365).atZone(UTC).toLocalDate())) {
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
    }

}
