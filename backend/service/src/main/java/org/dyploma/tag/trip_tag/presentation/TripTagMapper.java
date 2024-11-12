package org.dyploma.tag.trip_tag.presentation;

import org.dyploma.tag.trip_tag.domain.TripTag;

import java.util.List;

public class TripTagMapper {
    public static com.openapi.model.TripTag toTripTagApi(TripTag tripTag) {
        return new com.openapi.model.TripTag()
                .id(tripTag.getId())
                .name(tripTag.getName());
    }

    public static List<com.openapi.model.TripTag> toTripTagsApi(List<TripTag> tripTags) {
        return tripTags.stream()
                .map(TripTagMapper::toTripTagApi)
                .toList();
    }
}
