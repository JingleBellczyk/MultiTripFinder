package org.dyploma.trip.domain;

import org.dyploma.trip.domain.Trip;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.trip.transfer.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface TripService {
    Trip getUserTripById(Integer userId, Integer tripId);
    Trip getUserTripByName(Integer userId, String name);
    Trip createUserTrip(Integer userId, Trip trip, List<PlaceInTrip> places, List<Transfer> transfers, List<String> tags);
    void deleteUserTrip(Integer userId, Integer tripId);
    Trip updateUserTrip(Integer userId, Integer tripId, String name, List<String> tags);
    Page<Trip> getUserTrips(Integer userId, TripFilterRequest filterRequest, Pageable pageable);
    List<String> getUserTripNames(Integer userId);
}
