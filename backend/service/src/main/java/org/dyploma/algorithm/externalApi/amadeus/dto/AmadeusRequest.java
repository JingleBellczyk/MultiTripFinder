package org.dyploma.algorithm.externalApi.amadeus.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AmadeusRequest {

    public static final String DEFAULT_ORIGIN_DESTINATION_ID = "1";
    public static final String DEFAULT_DATE_WINDOW = "I1D";
    public static final String DEFAULT_CURRENCY_CODE = "EUR";
    public static final String DEFAULT_TRAVELER_ID = "1";
    public static final String DEFAULT_TRAVELER_TYPE = "ADULT";
    public static final List<String> DEFAULT_SOURCES = List.of("GDS");
    public static final List<Traveler> DEFAULT_TRAVELERS = List.of(new AmadeusRequest.Traveler());
    public static final int DEFAULT_MAX_PRICE = 999;
    public static final int DEFAULT_MAX_FLIGHT_TIME = 999;

    private List<OriginDestination> originDestinations;
    private SearchCriteria searchCriteria;

    @Builder.Default
    private String currencyCode = DEFAULT_CURRENCY_CODE;
    @Builder.Default
    private List<Traveler> travelers = DEFAULT_TRAVELERS;
    @Builder.Default
    private List<String> sources = DEFAULT_SOURCES;

    @Data
    @Builder
    public static class OriginDestination {
        @Builder.Default
        private String id = DEFAULT_ORIGIN_DESTINATION_ID;
        private String originLocationCode;
        private String destinationLocationCode;
        private DepartureDateTimeRange departureDateTimeRange;

        @Data
        @Builder
        public static class DepartureDateTimeRange {
            private String date;
            private String time;
            @Builder.Default
            private String dateWindow = DEFAULT_DATE_WINDOW;
        }
    }

    @Data
    public static class Traveler {
        private String id = DEFAULT_TRAVELER_ID;
        private String travelerType = DEFAULT_TRAVELER_TYPE;
    }

    @Data
    @Builder
    public static class SearchCriteria {

        private int maxFlightOffers;

        private int maxPrice;

        private FlightFilters flightFilters;

        @Data
        @Builder
        public static class FlightFilters {

            private int maxFlightTime;
        }
    }
}