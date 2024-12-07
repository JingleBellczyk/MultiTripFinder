package org.dyploma.tripTests;

import org.dyploma.exception.ConflictException;
import org.dyploma.exception.NotFoundException;
import org.dyploma.tag.trip_tag.domain.TripTag;
import org.dyploma.tag.trip_tag.domain.TripTagRepository;
import org.dyploma.trip.domain.Trip;
import org.dyploma.trip.domain.TripRepository;
import org.dyploma.trip.domain.TripServiceImpl;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.trip.transfer.Transfer;
import org.dyploma.useraccount.UserAccount;
import org.dyploma.useraccount.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripServiceImplTest {

    @Mock
    private TripRepository tripRepository;
    @Mock
    private TripTagRepository tripTagRepository;
    @Mock
    private UserAccountService userAccountService;

    private TripServiceImpl tripService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tripService = new TripServiceImpl(tripRepository, tripTagRepository, userAccountService);
    }

    @Test
    void testGetUserTripById() {
        Integer userId = 1;
        Integer tripId = 10;
        UserAccount userAccount = new UserAccount();
        userAccount.setId(userId);
        Trip trip = new Trip();
        trip.setId(tripId);
        trip.setUserAccount(userAccount);

        when(userAccountService.getUserById(userId)).thenReturn(userAccount);
        when(tripRepository.findByIdAndUserAccount(tripId, userAccount)).thenReturn(Optional.of(trip));

        Trip result = tripService.getUserTripById(userId, tripId);

        assertNotNull(result);
        assertEquals(tripId, result.getId());
        verify(userAccountService).getUserById(userId);
        verify(tripRepository).findByIdAndUserAccount(tripId, userAccount);
    }

    @Test
    void testGetUserTripByIdWhenTripNotFound() {
        Integer userId = 1;
        Integer tripId = 10;
        UserAccount userAccount = new UserAccount();
        userAccount.setId(userId);

        when(userAccountService.getUserById(userId)).thenReturn(userAccount);
        when(tripRepository.findByIdAndUserAccount(tripId, userAccount)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> tripService.getUserTripById(userId, tripId));
        verify(userAccountService).getUserById(userId);
        verify(tripRepository).findByIdAndUserAccount(tripId, userAccount);
    }

    @Test
    void testCreateTrip() {
        Integer userId = 1;
        Trip trip = new Trip();
        trip.setName("Test Trip");
        UserAccount userAccount = new UserAccount();
        userAccount.setId(userId);
        List<PlaceInTrip> places = Collections.emptyList();
        List<Transfer> transfers = Collections.emptyList();
        List<String> tags = List.of("Adventure", "Vacation");

        when(userAccountService.getUserById(userId)).thenReturn(userAccount);
        when(tripRepository.existsByNameAndUserAccount(trip.getName(), userAccount)).thenReturn(false);
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        Trip result = tripService.createUserTrip(userId, trip, places, transfers, tags);

        assertNotNull(result);
        assertEquals("Test Trip", result.getName());
        verify(tripRepository).save(trip);
    }

    @Test
    void testCreateDuplicateTrip() {
        Integer userId = 1;
        Trip trip = new Trip();
        trip.setName("Duplicate Trip");
        UserAccount userAccount = new UserAccount();
        userAccount.setId(userId);

        when(userAccountService.getUserById(userId)).thenReturn(userAccount);
        when(tripRepository.existsByNameAndUserAccount(trip.getName(), userAccount)).thenReturn(true);

        assertThrows(ConflictException.class, () -> tripService.createUserTrip(userId, trip, Collections.emptyList(), Collections.emptyList(), List.of("Tag1")));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void testDeleteTrip() {
        Integer userId = 1;
        Integer tripId = 10;
        UserAccount userAccount = new UserAccount();
        userAccount.setId(userId);

        when(userAccountService.getUserById(userId)).thenReturn(userAccount);
        when(tripRepository.existsByIdAndUserAccount(tripId, userAccount)).thenReturn(true);

        tripService.deleteUserTrip(userId, tripId);

        verify(tripRepository).deleteById(tripId);
    }

    @Test
    void testDeleteTripWhichDontExist() {
        Integer userId = 1;
        Integer tripId = 10;
        UserAccount userAccount = new UserAccount();
        userAccount.setId(userId);

        when(userAccountService.getUserById(userId)).thenReturn(userAccount);
        when(tripRepository.existsByIdAndUserAccount(tripId, userAccount)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> tripService.deleteUserTrip(userId, tripId));
        verify(tripRepository, never()).deleteById(anyInt());
    }

    @Test
    void testCreateTripAndSetTags() {
        UserAccount userAccount = new UserAccount();
        userAccount.setId(1);
        when(userAccountService.getUserById(1)).thenReturn(userAccount);

        Trip trip = new Trip();
        trip.setName("Test Trip");

        List<PlaceInTrip> places = List.of(new PlaceInTrip());
        List<Transfer> transfers = List.of(new Transfer());
        List<String> tagNames = List.of("Tag1", "Tag2");

        TripTag existingTag = TripTag.builder().name("Tag1").userAccount(userAccount).build();
        when(tripTagRepository.findByNameInAndUserAccount(tagNames, userAccount)).thenReturn(List.of(existingTag));

        when(tripRepository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trip result = tripService.createUserTrip(1, trip, places, transfers, tagNames);

        assertEquals("Test Trip", result.getName());
        assertEquals(2, result.getTags().size());
        verify(tripTagRepository).saveAll(anyList());
    }

}
