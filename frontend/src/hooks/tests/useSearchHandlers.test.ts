import {SearchDTOPost, SearchDTOSave} from '../../types/SearchDTO';
import {convertToSearchDTOSave} from "../../utils/convertSearchDTOSave"

jest.mock('../../api/services/searchService', () => ({
    postSearch: jest.fn(),
}));

describe('convertToSearchDTOSave', () => {
    it('should convert SearchDTOPost to SearchDTOSave with default null and empty values', () => {
        const input: SearchDTOPost = {
            placesToVisit: [
                { country: 'Poland', city: 'Warsaw', stayDuration: 2, entryOrder: 1 },
                { country: 'Germany', city: 'Berlin', stayDuration: 3, entryOrder: 2 },
            ],
            passengerCount: 4,
            maxTripDuration: 48,
            tripStartDate: new Date('2024-01-01T10:00:00'),
            preferredTransport: 'CAR',
            optimizationCriteria: 'FASTEST',
        };

        const expected: SearchDTOSave = {
            id: null,
            name: null,
            saveDate: null,
            tags: [],
            placesToVisit: input.placesToVisit,
            passengerCount: input.passengerCount,
            maxTripDuration: input.maxTripDuration,
            tripStartDate: input.tripStartDate,
            preferredTransport: input.preferredTransport,
            optimizationCriteria: input.optimizationCriteria,
        };

        const result = convertToSearchDTOSave(input);

        expect(result).toEqual(expected);
    });

    it('should handle empty placesToVisit array', () => {
        const input: SearchDTOPost = {
            placesToVisit: [],
            passengerCount: 2,
            maxTripDuration: 24,
            tripStartDate: new Date('2024-05-01T12:00:00'),
            preferredTransport: 'PLANE',
            optimizationCriteria: 'CHEAPEST',
        };

        const expected: SearchDTOSave = {
            id: null,
            name: null,
            saveDate: null,
            tags: [],
            placesToVisit: [],
            passengerCount: input.passengerCount,
            maxTripDuration: input.maxTripDuration,
            tripStartDate: input.tripStartDate,
            preferredTransport: input.preferredTransport,
            optimizationCriteria: input.optimizationCriteria,
        };

        const result = convertToSearchDTOSave(input);

        expect(result).toEqual(expected);
    });

    it('should handle null preferredTransport', () => {
        const input: SearchDTOPost = {
            placesToVisit: [
                { country: 'France', city: 'Paris', stayDuration: 5, entryOrder: 1 },
            ],
            passengerCount: 1,
            maxTripDuration: 12,
            tripStartDate: new Date('2024-06-01'),
            preferredTransport: null,
            optimizationCriteria: 'ECO-FRIENDLY',
        };

        const expected: SearchDTOSave = {
            id: null,
            name: null,
            saveDate: null,
            tags: [],
            placesToVisit: input.placesToVisit,
            passengerCount: input.passengerCount,
            maxTripDuration: input.maxTripDuration,
            tripStartDate: input.tripStartDate,
            preferredTransport: null,
            optimizationCriteria: input.optimizationCriteria,
        };

        const result = convertToSearchDTOSave(input);

        expect(result).toEqual(expected);
    });

    it('should handle null name and saveDate correctly in SearchDTOSave', () => {
        const input: SearchDTOPost = {
            placesToVisit: [
                { country: 'Spain', city: 'Madrid', stayDuration: 3, entryOrder: 1 },
            ],
            passengerCount: 3,
            maxTripDuration: 36,
            tripStartDate: new Date('2024-03-15T08:00:00'),
            preferredTransport: 'TRAIN',
            optimizationCriteria: 'BALANCED',
        };

        const result = convertToSearchDTOSave(input);

        expect(result.name).toBeNull();
        expect(result.saveDate).toBeNull();
    });
});