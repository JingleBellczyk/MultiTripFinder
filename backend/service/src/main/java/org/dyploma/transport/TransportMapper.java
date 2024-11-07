package org.dyploma.transport;

public class TransportMapper {

    public static TransportMode mapToTransportTypeRequest(com.openapi.model.TransportType transportType) {
        return switch (transportType) {
            case BUS -> TransportMode.BUS;
            case TRAIN -> TransportMode.TRAIN;
            case PLANE -> TransportMode.PLANE;
        };
    }

    public static com.openapi.model.TransportType mapToTransportTypeResponse(TransportMode transportType) {
        return switch (transportType) {
            case BUS -> com.openapi.model.TransportType.BUS;
            case TRAIN -> com.openapi.model.TransportType.TRAIN;
            case PLANE -> com.openapi.model.TransportType.PLANE;
        };
    }
}
