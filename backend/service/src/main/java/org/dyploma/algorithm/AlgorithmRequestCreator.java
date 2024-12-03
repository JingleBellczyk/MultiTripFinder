package org.dyploma.algorithm;

import org.dyploma.airport.Airport;
import org.dyploma.airport.AirportService;
import org.dyploma.algorithm.dto.AlgorithmRequest;
import org.dyploma.algorithm.dto.PlaceInSearchRequest;
import org.dyploma.algorithm.externalApi.nominatim.NominatimService;
import org.dyploma.algorithm.externalApi.nominatim.NominatimServiceRequest;
import org.dyploma.algorithm.externalApi.nominatim.NominatimServiceResponse;
import org.dyploma.search.domain.SearchRequest;
import org.dyploma.search.place.PlaceInSearch;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class AlgorithmRequestCreator {
    private final NominatimService nominatimService;
    private final AirportService airportService;

    public AlgorithmRequestCreator(NominatimService nominatimService, AirportService airportService) {
        this.nominatimService = nominatimService;
        this.airportService = airportService;
    }

    public AlgorithmRequest createRequest(SearchRequest searchRequest) {
        List<PlaceInSearch> placesInSearch = searchRequest.getPlacesToVisit();
        PlaceInSearch startPlace = null;
        PlaceInSearch endPlace = null;
        List<PlaceInSearch> placesToVisit = new ArrayList<>();
        for (PlaceInSearch place: placesInSearch) {
            if (place.getEntryOrder() == 1) {
                startPlace = place;
            }
            else if (place.getEntryOrder() == placesInSearch.size()) {
                endPlace = place;
            }
            else {
                placesToVisit.add(place);
            }
        }
        if (startPlace == null || endPlace == null) {
            throw new IllegalArgumentException("Start and end places must be provided");
        }
        placesToVisit.sort(Comparator.comparingInt(PlaceInSearch::getEntryOrder));
        return AlgorithmRequest.builder()
                .start_place(mapToPlaceInSearchRequest(startPlace))
                .end_place(mapToPlaceInSearchRequest(endPlace))
                .places_to_visit(placesToVisit.stream().map(this::mapToPlaceInSearchRequest).toList())
                .passenger_count(searchRequest.getPassengerCount())
                .trip_start_date(searchRequest.getTripStartDate().toString())
                .max_trip_duration(getMaxTripDurationDays(searchRequest.getMaxTripDuration()))
                .preferred_transport(searchRequest.getPreferredTransport())
                .optimization_criteria(searchRequest.getOptimizationCriteria())
                .build();
    }

    private PlaceInSearchRequest mapToPlaceInSearchRequest(PlaceInSearch placeInSearch) {
        String city = placeInSearch.getCity();
        String country = placeInSearch.getCountry();
        Airport airport = airportService.getAirportByCountryAndCity(country, city);
        String cityCode = airport != null ? airport.getCityCode() : null;
        return PlaceInSearchRequest.builder()
                .country(country)
                .city(city)
                .stay_hours_min(placeInSearch.getStayDuration())
                .stay_hours_max(getHoursMax(placeInSearch.getStayDuration()))
                .station_coordinates(getBusAndRailwayStationCoordinates(country, city))
                .city_code(cityCode)
                .build();
    }

    private NominatimServiceResponse getBusAndRailwayStationCoordinates(String country, String city) {
        return nominatimService.getBusAndRailwayStationCoordinates(NominatimServiceRequest.builder()
                .country(country)
                .city(city)
                .build());
    }

    private static int getMaxTripDurationDays(int hours) {
        return (int) Math.ceil((double) hours / 24);
    }

    public static int getHoursMax(int hoursMin) {
        return hoursMin >= 24 ? hoursMin + 24 : (int) Math.ceil(hoursMin * 1.5);
    }
}
