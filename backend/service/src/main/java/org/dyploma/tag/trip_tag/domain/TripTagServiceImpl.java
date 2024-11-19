package org.dyploma.tag.trip_tag.domain;

import org.dyploma.exception.ConflictException;
import org.dyploma.exception.NotFoundException;
import org.dyploma.exception.ValidationException;
import org.dyploma.useraccount.UserAccount;
import org.dyploma.useraccount.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripTagServiceImpl implements TripTagService {
    private final TripTagRepository tripTagRepository;
    private final UserAccountService userAccountService;

    @Autowired
    public TripTagServiceImpl(TripTagRepository tripTagRepository, UserAccountService userAccountService) {
        this.tripTagRepository = tripTagRepository;
        this.userAccountService = userAccountService;
    }

    @Override
    public List<TripTag> listUserTripTags(Integer userId) {
        return tripTagRepository.findByUserAccount(userAccountService.getUserById(userId));
    }

    @Override
    public TripTag updateUserTripTag(Integer userId, Integer tripTagId, String tag) {
        if (tag.isBlank()) {
            throw new ValidationException("Tag name cannot be empty");
        }
        tag = tag.trim().toLowerCase();
        UserAccount userAccount = userAccountService.getUserById(userId);
        TripTag tripTag = tripTagRepository.findByIdAndUserAccount(tripTagId, userAccount)
                .orElseThrow(() -> new NotFoundException("Trip tag not found"));
        if (!tag.equals(tripTag.getName()) && tripTagRepository.existsByNameAndUserAccount(tag, userAccount)) {
            throw new ConflictException("Tag with this name already exists");
        }
        tripTag.setName(tag);
        return tripTagRepository.save(tripTag);
    }

    @Override
    public void deleteUserTripTag(Integer userId, Integer tripTagId) {
        TripTag tag = tripTagRepository.findByIdAndUserAccount(tripTagId, userAccountService.getUserById(userId))
                .orElseThrow(() -> new NotFoundException("Tag not found"));

        tag.getTrips().forEach(trip -> trip.getTags().remove(tag));
        tripTagRepository.deleteById(tripTagId);
    }
}
