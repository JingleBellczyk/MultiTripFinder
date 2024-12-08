package org.dyploma.algorithm.externalApi.google.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleRoutesResponse {
    @JsonProperty("routes")
    private List<Route> routes;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {
        @JsonProperty("legs")
        private List<Leg> legs;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Leg {
        @JsonProperty("steps")
        private List<Step> steps;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Step {
        @JsonProperty("transitDetails")
        private TransitDetails transitDetails;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitDetails {
        @JsonProperty("stopDetails")
        private StopDetails stopDetails;

        @JsonProperty("localizedValues")
        private LocalizedValues localizedValues;

        @JsonProperty("headsign")
        private String headsign;

        @JsonProperty("transitLine")
        private TransitLine transitLine;

        @JsonProperty("stopCount")
        private int stopCount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StopDetails {
        @JsonProperty("arrivalStop")
        private Stop arrivalStop;

        @JsonProperty("departureStop")
        private Stop departureStop;

        @JsonProperty("arrivalTime")
        private String arrivalTime;

        @JsonProperty("departureTime")
        private String departureTime;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stop {
        @JsonProperty("name")
        private String name;

        @JsonProperty("location")
        private Location location;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        @JsonProperty("latLng")
        private LatLng latLng;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LatLng {
        @JsonProperty("latitude")
        private double latitude;

        @JsonProperty("longitude")
        private double longitude;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocalizedValues {
        @JsonProperty("arrivalTime")
        private LocalizedTime arrivalTime;

        @JsonProperty("departureTime")
        private LocalizedTime departureTime;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocalizedTime {
        @JsonProperty("time")
        private TimeDetails time;

        @JsonProperty("timeZone")
        private String timeZone;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TimeDetails {
        @JsonProperty("text")
        private String text;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransitLine {
        @JsonProperty("agencies")
        private List<Agency> agencies;

        @JsonProperty("name")
        private String name;

        @JsonProperty("nameShort")
        private String nameShort;

        @JsonProperty("color")
        private String color;

        @JsonProperty("textColor")
        private String textColor;

        @JsonProperty("vehicle")
        private Vehicle vehicle;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Agency {
        @JsonProperty("name")
        private String name;

        @JsonProperty("phoneNumber")
        private String phoneNumber;

        @JsonProperty("uri")
        private String uri;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Vehicle {
        @JsonProperty("name")
        private Name name;

        @JsonProperty("type")
        private String type;

        @JsonProperty("iconUri")
        private String iconUri;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Name {
        @JsonProperty("text")
        private String text;
    }
}
