package org.dyploma.search.presentation;

import com.openapi.model.SearchRequest;
import com.openapi.model.SearchResponse;
import org.dyploma.search.domain.SearchMapper;
import org.dyploma.search.domain.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import com.openapi.api.SearchApi;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:80","http://frontend:80"})
@RestController
public class SearchController implements SearchApi {
    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public ResponseEntity<SearchResponse> search(SearchRequest searchRequest) {
        return ResponseEntity.ok(SearchMapper.mapToSearchResponse(searchService.search(SearchMapper.mapToSearchRequest(searchRequest))));
    }
}
