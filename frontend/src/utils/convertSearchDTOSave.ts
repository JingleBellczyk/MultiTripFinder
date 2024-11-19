import {SearchDTOPost, SearchDTOSave} from "../types/SearchDTO";

export const convertToSearchDTOSave = (dto: SearchDTOPost): SearchDTOSave => {
    return {
        id: null,
        name: null,
        saveDate: null,
        tags: [],
        placesToVisit: dto.placesToVisit,
        passengerCount: dto.passengerCount,
        maxTripDuration: dto.maxTripDuration,
        tripStartDate: dto.tripStartDate,
        preferredTransport: dto.preferredTransport,
        optimizationCriteria: dto.optimizationCriteria,
    };
};