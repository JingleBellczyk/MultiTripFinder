package org.dyploma.algorithm.algorithmGoogleAmadeus;

import lombok.Data;
import org.dyploma.airport.Airport;

/**
 * This class represents a location with associated airport information or null, if place doesn't have airport.
 * It is used for generating permutations of places and for easily retrieving
 * details about the corresponding airport.
 */
@Data
public class PlacesWithAirport {
    private String country;
    private String city;
    private Integer stayDuration;
    private Integer entryOrder;
    private Airport airport;

    public PlacesWithAirport(String country, String city, Integer stayDuration, Integer entryOrder, Airport airport) {
        this.country = country;
        this.city = city;
        this.stayDuration = stayDuration;
        this.entryOrder = entryOrder;
        this.airport = airport;
    }

    @Override
    public String toString() {
        return "PlacesWithAirport{" +
                "city='" + city + '\'' +
                '}';
    }
}