package org.dyploma.trip.domain;

import org.dyploma.exception.ConflictException;
import org.dyploma.exception.NotFoundException;
import org.dyploma.exception.ValidationException;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.tag.trip_tag.domain.TripTag;
import org.dyploma.tag.trip_tag.domain.TripTagRepository;
import org.dyploma.trip.transfer.Transfer;
import org.dyploma.useraccount.UserAccount;
import org.dyploma.useraccount.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final TripTagRepository tripTagRepository;
    private final UserAccountService userAccountService;

    @Autowired
    public TripServiceImpl(TripRepository tripRepository, TripTagRepository tripTagRepository, UserAccountService userAccountService) {
        this.tripRepository = tripRepository;
        this.tripTagRepository = tripTagRepository;
        this.userAccountService = userAccountService;
    }

    @Override
    public Trip getUserTripById(Integer userId, Integer tripId) {
        UserAccount userAccount = userAccountService.getUserById(userId);
        return tripRepository.findByIdAndUserAccount(tripId, userAccount).orElseThrow(() -> new NotFoundException("Trip not found"));
    }

    @Override
    public Trip getUserTripByName(Integer userId, String name) {
        UserAccount userAccount = userAccountService.getUserById(userId);
        return tripRepository.findByNameAndUserAccount(name, userAccount).orElseThrow(() -> new NotFoundException("Trip not found"));
    }

    @Override
    public Trip createUserTrip(Integer userId, Trip trip, List<PlaceInTrip> places, List<Transfer> transfers, List<String> tagNames) {
        UserAccount userAccount = userAccountService.getUserById(userId);
        if (tripRepository.existsByNameAndUserAccount(trip.getName(), userAccount)) {
            throw new ConflictException("Trip with this name already exists");
        }
        trip.setUserAccount(userAccount);
        places.forEach(trip::addPlace);
        transfers.forEach(trip::addTransfer);
        setTagsToTrip(userAccount, trip, tagNames);
        trip.setSaveDate(Date.valueOf(LocalDate.now()));
        return tripRepository.save(trip);
    }

    @Override
    public void deleteUserTrip(Integer userId, Integer tripId) {
        if (!tripRepository.existsByIdAndUserAccount(tripId, userAccountService.getUserById(userId))) {
            throw new NotFoundException("Trip not found");
        }
        tripRepository.deleteById(tripId);
    }

    @Override
    public Trip updateUserTrip(Integer userId, Integer tripId, String name, List<String> tags) {
        UserAccount userAccount = userAccountService.getUserById(userId);
        Trip trip = tripRepository.findByIdAndUserAccount(tripId, userAccount).orElseThrow(() -> new NotFoundException("Trip not found"));
        if (!name.equals(trip.getName()) && tripRepository.existsByNameAndUserAccount(name, userAccount)) {
            throw new ConflictException("Trip with this name already exists");
        }
        trip.setName(name);
        setTagsToTrip(userAccount, trip, tags);
        return tripRepository.save(trip);
    }

    @Override
    public Page<Trip> getUserTrips(Integer userId, TripFilterRequest filterRequest, Pageable pageable) {
        UserAccount userAccount = userAccountService.getUserById(userId);
        Specification<Trip> spec = Specification.where(TripSpecification.belongsToUserAccount(userAccount))
                .and(TripSpecification.withTags(filterRequest.getTags()))
                .and(TripSpecification.withDateRange(filterRequest.getFromDate(), filterRequest.getToDate()));
        return tripRepository.findAll(spec, pageable);
    }

    @Override
    public List<String> getUserTripNames(Integer userId) {
        UserAccount userAccount = userAccountService.getUserById(userId);
        return tripRepository.findAllNamesByUserAccount(userAccount);
    }

    private void setTagsToTrip(UserAccount userAccount, Trip trip, List<String> tagNames) {
        List<TripTag> existingTags = tripTagRepository.findByNameInAndUserAccount(tagNames, userAccount);

        Map<String, TripTag> existingTagMap = existingTags.stream()
                .collect(Collectors.toMap(TripTag::getName, tag -> tag));

        List<TripTag> tagsToAssociate = new ArrayList<>(existingTags);
        List<TripTag> newTags = tagNames.stream()
                .filter(tagName -> !existingTagMap.containsKey(tagName))
                .map(tagName -> TripTag.builder().name(tagName).userAccount(userAccount).build())
                .collect(Collectors.toList());

        if (!newTags.isEmpty()) {
            tripTagRepository.saveAll(newTags);
            tagsToAssociate.addAll(newTags);
        }
        trip.setTags(tagsToAssociate);
    }
}
