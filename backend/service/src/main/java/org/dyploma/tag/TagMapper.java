package org.dyploma.tag;

public class TagMapper {

    public static Tag mapToSearchTagDomain(String searchTag) {
        return Tag.builder()
                .tag(searchTag)
                .build();
    }

    public static TripTag mapToTripTagDomain(String tripTag) {
        return TripTag.builder()
                .tag(tripTag)
                .build();
    }
}
