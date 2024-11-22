package org.dyploma.tag.search_tag.presentation;

import org.dyploma.exception.ValidationException;
import org.dyploma.tag.search_tag.domain.SearchTag;

import java.util.List;

public class SearchTagMapper {
    public static com.openapi.model.SearchTag mapToSearchTagApi(SearchTag searchTag) {
        return new com.openapi.model.SearchTag()
                .id(searchTag.getId())
                .name(searchTag.getName());
    }

    public static List<com.openapi.model.SearchTag> mapToSearchTagsApi(List<SearchTag> searchTags) {
        return searchTags.stream()
                .map(SearchTagMapper::mapToSearchTagApi)
                .toList();
    }

    public static List<String> mapToSearchTags(List<String> searchTags) {
        return searchTags.stream()
                .map(SearchTagMapper::mapToSearchTag)
                .distinct()
                .toList();
    }

    public static String mapToSearchTag(String searchTag) {
        if (searchTag.isBlank()) throw new ValidationException("Tag name cannot be empty");
        return searchTag.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}
