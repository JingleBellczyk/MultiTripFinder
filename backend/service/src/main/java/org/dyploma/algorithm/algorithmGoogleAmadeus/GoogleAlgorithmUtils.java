package org.dyploma.algorithm.algorithmGoogleAmadeus;

import org.dyploma.algorithm.externalApi.google.dto.GoogleApiClient;
import org.dyploma.algorithm.externalApi.google.dto.GoogleRoutesResponse;
import org.dyploma.trip.place.PlaceInTrip;
import org.dyploma.trip.transfer.Transfer;

import java.time.LocalDateTime;
import java.util.List;

public class GoogleAlgorithmUtils {

    public void send1GoogleRequest(
            List<PlaceInTrip> placeInTripList, List<Transfer> transferList,
            String originAddress, String destinationAddress, String departureTime, List<String> prefferedTransport) {

        GoogleApiClient service = new GoogleApiClient();

        try {
            GoogleRoutesResponse routesResponse = service.fetchRoutes(
                    originAddress,
                    destinationAddress,
                    departureTime,
                    prefferedTransport
            );

            if (routesResponse != null && routesResponse.getRoutes() != null) {
                routesResponse.getRoutes().forEach(route -> {
                    route.getLegs().forEach(leg -> {
                        List<GoogleRoutesResponse.Step> stepList = leg.getSteps();

                        List<GoogleRoutesResponse.Step> filteredSteps = stepList.stream()
                                .filter(step -> step.getTransitDetails() != null)
                                .toList();

                        for (int i = 0; i < filteredSteps.size(); i++) {
                            Transfer transfer = Utils.convertStepIntoTransfer(filteredSteps.get(i), transferList.size());

//                            todo trzeba wyizlować żeby amadus mógł użyć, albo i nie
                            transferList.add(transfer);

                            if (i == 0 && transferList.size() == 1) {
                                PlaceInTrip placeInTrip = Utils.convertStepIntoPlace(filteredSteps.get(i), false, placeInTripList.size(), true, 0);
                                placeInTripList.add(placeInTrip);
                            }

                            if (i == (filteredSteps.size() - 1)) {
                                PlaceInTrip placeInTrip = Utils.convertStepIntoPlace(filteredSteps.get(i), false, placeInTripList.size(), false, 0);
                                placeInTripList.add(placeInTrip);
                            } else {
                                PlaceInTrip placeInTrip = Utils.convertStepIntoPlace(filteredSteps.get(i), true, placeInTripList.size(), false, 0);
                                placeInTripList.add(placeInTrip);
                            }

                            if (transferList.size() > 1) {
                                int duration = Utils.calculateDurationInMinutes(transferList.get(transferList.size() - 2).getEndDateTime(), transferList.get(transferList.size() - 1).getStartDateTime());
                                placeInTripList.get(placeInTripList.size() - 2).setStayDuration(duration);
                            }
                        }
                    });
                });
            } else {
                System.out.println("No routes found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send1GoogleRequest(List<PlaceInTrip> placeInTripList, List<Transfer> transferList,
                                   String cityStart, String countryStart, String cityEnd,
                                   String countryEnd, LocalDateTime date, List<String> prefferedTransport) {

        send1GoogleRequest(placeInTripList,
                transferList,
                cityStart + ", " + countryStart,
                cityEnd + ", " + countryEnd,
                Utils.convertToFormattedString(date),
                prefferedTransport);
    }
}
