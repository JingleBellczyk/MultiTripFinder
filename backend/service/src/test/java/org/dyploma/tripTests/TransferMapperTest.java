package org.dyploma.tripTests;

import com.openapi.model.Transfer;
import com.openapi.model.TransportMode;
import org.dyploma.trip.transfer.TransferMapper;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

public class TransferMapperTest {

    @Test
    void testMapToTransfers() {
        Transfer transferApi1 = mock(Transfer.class);
        Transfer transferApi2 = mock(Transfer.class);

        when(transferApi1.getTransportMode()).thenReturn(TransportMode.valueOf("BUS"));
        when(transferApi1.getCarrier()).thenReturn("Carrier1");
        when(transferApi1.getStartDateTime()).thenReturn(OffsetDateTime.of(LocalDateTime.of(2023, 12, 1, 8, 0, 0, 0), OffsetDateTime.now().getOffset()));
        when(transferApi1.getEndDateTime()).thenReturn(OffsetDateTime.of(LocalDateTime.of(2023, 12, 1, 10, 0, 0, 0), OffsetDateTime.now().getOffset()));
        when(transferApi1.getDuration()).thenReturn(120);
        when(transferApi1.getCost()).thenReturn(BigDecimal.valueOf(50));
        when(transferApi1.getStartAddress()).thenReturn("Address1");
        when(transferApi1.getEndAddress()).thenReturn("Address2");
        when(transferApi1.getTransferOrder()).thenReturn(1);

        when(transferApi2.getTransportMode()).thenReturn(TransportMode.valueOf("TRAIN"));
        when(transferApi2.getCarrier()).thenReturn("Carrier2");
        when(transferApi2.getStartDateTime()).thenReturn(OffsetDateTime.of(LocalDateTime.of(2023, 12, 1, 11, 0, 0, 0), OffsetDateTime.now().getOffset()));
        when(transferApi2.getEndDateTime()).thenReturn(OffsetDateTime.of(LocalDateTime.of(2023, 12, 1, 13, 0, 0, 0), OffsetDateTime.now().getOffset()));
        when(transferApi2.getDuration()).thenReturn(120);
        when(transferApi2.getCost()).thenReturn(BigDecimal.valueOf(75));
        when(transferApi2.getStartAddress()).thenReturn("Address3");
        when(transferApi2.getEndAddress()).thenReturn("Address4");
        when(transferApi2.getTransferOrder()).thenReturn(2);

        List<Transfer> transferApis = Arrays.asList(transferApi1, transferApi2);

        List<org.dyploma.trip.transfer.Transfer> transfers = TransferMapper.mapToTransfers(transferApis);

        assertNotNull(transfers);
        assertEquals(2, transfers.size());

        org.dyploma.trip.transfer.Transfer mappedTransfer1 = transfers.get(0);
        org.dyploma.trip.transfer.Transfer mappedTransfer2 = transfers.get(1);

        assertEquals("BUS", mappedTransfer1.getTransportMode().toString());
        assertEquals("Carrier1", mappedTransfer1.getCarrier());
        assertEquals(50, mappedTransfer1.getCost(), 0.01);
        assertEquals("address1", mappedTransfer1.getStartAddress().toLowerCase());
        assertEquals("address2", mappedTransfer1.getEndAddress().toLowerCase());
        assertEquals(1, mappedTransfer1.getTransferOrder());

        assertEquals("TRAIN", mappedTransfer2.getTransportMode().toString());
        assertEquals("Carrier2", mappedTransfer2.getCarrier());
        assertEquals(75, mappedTransfer2.getCost(), 0.01);
        assertEquals("address3", mappedTransfer2.getStartAddress().toLowerCase());
        assertEquals("address4", mappedTransfer2.getEndAddress().toLowerCase());
        assertEquals(2, mappedTransfer2.getTransferOrder());
    }
}
