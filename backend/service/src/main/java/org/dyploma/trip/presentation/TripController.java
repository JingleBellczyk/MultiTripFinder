package org.dyploma.trip.presentation;

import com.openapi.api.TripListApi;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:80","http://frontend:80"})
@RestController
public class TripController implements TripListApi {

}
