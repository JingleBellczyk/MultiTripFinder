package org.dyploma.tag.search_tag.presentation;

import com.openapi.api.SearchTagApi;
import com.openapi.model.SearchTag;
import com.openapi.model.TagUpdating;
import org.dyploma.tag.search_tag.domain.SearchTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:80","http://frontend:80"})
@RestController
public class SearchTagController implements SearchTagApi {
    private final SearchTagService searchTagService;

    @Autowired
    public SearchTagController(SearchTagService searchTagService) {
        this.searchTagService = searchTagService;
    }

    @Override
    public ResponseEntity<SearchTag> updateUserSearchTag(Integer userId, Integer searchTagId, TagUpdating tagUpdating) {
        return ResponseEntity.ok(SearchTagMapper.mapToSearchTagApi(searchTagService.updateUserSearchTag(userId, searchTagId, SearchTagMapper.mapToSearchTag(tagUpdating.getName()))));
    }

    @Override
    public ResponseEntity<Void> deleteUserSearchTag(Integer userId, Integer searchTagId) {
        searchTagService.deleteUserSearchTag(userId, searchTagId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<SearchTag>> listUserSearchTags(Integer userId) {
        return ResponseEntity.ok(SearchTagMapper.mapToSearchTagsApi(searchTagService.listUserSearchTags(userId)));
    }
}
