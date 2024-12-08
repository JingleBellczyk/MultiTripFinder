package org.dyploma.trip.presentation;

import com.openapi.api.TripListApi;
import com.openapi.model.*;
import org.dyploma.tag.trip_tag.presentation.TripTagMapper;
import org.dyploma.trip.domain.TripService;
import org.dyploma.trip.place.PlaceInTripMapper;
import org.dyploma.trip.transfer.TransferMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:80","http://frontend:80"})
@RestController
public class TripController implements TripListApi {
    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @Override
    public ResponseEntity<Void> deleteTrip(Integer userId, Integer tripId) {
        tripService.deleteUserTrip(userId, tripId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Trip> findUserTrip(Integer userId, Integer tripId) {
        return ResponseEntity.ok(TripMapper.mapToTripApi(tripService.getUserTripById(userId, tripId)));
    }

    @Override
    public ResponseEntity<Trip> findUserTripByName(Integer userId, String tripName) {
        return ResponseEntity.ok(TripMapper.mapToTripApi(tripService.getUserTripByName(userId, tripName)));
    }

    @Override
    public ResponseEntity<Trip> saveTrip(Integer userId, Trip trip) {
        return ResponseEntity.ok(TripMapper.mapToTripApi(tripService.createUserTrip(
                                                        userId,
                                                        TripMapper.mapToTrip(trip),
                                                        PlaceInTripMapper.mapToPlacesInTrip(trip.getPlaces()),
                                                        TransferMapper.mapToTransfers(trip.getTransfers()),
                                                        TripTagMapper.mapToTripTags(trip.getTags()))));
    }

    @Override
    public ResponseEntity<Trip> updateTrip(Integer userId, Integer tripId, TripUpdating tripUpdating) {
        return ResponseEntity.ok(TripMapper.mapToTripApi(tripService.updateUserTrip(
                userId,
                tripId,
                tripUpdating.getName().trim().replaceAll("\\s+", " ").toLowerCase(),
                TripTagMapper.mapToTripTags(tripUpdating.getTags()))));
    }

    @Override
    public ResponseEntity<TripPage> listUserTrip(Integer userId, Integer page, Integer size, LocalDate fromDate, LocalDate toDate, List<String> tripTags) {
        return ResponseEntity.ok(TripMapper.mapToTripPageApi(tripService.getUserTrips(
                        userId,
                        TripMapper.mapToTripFilterRequest(fromDate, toDate, tripTags),
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "saveDate"))))
        );
    }

    @Override
    public ResponseEntity<List<String>> listUserTripNames(Integer userId) {
        return ResponseEntity.ok(tripService.getUserTripNames(userId));
    }
}
