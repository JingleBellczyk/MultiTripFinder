package org.dyploma.algorithm.algorithmGoogleAmadeus;

import org.dyploma.airport.Airport;
import org.dyploma.airport.AirportService;
import org.dyploma.algorithm.externalApi.amadeus.AmadeusApiClient;
import org.dyploma.algorithm.externalApi.amadeus.dto.AmadeusRequest;
import org.dyploma.algorithm.externalApi.amadeus.dto.AmadeusResponse;
import org.dyploma.algorithm.externalApi.amadeus.dto.TravelSegment;
import org.dyploma.transport.TransportMode;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.trip.transfer.Transfer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class AmadeusAlgorithmUtils {

    AirportService airportService;
    AmadeusApiClient amadeusApiClient;

    public AmadeusAlgorithmUtils(AirportService airportService, AmadeusApiClient amadeusApiClient) {
        this.airportService = airportService;
        this.amadeusApiClient = amadeusApiClient;
    }

    public void processRequest(List<PlaceInTrip> placeInTripList, List<Transfer> transferList,
                               LocalDateTime localDateTime, Airport airportStart, Airport airportEnd) {

        List<TravelSegment> travelSegments = Arrays.asList(
                new TravelSegment("1", airportStart.getAirportCode(), airportEnd.getAirportCode(), formatDate(localDateTime), formatTime(localDateTime))
        );

        AmadeusResponse response = null;
        List<AmadeusRequest.OriginDestination> originDestinations = amadeusApiClient.createOriginDestinations(travelSegments);
        try {
            response = amadeusApiClient.sendAmadeusRequest(originDestinations, 1);
        } catch (Exception e) {
            return; //todo null
        }
//        todo pewnie bardziej się zabezpieczyć
        System.out.print("tutaj");
        if (response != null && response.getData().get(0) != null) {
            int segmentsNumber = response.getData().get(0).getItineraries().get(0).getSegments().size();
            System.out.println(convertResponseToTransfer(response, 0, 0, segmentsNumber));
            System.out.println(convertResponseToTransfer(response, 1, 1, segmentsNumber));
        }

        System.out.println(response);
    }

    public Transfer convertResponseToTransfer(AmadeusResponse response, int index, int transferOrder, int segmentsNumber) {

        String carrierCode = response.getData().get(0).getItineraries().get(0).getSegments().get(index).getCarrierCode();
        String carrier = response.getCarriers().get(carrierCode);

        String aircarftCode = response.getData().get(0).getItineraries().get(0).getSegments().get(index).getAircraftCode();
        String aircraft = response.getAircraft().get(aircarftCode);

        String finalCarrier = carrier + " : " + aircraft;

        String startDateTime = response.getData().get(0).getItineraries().get(0).getSegments().get(index).getDepartureAt();
        String endDateTime = response.getData().get(0).getItineraries().get(0).getSegments().get(index).getArrivalAt();

        LocalDateTime startLocalDateTime = convertStringToLocalDateTime(startDateTime);
        LocalDateTime endLocalDateTime = convertStringToLocalDateTime(endDateTime);

        Double cost = response.getData().get(0).getGrandTotal();

        String departureIataCode = response.getData().get(0).getItineraries().get(0).getSegments().get(index).getDepartureIataCode();
        String arrivalIataCode = response.getData().get(0).getItineraries().get(0).getSegments().get(index).getArrivalIataCode();

        String startAirportName = airportService.getAirportByAirportCode(departureIataCode).getAirportName();
        String endAirportName = airportService.getAirportByAirportCode(arrivalIataCode).getAirportName();

        return Transfer.builder()
                .transportMode(TransportMode.PLANE)
                .carrier(finalCarrier)
                .startDateTime(startLocalDateTime)
                .endDateTime(endLocalDateTime)
                .duration(Utils.calculateDurationInMinutes(startLocalDateTime, endLocalDateTime))
                .cost(cost / segmentsNumber)
                .startAddress(startAirportName)
                .endAddress(endAirportName)
                .transferOrder(transferOrder)
                .build();
    }

    public PlaceInTrip convertStepIntoPlace(AmadeusResponse response, Boolean isTransfer, int visitOrder, boolean departureOrArrival, int stayDuration, int index) {
        String country;
        String city;
        if(departureOrArrival){
            String departureIataCode = response.getData().get(0).getItineraries().get(0).getSegments().get(index).getDepartureIataCode();
            country = airportService.getAirportByAirportCode(departureIataCode).getCountry();
            city = airportService.getAirportByAirportCode(departureIataCode).getAirportName() + " Airport";

        }else{
            String arrivalIataCode = response.getData().get(0).getItineraries().get(0).getSegments().get(index).getArrivalIataCode();
            country = airportService.getAirportByAirportCode(arrivalIataCode).getCountry();
            city = airportService.getAirportByAirportCode(arrivalIataCode).getAirportName() + " Airport";
        }

        return PlaceInTrip.builder()
                .country(country)
                .city(city)
                .isTransfer(isTransfer)
                .stayDuration(stayDuration)
                .visitOrder(visitOrder)
                .build();
    }
    public static String formatDate(LocalDateTime dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return dateTime.toLocalDate().format(formatter);
    }

    public static String formatTime(LocalDateTime dateTime) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        return dateTime.toLocalTime().format(formatter);
    }

    public static LocalDateTime convertStringToLocalDateTime(String dateTimeString) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public static Airport getAirportByCountryAndCityFromList(String city, String country, List<PlacesWithAirport> placesWithAirportList) {

        for (PlacesWithAirport placesWithAirport : placesWithAirportList) {
            if (placesWithAirport.getCountry().equals(country)
                    && placesWithAirport.getCity().equals(city)) {
                return placesWithAirport.getAirport();
            }
        }
        return null;
    }
}
