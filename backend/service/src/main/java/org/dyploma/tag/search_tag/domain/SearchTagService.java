package org.dyploma.tag.search_tag.domain;

import java.util.List;

public interface SearchTagService {
    List<SearchTag> listUserSearchTags(Integer userId);
    SearchTag updateUserSearchTag(Integer userId, Integer searchTagId, String tag);
    void deleteUserSearchTag(Integer userId, Integer searchTagId);
}
