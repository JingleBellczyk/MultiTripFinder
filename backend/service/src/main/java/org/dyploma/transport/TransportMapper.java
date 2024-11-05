package org.dyploma.transport;

public class TransportMapper {

    public static TransportType mapToTransportTypeRequest(com.openapi.model.TransportType transportType) {
        return switch (transportType) {
            case BUS -> TransportType.BUS;
            case TRAIN -> TransportType.TRAIN;
            case PLANE -> TransportType.PLANE;
        };
    }

    public static com.openapi.model.TransportType mapToTransportTypeResponse(TransportType transportType) {
        return switch (transportType) {
            case BUS -> com.openapi.model.TransportType.BUS;
            case TRAIN -> com.openapi.model.TransportType.TRAIN;
            case PLANE -> com.openapi.model.TransportType.PLANE;
        };
    }
}
