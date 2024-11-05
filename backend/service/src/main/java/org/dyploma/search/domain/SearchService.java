package org.dyploma.search.domain;

import org.dyploma.search.algorithm.request.SearchRequest;
import org.dyploma.search.algorithm.response.SearchResponseElement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SearchService {
    List<SearchResponseElement> search(SearchRequest searchRequest);
    Search getUserSearchById(UUID id, UUID userId);
    Search createUserSearch(Search search, UUID userId);
    void deleteUserSearch(UUID id);
    Search updateUserSearch(Search search, UUID userId);
    Page<Search> getUserSearches(UUID userId, Pageable pageable);
}
