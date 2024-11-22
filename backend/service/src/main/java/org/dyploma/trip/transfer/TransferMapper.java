package org.dyploma.trip.transfer;


import org.dyploma.search.place.PlaceInSearch;
import org.dyploma.search.place.PlaceInSearchMapper;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;

import static org.dyploma.transport.TransportModeMapper.mapToTransportMode;
import static org.dyploma.transport.TransportModeMapper.mapToTransportModeApi;

public class TransferMapper {
    public static List<Transfer> mapToTransfers(List<com.openapi.model.Transfer> transfersApi) {
        return transfersApi.stream()
                .map(TransferMapper::mapToTransfer)
                .toList();
    }

    public static Transfer mapToTransfer(com.openapi.model.Transfer transferApi) {
        return Transfer.builder()
                .transportMode(mapToTransportMode(transferApi.getTransportMode()))
                .carrier(transferApi.getCarrier())
                .startDateTime(transferApi.getStartDateTime().toLocalDateTime())
                .endDateTime(transferApi.getEndDateTime().toLocalDateTime())
                .duration(transferApi.getDuration())
                .cost(transferApi.getCost().doubleValue())
                .startAddress(transferApi.getStartAddress().toLowerCase())
                .endAddress(transferApi.getEndAddress().toLowerCase())
                .transferOrder(transferApi.getTransferOrder())
                .build();
    }

    public static com.openapi.model.Transfer mapToTransferApi(Transfer transfer) {
        return new com.openapi.model.Transfer()
                .transportMode(mapToTransportModeApi(transfer.getTransportMode()))
                .carrier(transfer.getCarrier())
                .startDateTime(transfer.getStartDateTime().atOffset(ZoneOffset.UTC))
                .endDateTime(transfer.getEndDateTime().atOffset(ZoneOffset.UTC))
                .duration(transfer.getDuration())
                .cost(BigDecimal.valueOf(transfer.getCost()))
                .startAddress(transfer.getStartAddress())
                .endAddress(transfer.getEndAddress())
                .transferOrder(transfer.getTransferOrder());
    }
}
