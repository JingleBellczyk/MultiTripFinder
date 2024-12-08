package org.dyploma.tag.trip_tag.presentation;

import com.openapi.api.TripTagApi;
import com.openapi.model.TripTag;
import com.openapi.model.TagUpdating;
import org.dyploma.tag.trip_tag.domain.TripTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:80","http://frontend:80"})
@RestController
public class TripTagController implements TripTagApi {
    private final TripTagService tripTagService;

    @Autowired
    public TripTagController(TripTagService tripTagService) {
        this.tripTagService = tripTagService;
    }

    @Override
    public ResponseEntity<TripTag> updateUserTripTag(Integer userId, Integer tripTagId, TagUpdating tagUpdating) {
        return ResponseEntity.ok(TripTagMapper.mapToTripTagApi(tripTagService.updateUserTripTag(userId, tripTagId, TripTagMapper.mapToTripTag(tagUpdating.getName()))));
    }

    @Override
    public ResponseEntity<Void> deleteUserTripTag(Integer userId, Integer tripTagId) {
        tripTagService.deleteUserTripTag(userId, tripTagId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<TripTag>> listUserTripTags(Integer userId) {
        return ResponseEntity.ok(TripTagMapper.mapToTripTagsApi(tripTagService.listUserTripTags(userId)));
    }
}
