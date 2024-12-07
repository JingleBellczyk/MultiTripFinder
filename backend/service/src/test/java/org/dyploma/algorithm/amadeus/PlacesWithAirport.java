package org.dyploma.algorithm.amadeus;

import lombok.Data;
import org.dyploma.airport.Airport;
//mozna usunac
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