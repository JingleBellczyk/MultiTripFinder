package org.dyploma.transfer;

import static org.dyploma.place.PlaceMapper.mapPlaceDtoToPlace;
import static org.dyploma.transport.TransportMapper.mapToTransportTypeResponse;

public class TransferMapper {
    public static com.openapi.model.Transfer mapTransferResponseToTransfer(TransferResponse transfer) {
        com.openapi.model.Transfer transferResult = new com.openapi.model.Transfer();

        transferResult.setStartPlace(mapPlaceDtoToPlace(transfer.getStartPlace()));
        transferResult.setEndPlace(mapPlaceDtoToPlace(transfer.getEndPlace()));
        transferResult.setStartDate(transfer.getStartDate().toString());
        transferResult.setEndDate(transfer.getEndDate().toString());
        transferResult.setTransitLine(transfer.getTransitLine());
        transferResult.setTransport(mapToTransportTypeResponse(transfer.getTransport()));
        transferResult.setPrice(transfer.getPrice());
        transferResult.setDuration(transfer.getDuration());
        transferResult.setOrder(transfer.getOrder());

        return transferResult;
    }
}
