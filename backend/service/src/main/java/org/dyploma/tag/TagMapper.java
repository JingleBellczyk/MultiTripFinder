package org.dyploma.tag;

public class TagMapper {

    public static SearchTag mapToSearchTagDomain(String searchTag) {
        return SearchTag.builder()
                .tag(searchTag)
                .build();
    }

    public static TripTag mapToTripTagDomain(String tripTag) {
        return TripTag.builder()
                .tag(tripTag)
                .build();
    }
}
