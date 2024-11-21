package org.dyploma.tag.trip_tag.presentation;

import org.dyploma.exception.ValidationException;
import org.dyploma.tag.trip_tag.domain.TripTag;

import java.util.List;

public class TripTagMapper {
    public static com.openapi.model.TripTag mapToTripTagApi(TripTag tripTag) {
        return new com.openapi.model.TripTag()
                .id(tripTag.getId())
                .name(tripTag.getName());
    }

    public static List<com.openapi.model.TripTag> mapToTripTagsApi(List<TripTag> tripTags) {
        return tripTags.stream()
                .map(TripTagMapper::mapToTripTagApi)
                .toList();
    }

    public static List<String> mapToTripTags(List<String> tripTags) {
        return tripTags.stream()
                .map(TripTagMapper::mapToTripTag)
                .distinct()
                .toList();
    }

    public static String mapToTripTag(String tripTag) {
        if (tripTag.isBlank()) throw new ValidationException("Tag name cannot be empty");
        return tripTag.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}
