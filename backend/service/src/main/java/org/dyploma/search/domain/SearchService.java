package org.dyploma.search.domain;

import org.dyploma.search.algorithm.request.SearchRequest;
import org.dyploma.search.algorithm.response.SearchResponseElement;

import java.util.List;

public interface SearchService {
    List<SearchResponseElement> search(SearchRequest searchRequest);
}
