package org.dyploma.tag.trip_tag.domain;

import org.dyploma.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripTagServiceImpl implements TripTagService {
    private final TripTagRepository tripTagRepository;

    @Autowired
    public TripTagServiceImpl(TripTagRepository tripTagRepository) {
        this.tripTagRepository = tripTagRepository;
    }

    private TripTag findUserTripTag(Integer userId, Integer tripTagId) {
        return tripTagRepository.findById(tripTagId)
                .orElseThrow(() -> new NotFoundException("Trip tag not found"));
    }

    @Override
    public List<TripTag> listUserTripTags(Integer userId) {
        return tripTagRepository.findAll();
    }

    @Override
    public TripTag updateUserTripTag(Integer userId, Integer tripTagId, String tag) {
        TripTag tripTag = findUserTripTag(userId, tripTagId);
        tripTag.setName(tag);
        return tripTagRepository.save(tripTag);
    }

    @Override
    public void deleteUserTripTag(Integer userId, Integer tripTagId) {
        TripTag tag = tripTagRepository.findById(tripTagId)
                .orElseThrow(() -> new NotFoundException("Tag not found"));

        // Clear the association with Trip entities
        tag.getTrips().forEach(trip -> trip.getTags().remove(tag));
        tripTagRepository.deleteById(tripTagId);
    }
}
