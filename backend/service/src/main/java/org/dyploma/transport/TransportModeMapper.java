package org.dyploma.transport;

public class TransportModeMapper {

    public static TransportMode mapToTransportMode(com.openapi.model.TransportMode transportModeApi) {
        if (transportModeApi == null) {
            return null;
        }
        return switch (transportModeApi) {
            case BUS -> TransportMode.BUS;
            case TRAIN -> TransportMode.TRAIN;
            case PLANE -> TransportMode.PLANE;
        };
    }

    public static com.openapi.model.TransportMode mapToTransportModeApi(TransportMode transportMode) {
        if (transportMode == null) {
            return null;
        }
        return switch (transportMode) {
            case BUS -> com.openapi.model.TransportMode.BUS;
            case TRAIN -> com.openapi.model.TransportMode.TRAIN;
            case PLANE -> com.openapi.model.TransportMode.PLANE;
        };
    }
}
