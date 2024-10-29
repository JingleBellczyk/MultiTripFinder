package org.dyploma.search.domain;

import org.dyploma.search.dto.request.SearchRequest;
import org.dyploma.search.dto.response.TripResponse;
import org.dyploma.search.validator.SearchRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<TripResponse> search(SearchRequest searchRequest) {
        searchRequestValidator.validate(searchRequest);
        return List.of();
    }
}
