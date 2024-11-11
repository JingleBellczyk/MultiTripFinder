package org.dyploma.search.domain;

import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.trip.domain.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface SearchService {
    List<Trip> search(SearchRequest searchRequest);
    Search getUserSearchById(Integer userId, Integer searchId);
    Search getUserSearchByName(Integer userId, String name);
    Search createUserSearch(Integer userId, Search search, List<PlaceInSearch> places, List<String> tags);
    void deleteUserSearch(Integer searchId);
    Search updateUserSearch(Integer userId, Integer searchId, String name, List<String> tags);
    Page<Search> getUserSearches(Integer userId, Pageable pageable);
}
