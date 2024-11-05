package org.dyploma.search.domain;

import org.dyploma.search.algorithm.request.SearchRequest;
import org.dyploma.search.algorithm.response.SearchResponseElement;
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
    public List<SearchResponseElement> search(SearchRequest searchRequest) {
        searchRequestValidator.validate(searchRequest);
        return List.of();
    }
}
