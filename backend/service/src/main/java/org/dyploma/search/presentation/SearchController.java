package org.dyploma.search.presentation;

import com.openapi.api.SearchListApi;
import com.openapi.model.*;
import org.dyploma.search.domain.SearchService;
import org.dyploma.search.place.PlaceInSearchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import com.openapi.api.SearchApi;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:80","http://frontend:80"})
@RestController
public class SearchController implements SearchApi, SearchListApi {
    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public ResponseEntity<SearchResponse> search(SearchRequest searchRequest) {
        return ResponseEntity.ok(SearchMapper.mapToSearchResponseApi(searchService.search(SearchMapper.mapToSearchRequest(searchRequest))));
    }

    @Override
    public ResponseEntity<Void> deleteSearch(Integer userId, Integer searchId) {
        searchService.deleteUserSearch(userId, searchId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Search> findUserSearch(Integer userId, Integer searchId) {
        return ResponseEntity.ok(SearchMapper.mapToSearchApi(searchService.getUserSearchById(userId, searchId)));
    }

    @Override
    public ResponseEntity<Search> findUserSearchByName(Integer userId, String searchName) {
        return ResponseEntity.ok(SearchMapper.mapToSearchApi(searchService.getUserSearchByName(userId, searchName)));
    }

    @Override
    public ResponseEntity<Search> saveSearch(Integer userId, Search search) {
        return ResponseEntity.ok(SearchMapper.mapToSearchApi(searchService.createUserSearch(
                                                        userId,
                                                        SearchMapper.mapToSearch(search),
                                                        PlaceInSearchMapper.mapToPlacesInSearch(search.getPlacesToVisit()),
                                                        search.getTags())));
    }

    @Override
    public ResponseEntity<Search> updateSearch(Integer userId, Integer searchId, SearchUpdating searchUpdating) {
        return ResponseEntity.ok(SearchMapper.mapToSearchApi(searchService.updateUserSearch(userId, searchId, searchUpdating.getName(), searchUpdating.getTags())));
    }

    @Override
    public ResponseEntity<SearchPage> listUserSearch(Integer userId, Integer page, Integer size, CriteriaMode optimizationCriteria, List<TransportMode> preferredTransports, LocalDate saveDate, List<String> searchTags) {
        return ResponseEntity.ok(SearchMapper.mapToSearchPageApi(searchService.getUserSearches(userId, SearchMapper.mapToSearchFilterRequest(optimizationCriteria, preferredTransports, saveDate, searchTags), Pageable.ofSize(size).withPage(page))));
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return SearchApi.super.getRequest();
    }
}
