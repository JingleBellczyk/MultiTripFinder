package org.dyploma.algorithm.externalApi.amadeus.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmadeusResponse {

    @JsonProperty("data")
    private List<FlightOffer> data = new ArrayList<>();

    private Map<String, String> aircraft = new HashMap<>();
    private Map<String, String> carriers = new HashMap<>();

    @JsonProperty("errors")
    private List<Error> errors = new ArrayList<>();

    @JsonProperty("data")
    public void setData(List<FlightOffer> data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("dictionaries")
    private void unpackDictionaries(Map<String, Object> dictionaries) {
        if (dictionaries != null) {
            Map<String, String> aircraftData = (Map<String, String>) dictionaries.get("aircraft");
            if (aircraftData != null) {
                aircraft.putAll(aircraftData);
            }

            Map<String, String> carriersData = (Map<String, String>) dictionaries.get("carriers");
            if (carriersData != null) {
                carriers.putAll(carriersData);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @JsonProperty("data")
    private void unpackData(List<Map<String, Object>> data) {
        if (data != null && !data.isEmpty()) {
            for (Map<String, Object> offerData : data) {
                FlightOffer offer = new FlightOffer();

                offer.setId((String) offerData.get("id"));

                List<Map<String, Object>> itinerariesData = (List<Map<String, Object>>) offerData.get("itineraries");
                List<Itinerary> itineraries = new ArrayList<>();

                if (itinerariesData != null) {
                    for (Map<String, Object> itineraryData : itinerariesData) {
                        Itinerary itinerary = new Itinerary();
                        itinerary.setDuration((String) itineraryData.get("duration"));

                        // Unpack segments
                        List<Map<String, Object>> segmentsData = (List<Map<String, Object>>) itineraryData.get("segments");
                        List<Segment> segments = new ArrayList<>();
                        if (segmentsData != null) {
                            for (Map<String, Object> segmentData : segmentsData) {
                                Segment segment = new Segment();

                                // Unpack departure and arrival details
                                Map<String, Object> departureData = (Map<String, Object>) segmentData.get("departure");
                                Map<String, Object> arrivalData = (Map<String, Object>) segmentData.get("arrival");

                                if (departureData != null) {
                                    segment.setDepartureIataCode((String) departureData.get("iataCode"));
                                    segment.setDepartureAt((String) departureData.get("at"));
                                }
                                if (arrivalData != null) {
                                    segment.setArrivalIataCode((String) arrivalData.get("iataCode"));
                                    segment.setArrivalAt((String) arrivalData.get("at"));
                                }

                                // Unpack other segment details
                                segment.setCarrierCode((String) segmentData.get("carrierCode"));
                                segment.setAircraftCode((String) ((Map<String, Object>) segmentData.get("aircraft")).get("code"));
                                segment.setId((String) segmentData.get("id"));

                                segments.add(segment);
                            }
                        }
                        itinerary.setSegments(segments);
                        itineraries.add(itinerary);
                    }
                }
                offer.setItineraries(itineraries);

                // Directly unpack grandTotal from the price field
                Map<String, Object> priceData = (Map<String, Object>) offerData.get("price");
                if (priceData != null) {
                    String grandTotalStr = (String) priceData.get("grandTotal");
                    if (grandTotalStr != null) {
                        offer.setGrandTotal(Double.parseDouble(grandTotalStr));
                    }
                }

                this.data.add(offer);
            }
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlightOffer {
        private String id;
        private List<Itinerary> itineraries;
        private Double grandTotal;

        @Override
        public String toString() {
            return "FlightOffer{" +
                    "id='" + id + '\'' +
                    ", itineraries=" + itineraries +
                    ", grandTotal=" + grandTotal +
                    '}';
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Itinerary {
        private String duration;
        private List<Segment> segments;

        @Override
        public String toString() {
            return "Itinerary{" +
                    "duration='" + duration + '\'' +
                    ", segments=" + segments +
                    '}';
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Segment {
        private String departureIataCode;
        private String departureAt;
        private String arrivalIataCode;
        private String arrivalAt;
        private String carrierCode;
        private String aircraftCode;
        private String id;

        @Override
        public String toString() {
            return "Segment{" +
                    "departureIataCode='" + departureIataCode + '\'' +
                    ", departureAt='" + departureAt + '\'' +
                    ", arrivalIataCode='" + arrivalIataCode + '\'' +
                    ", arrivalAt='" + arrivalAt + '\'' +
                    ", carrierCode='" + carrierCode + '\'' +
                    ", aircraftCode='" + aircraftCode + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error {
        private Integer code;
        private String title;
        private String detail;
        private Integer status;

        @Override
        public String toString() {
            return "Error{" +
                    "code=" + code +
                    ", title='" + title + '\'' +
                    ", detail='" + detail + '\'' +
                    ", status=" + status +
                    '}';
        }
    }
}
