package org.dyploma.tripTests;

import com.openapi.model.PlaceInTrip;
import org.dyploma.trip.place.PlaceInTripMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

public class PlaceInTripMapperTest {

    @Test
    void testMapToPlacesInTrip() {
        com.openapi.model.PlaceInTrip placeInTripApi1 = Mockito.mock(com.openapi.model.PlaceInTrip.class);
        com.openapi.model.PlaceInTrip placeInTripApi2 = Mockito.mock(com.openapi.model.PlaceInTrip.class);

        Mockito.when(placeInTripApi1.getCountry()).thenReturn("Poland");
        Mockito.when(placeInTripApi1.getCity()).thenReturn("Warsaw");
        Mockito.when(placeInTripApi1.getStayDuration()).thenReturn(3);
        Mockito.when(placeInTripApi1.getVisitOrder()).thenReturn(1);
        Mockito.when(placeInTripApi1.getIsTransfer()).thenReturn(false);

        Mockito.when(placeInTripApi2.getCountry()).thenReturn("Germany");
        Mockito.when(placeInTripApi2.getCity()).thenReturn("Berlin");
        Mockito.when(placeInTripApi2.getStayDuration()).thenReturn(4);
        Mockito.when(placeInTripApi2.getVisitOrder()).thenReturn(2);
        Mockito.when(placeInTripApi2.getIsTransfer()).thenReturn(true);

        List<com.openapi.model.PlaceInTrip> placesInTripApi = List.of(placeInTripApi1, placeInTripApi2);

        List<org.dyploma.trip.place.PlaceInTrip> placesInTrip = PlaceInTripMapper.mapToPlacesInTrip(placesInTripApi);

        assertNotNull(placesInTrip);
        assertEquals(2, placesInTrip.size());

        org.dyploma.trip.place.PlaceInTrip mappedPlace1 = placesInTrip.get(0);
        org.dyploma.trip.place.PlaceInTrip mappedPlace2 = placesInTrip.get(1);

        // Sprawdzanie wartości z uwzględnieniem toLowerCase()
        assertEquals("poland", mappedPlace1.getCountry());
        assertEquals("warsaw", mappedPlace1.getCity());
        assertEquals(3, mappedPlace1.getStayDuration());
        assertEquals(1, mappedPlace1.getVisitOrder());
        assertFalse(mappedPlace1.isTransfer());

        assertEquals("germany", mappedPlace2.getCountry());
        assertEquals("berlin", mappedPlace2.getCity());
        assertEquals(4, mappedPlace2.getStayDuration());
        assertEquals(2, mappedPlace2.getVisitOrder());
        assertTrue(mappedPlace2.isTransfer());
    }

    @Test
    void testMapToPlaceInTripApi() {
        org.dyploma.trip.place.PlaceInTrip placeInTrip = new org.dyploma.trip.place.PlaceInTrip();
        placeInTrip.setCountry("poland");
        placeInTrip.setCity("warsaw");
        placeInTrip.setStayDuration(3);
        placeInTrip.setVisitOrder(1);
        placeInTrip.setTransfer(false);

        com.openapi.model.PlaceInTrip placeInTripApi = PlaceInTripMapper.mapToPlaceInTripApi(placeInTrip);

        assertNotNull(placeInTripApi);
        assertEquals("poland", placeInTripApi.getCountry());
        assertEquals("warsaw", placeInTripApi.getCity());
        assertEquals(3, placeInTripApi.getStayDuration());
        assertEquals(1, placeInTripApi.getVisitOrder());
        assertFalse(placeInTripApi.getIsTransfer());
    }

}
