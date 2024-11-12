package org.dyploma.tag.search_tag.domain;

import org.dyploma.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchTagServiceImpl implements SearchTagService {
    private final SearchTagRepository searchTagRepository;

    @Autowired
    public SearchTagServiceImpl(SearchTagRepository searchTagRepository) {
        this.searchTagRepository = searchTagRepository;
    }

    private SearchTag findUserSearchTag(Integer userId, Integer searchTagId) {
        return searchTagRepository.findById(searchTagId)
                .orElseThrow(() -> new NotFoundException("Search tag not found"));
    }

    @Override
    public List<SearchTag> listUserSearchTags(Integer userId) {
        return searchTagRepository.findAll();
    }

    @Override
    public SearchTag updateUserSearchTag(Integer userId, Integer searchTagId, String tag) {
        SearchTag searchTag = findUserSearchTag(userId, searchTagId);
        searchTag.setName(tag);
        return searchTagRepository.save(searchTag);
    }

    @Override
    public void deleteUserSearchTag(Integer userId, Integer searchTagId) {
        SearchTag tag = searchTagRepository.findById(searchTagId)
                .orElseThrow(() -> new NotFoundException("Tag not found"));

        // Clear the association with Search entities
        tag.getSearches().forEach(search -> search.getTags().remove(tag));
        searchTagRepository.deleteById(searchTagId);
    }
}
