package org.dyploma.tag.search_tag.presentation;

import org.dyploma.tag.search_tag.domain.SearchTag;

import java.util.List;

public class SearchTagMapper {
    public static com.openapi.model.SearchTag toSearchTagApi(SearchTag searchTag) {
        return new com.openapi.model.SearchTag()
                .id(searchTag.getId())
                .name(searchTag.getName());
    }

    public static List<com.openapi.model.SearchTag> toSearchTagsApi(List<SearchTag> searchTags) {
        return searchTags.stream()
                .map(SearchTagMapper::toSearchTagApi)
                .toList();
    }
}
