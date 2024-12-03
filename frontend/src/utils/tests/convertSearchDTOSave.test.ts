import { convertToSearchDTOSave } from '../convertSearchDTOSave';
import { SearchDTOPost, SearchDTOSave, PlaceTimePost } from '../../types/SearchDTO';
import {selectedDateToString} from '../../utils/formatDateToReadableString'
describe('convertToSearchDTOSave', () => {
    it('should convert SearchDTOPost to SearchDTOSave with default null and empty values', () => {
        const input: SearchDTOPost = {
            placesToVisit: [
                { country: 'Poland', city: 'Warsaw', stayDuration: 2, entryOrder: 1 },
                { country: 'Germany', city: 'Berlin', stayDuration: 3, entryOrder: 2 },
            ],
            passengerCount: 4,
            maxTripDuration: 48,
            tripStartDate: '2024-01-01',
            preferredTransport: 'BUS',
            optimizationCriteria: 'DURATION',
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
            tripStartDate: '2024-05-01',
            preferredTransport: 'PLANE',
            optimizationCriteria: 'PRICE',
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

    it('should handle null tripStartDate and preferredTransport', () => {
        const input: SearchDTOPost = {
            placesToVisit: [
                { country: 'France', city: 'Paris', stayDuration: 5, entryOrder: 1 },
            ],
            passengerCount: 1,
            maxTripDuration: 12,
            tripStartDate: null,
            preferredTransport: null,
            optimizationCriteria: 'DURATION',
        };

        const expected: SearchDTOSave = {
            id: null,
            name: null,
            saveDate: null,
            tags: [],
            placesToVisit: input.placesToVisit,
            passengerCount: input.passengerCount,
            maxTripDuration: input.maxTripDuration,
            tripStartDate: null,
            preferredTransport: null,
            optimizationCriteria: input.optimizationCriteria,
        };

        const result = convertToSearchDTOSave(input);

        expect(result).toEqual(expected);
    });
});
