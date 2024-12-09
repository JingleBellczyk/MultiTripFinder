package org.dyploma.tripTests;
import com.openapi.model.TripPage;
import org.dyploma.tag.trip_tag.domain.TripTag;
import org.dyploma.trip.domain.Trip;
import org.dyploma.trip.domain.TripFilterRequest;
import org.dyploma.trip.domain.TripService;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.trip.presentation.TripController;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.dyploma.trip.transfer.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.lang.reflect.Constructor;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class TripControllerTest {

    @Mock
    private TripService tripService;

    @InjectMocks
    private TripController tripController;

    private MockMvc mockMvc;

    private Trip trip;

    private com.openapi.model.Trip openApiTrip;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tripController).build();

        trip = new org.dyploma.trip.domain.Trip();
        trip.setId(1);
        trip.setName("My Trip");

        Transfer transfer = new org.dyploma.trip.transfer.Transfer();
        transfer.setId(1);
        transfer.setTransportMode(org.dyploma.transport.TransportMode.BUS);
        transfer.setCarrier("Carrier Example");
        transfer.setStartDateTime(LocalDateTime.now());
        transfer.setEndDateTime(LocalDateTime.now().plusHours(2));
        transfer.setDuration(120);
        transfer.setCost(50.0);
        transfer.setStartAddress("Start Address");
        transfer.setEndAddress("End Address");
        transfer.setTransferOrder(1);

        trip.setTransfers(List.of(transfer));

        PlaceInTrip placeInTrip1 = new org.dyploma.trip.place.PlaceInTrip();
        placeInTrip1.setId(1);
        placeInTrip1.setCountry("Country Example 1");
        placeInTrip1.setCity("City Example 1");
        placeInTrip1.setTransfer(false);
        placeInTrip1.setStayDuration(3);
        placeInTrip1.setVisitOrder(1);
        placeInTrip1.setTrip(trip);

        org.dyploma.trip.place.PlaceInTrip placeInTrip2 = new org.dyploma.trip.place.PlaceInTrip();
        placeInTrip2.setId(2);
        placeInTrip2.setCountry("Country Example 2");
        placeInTrip2.setCity("City Example 2");
        placeInTrip2.setTransfer(false);
        placeInTrip2.setStayDuration(5);
        placeInTrip2.setVisitOrder(2);
        placeInTrip2.setTrip(trip);

        trip.setPlaces(List.of(placeInTrip1, placeInTrip2));


        openApiTrip = new com.openapi.model.Trip();
        openApiTrip.setName("test");
        openApiTrip.setId(1);
    }

    @Test
    void testDeleteTrip() throws Exception {
        Integer userId = 1;
        Integer tripId = 1;

        doNothing().when(tripService).deleteUserTrip(userId, tripId);

        mockMvc.perform(delete("/tripList/{userId}/{tripId}", userId, tripId))
                .andExpect(status().isOk());

        verify(tripService, times(1)).deleteUserTrip(userId, tripId);
    }

    @Test
    void testFindUserTrip() throws Exception {
        Integer userId = 1;
        Integer tripId = 1;

        when(tripService.getUserTripById(userId, tripId)).thenReturn(trip);

        mockMvc.perform(get("/tripList/{userId}/{tripId}", userId, tripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("My Trip"))
                .andExpect(jsonPath("$.transfers[0].transferOrder").value(1))
                .andExpect(jsonPath("$.transfers[0].carrier").value("Carrier Example"))
                .andExpect(jsonPath("$.places[0].visitOrder").value(1))
                .andExpect(jsonPath("$.places[0].country").value("Country Example 1"))
                .andExpect(jsonPath("$.places[0].city").value("City Example 1"))
                .andExpect(jsonPath("$.places[1].visitOrder").value(2))
                .andExpect(jsonPath("$.places[1].country").value("Country Example 2"))
                .andExpect(jsonPath("$.places[1].city").value("City Example 2"));

        verify(tripService, times(1)).getUserTripById(userId, tripId);
    }

    @Test
    void testFindUserTripByName() throws Exception {
        Integer userId = 1;
        String tripName = "My Trip";

        when(tripService.getUserTripByName(userId, tripName)).thenReturn(trip);

        mockMvc.perform(get("/tripList/{userId}/name/{tripName}", userId, tripName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("My Trip"))
                .andExpect(jsonPath("$.transfers[0].transferOrder").value(1))
                .andExpect(jsonPath("$.transfers[0].carrier").value("Carrier Example"))
                .andExpect(jsonPath("$.places[0].visitOrder").value(1))
                .andExpect(jsonPath("$.places[0].country").value("Country Example 1"))
                .andExpect(jsonPath("$.places[0].city").value("City Example 1"))
                .andExpect(jsonPath("$.places[1].visitOrder").value(2))
                .andExpect(jsonPath("$.places[1].country").value("Country Example 2"))
                .andExpect(jsonPath("$.places[1].city").value("City Example 2"));

        verify(tripService, times(1)).getUserTripByName(userId, tripName);
    }


    @Test
    void testSaveTrip() throws Exception {
        String requestJson = """
                {
                  "id": 1,
                  "name": "My Trip",
                  "saveDate": "2024-12-06",
                  "tags": ["string"],
                  "startDate": "2024-12-06",
                  "endDate": "2024-12-06",
                  "passengerCount": 1,
                  "totalCost": 0,
                  "totalTransferTime": 0,
                  "duration": 0,
                  "transfers": [
                    {
                      "startDateTime": "2024-12-06T20:47:28.169Z",
                      "endDateTime": "2024-12-06T20:47:28.169Z",
                      "carrier": "string",
                      "transportMode": "BUS",
                      "cost": 0,
                      "duration": 0,
                      "transferOrder": 1,
                      "startAddress": "string",
                      "endAddress": "string"
                    }
                  ],
                  "places": [
                    {
                      "country": "string",
                      "city": "string",
                      "isTransfer": false,
                      "stayDuration": 0,
                      "visitOrder": 1
                    },
                    {
                      "country": "string",
                      "city": "string",
                      "isTransfer": false,
                      "stayDuration": 0,
                      "visitOrder": 1
                    }
                  ]
                }""";

        Integer userId = 1;

        when(tripService.createUserTrip(eq(userId), any(org.dyploma.trip.domain.Trip.class), anyList(), anyList(), anyList()))
                .thenReturn(trip);

        mockMvc.perform(post("/tripList/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("My Trip"));

        verify(tripService, times(1)).createUserTrip(eq(userId), any(org.dyploma.trip.domain.Trip.class),
                anyList(), anyList(), anyList());
    }

    @Test
    void testUpdateTrip() throws Exception {
        String requestJson = """
                {
                  "name": "updated name",
                  "tags": ["updated tag"]
                }""";

        Integer userId = 1;
        Integer tripId = 1;

        Trip updatedtrip = trip;
        updatedtrip.setName("updated name");
        TripTag tag = new TripTag();
        tag.setName("updated tag");
        updatedtrip.setTags(List.of(tag));

        when(tripService.updateUserTrip(eq(userId), eq(tripId), eq("updated name"), anyList()))
                .thenReturn(updatedtrip);

        mockMvc.perform(put("/tripList/{userId}/{tripId}", userId, tripId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updated name"))
                .andExpect(jsonPath("$.tags[0]").value("updated tag"));

        verify(tripService, times(1)).updateUserTrip(eq(userId), eq(tripId), eq("updated name"), anyList());
    }

    @Test
    void testListUserTripNames() throws Exception {
        Integer userId = 1;
        List<String> mockTripNames = Arrays.asList("Trip to Paris", "Trip to Tokyo");
        when(tripService.getUserTripNames(userId)).thenReturn(mockTripNames);

        mockMvc.perform(get("/tripList/{userId}/names", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("Trip to Paris"))
                .andExpect(jsonPath("$[1]").value("Trip to Tokyo"));
    }

    @Test
    void testListUserTrip() throws Exception {
        // Input data
        Integer userId = 1;
        int page = 0;
        int size = 5;
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);
        List<String> tripTags = List.of("vacation", "summer");

        Constructor<TripFilterRequest> constructor = TripFilterRequest.class.getDeclaredConstructor(List.class, Date.class, Date.class);
        constructor.setAccessible(true);
        TripFilterRequest filterRequest = constructor.newInstance(tripTags, Date.valueOf(fromDate), Date.valueOf(toDate));

        List<com.openapi.model.Trip> apiTrips = List.of(openApiTrip);
        TripPage tripPage = new TripPage();
        tripPage.setContent(apiTrips);

        List<Trip> trips = List.of(trip);
        Page<Trip> expectedTripPage = new PageImpl<>(trips);

        when(tripService.getUserTrips(eq(userId), eq(filterRequest), eq(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "saveDate")))))
                .thenReturn(expectedTripPage);

        mockMvc.perform(get("/tripList/{userId}", userId)
                        .param("page", Integer.toString(page))
                        .param("size", Integer.toString(size))
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString())
                        .param("tripTags", String.join(",", tripTags)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content[0]").exists());

        verify(tripService, times(1)).getUserTrips(eq(userId), eq(filterRequest), eq(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "saveDate"))));
    }
}