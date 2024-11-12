package org.dyploma.tag.trip_tag.domain;

import java.util.List;

public interface TripTagService {
    List<TripTag> listUserTripTags(Integer userId);
    TripTag updateUserTripTag(Integer userId, Integer tripTagId, String tag);
    void deleteUserTripTag(Integer userId, Integer tripTagId);
}
