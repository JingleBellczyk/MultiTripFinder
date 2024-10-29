package org.dyploma.search.domain;

import org.dyploma.search.dto.request.SearchRequest;
import org.dyploma.search.dto.response.TripResponse;

import java.util.List;

public interface SearchService {
    List<TripResponse> search(SearchRequest searchRequest);
}
