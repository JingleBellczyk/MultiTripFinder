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

    /**
     * This method sends one request to Amadeus API
     * and processes response into objects of type: PriceInTrip and Transfer
     * Then adds them to param lists: placeInTripList, transferList
     *
     * @return true if transfer was found and was successfully added to lists
     */
    public boolean send1AmadeusRequest(
            List<PlaceInTrip> placeInTripList, List<Transfer> transferList,
            LocalDateTime localDateTime, Airport airportStart, Airport airportEnd, int passengerCount) {

        List<TravelSegment> travelSegments = Arrays.asList(
                new TravelSegment("1", airportStart.getCityCode(), airportEnd.getCityCode(), formatDate(localDateTime), formatTime(localDateTime))
        );

        AmadeusResponse response = null;
        List<AmadeusRequest.OriginDestination> originDestinations = amadeusApiClient.createOriginDestinations(travelSegments);

        try {
            response = amadeusApiClient.sendAmadeusRequest(originDestinations, 1);

            if (response != null && response.getData().get(0) != null) {

                List<AmadeusResponse.Segment> segmentList = response.getData().get(0).getItineraries().get(0).getSegments();

                for (int i = 0; i < segmentList.size(); i++) {

                    Transfer transfer = convertResponseToTransfer(response, i, transferList.size(), segmentList.size(), passengerCount);
                    transferList.add(transfer);

                    //transfer places are those, in which there is change of transport

                    //start place
                    if (i == 0 && transferList.size() == 1) {

                        //places to visit and last place
                        PlaceInTrip placeInTrip = convertResponseToPlace(response, false, placeInTripList.size(), true, 0, i);
                        placeInTripList.add(placeInTrip);
                    }

                    if (i == (segmentList.size() - 1)) {

                        //changes
                        PlaceInTrip placeInTrip = convertResponseToPlace(response, false, placeInTripList.size(), false, 0, i);
                        placeInTripList.add(placeInTrip);
                    } else {
                        PlaceInTrip placeInTrip = convertResponseToPlace(response, true, placeInTripList.size(), false, 0, i);
                        placeInTripList.add(placeInTrip);
                    }

                    if (transferList.size() > 1) {
                        int duration = Utils.calculateDurationInMinutes(transferList.get(transferList.size() - 2).getEndDateTime(), transferList.get(transferList.size() - 1).getStartDateTime());
                        placeInTripList.get(placeInTripList.size() - 2).setStayDuration(duration);
                    }
                }
            } else {
                System.out.println("No routes found.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * This method is converter of part of Amadeus into Transfer
     *
     * @param response is converted into Transfer
     * @param index is index of trasnfer that is converted
     * @param transferOrder is order of transfer
     * @param segmentsNumber is used to estimate the price, because Amadeus API returns one summary price for all transfers
     * @param passengerCount is number of passengers
     * @return Transfer
     */
    public Transfer convertResponseToTransfer(AmadeusResponse response, int index, int transferOrder, int segmentsNumber, int passengerCount) {
        AmadeusResponse.Segment segment = response.getData().get(0).getItineraries().get(0).getSegments().get(index);
        String carrierCode = segment.getCarrierCode();
        String carrier = response.getCarriers().get(carrierCode);

        String aircarftCode = segment.getAircraftCode();
        String aircraft = response.getAircraft().get(aircarftCode);

        String finalCarrier = carrier + " : " + aircraft;

        String startDateTime = segment.getDepartureAt();
        String endDateTime = segment.getArrivalAt();

        LocalDateTime startLocalDateTime = convertStringToLocalDateTime(startDateTime);
        LocalDateTime endLocalDateTime = convertStringToLocalDateTime(endDateTime);

        Double cost = response.getData().get(0).getGrandTotal() * passengerCount;

        String departureIataCode = segment.getDepartureIataCode();
        String arrivalIataCode = segment.getArrivalIataCode();

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
                .transferOrder(transferOrder + 1) //order from 1
                .build();
    }

    /**
     * This method is converter of part of Amadeus into PLaceInTrip
     *
     * @param response is converted
     * @param isTransfer is a flag if in place is change of transport
     * @param visitOrder is order of visit
     * @param departureOrArrival true means departure, false means arrival
     * @param stayDuration time of beeing in place
     * @param index is index of trasnfer that is converted
     * @return PlaceInTrip
     */
    public PlaceInTrip convertResponseToPlace(AmadeusResponse response, Boolean isTransfer, int visitOrder, boolean departureOrArrival, int stayDuration, int index) {

        AmadeusResponse.Segment segment = response.getData().get(0).getItineraries().get(0).getSegments().get(index);
        String country;
        String city;
        String airportCode;

        if (departureOrArrival) {
            airportCode = segment.getDepartureIataCode();
        } else {
            airportCode = segment.getArrivalIataCode();

        }
        country = airportService.getAirportByAirportCode(airportCode).getCountry();
        city = airportService.getAirportByAirportCode(airportCode).getAirportName() + " Airport";

        return PlaceInTrip.builder()
                .country(country)
                .city(city)
                .isTransfer(isTransfer)
                .stayDuration(stayDuration)
                .visitOrder(visitOrder + 1) //order from 1
                .build();
    }

    /**
     *  * Utility methods for working with LocalDateTime:
     * - `formatDate`: Formats a LocalDateTime object to a string representing the date in "yyyy-MM-dd" format.
     * - `formatTime`: Formats a LocalDateTime object to a string representing the time in "HH:mm" format.
     * - `convertStringToLocalDateTime`: Converts a string in ISO_LOCAL_DATE_TIME format to a LocalDateTime object
     */

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
}
