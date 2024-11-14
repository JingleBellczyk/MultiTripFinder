package org.dyploma.tag.search_tag.domain;

import org.dyploma.exception.ConflictException;
import org.dyploma.exception.NotFoundException;
import org.dyploma.exception.ValidationException;
import org.dyploma.useraccount.UserAccount;
import org.dyploma.useraccount.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchTagServiceImpl implements SearchTagService {
    private final SearchTagRepository searchTagRepository;
    private final UserAccountService userAccountService;

    @Autowired
    public SearchTagServiceImpl(SearchTagRepository searchTagRepository, UserAccountService userAccountService) {
        this.searchTagRepository = searchTagRepository;
        this.userAccountService = userAccountService;
    }

    @Override
    public List<SearchTag> listUserSearchTags(Integer userId) {
        return searchTagRepository.findByUserAccount(userAccountService.getUserById(userId));
    }

    @Override
    public SearchTag updateUserSearchTag(Integer userId, Integer searchTagId, String tag) {
        if (tag.isBlank()) {
            throw new ValidationException("Tag name cannot be empty");
        }
        tag = tag.trim().toLowerCase();
        UserAccount userAccount = userAccountService.getUserById(userId);
        SearchTag searchTag = searchTagRepository.findByIdAndUserAccount(searchTagId, userAccount)
                .orElseThrow(() -> new NotFoundException("Search tag not found"));
        if (!tag.equals(searchTag.getName()) && searchTagRepository.existsByNameAndUserAccount(tag, userAccount)) {
            throw new ConflictException("Tag with this name already exists");
        }
        searchTag.setName(tag);
        return searchTagRepository.save(searchTag);
    }

    @Override
    public void deleteUserSearchTag(Integer userId, Integer searchTagId) {
        SearchTag tag = searchTagRepository.findByIdAndUserAccount(searchTagId, userAccountService.getUserById(userId))
                .orElseThrow(() -> new NotFoundException("Tag not found"));

        tag.getSearches().forEach(search -> search.getTags().remove(tag));
        searchTagRepository.deleteById(searchTagId);
    }
}
