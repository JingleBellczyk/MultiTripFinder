package org.dyploma.search.domain;

import org.dyploma.search.algorithm.request.SearchRequest;
import org.dyploma.search.algorithm.response.SearchResponseElement;
import org.dyploma.search.validator.SearchRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SearchServiceImpl implements SearchService {
    private final SearchRepository searchRepository;
    private final SearchRequestValidator searchRequestValidator;

    @Autowired
    public SearchServiceImpl(SearchRepository searchRepository, SearchRequestValidator searchRequestValidator) {
        this.searchRepository = searchRepository;
        this.searchRequestValidator = searchRequestValidator;
    }

    @Override
    public List<SearchResponseElement> search(SearchRequest searchRequest) {
        searchRequestValidator.validate(searchRequest);
        return List.of();
    }

    @Override
    public Search getUserSearchById(UUID id, UUID userId) {
        return null;
    }

    @Override
    public Search createUserSearch(Search search, UUID userId) {
        return null;
    }

    @Override
    public void deleteUserSearch(UUID id) {

    }

    @Override
    public Search updateUserSearch(Search search, UUID userId) {
        return null;
    }

    @Override
    public Page<Search> getUserSearches(UUID userId, Pageable pageable) {
        return null;
    }
}
