import {
    isDateValid,
    isValidPlacesTime,
    isValidPlace,
    isValidMaxHoursToSpend,
    validateForm
} from "../placesTimeUtils"
import { PlaceTime, PlaceLocation } from '../../types/SearchDTO';

describe('Validation Functions', () => {
    describe('isDateValid', () => {
        it('returns false for null date', () => {
            expect(isDateValid(null)).toBe(false);
        });

        it('returns false for past dates', () => {
            const pastDate = new Date();
            pastDate.setDate(pastDate.getDate() - 1);
            expect(isDateValid(pastDate)).toBe(false);
        });

        it('returns true for today', () => {
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            expect(isDateValid(today)).toBe(true);
        });

        it('returns true for future dates', () => {
            const futureDate = new Date();
            futureDate.setDate(futureDate.getDate() + 1);
            expect(isDateValid(futureDate)).toBe(true);
        });
    });

    describe('isValidPlacesTime', () => {
        it('returns false if any place name is empty', () => {
            const invalidPlaces: PlaceTime[] = [
                { name: '',country:"",city:"", hoursToSpend: 5 },
                { name: '',country:"",city:"", hoursToSpend: 5 },
            ];
            expect(isValidPlacesTime(invalidPlaces)).toBe(false);
        });

        it('returns false if any hoursToSpend is <= 0', () => {
            const invalidPlaces: PlaceTime[] = [
                { name: '',country:"",city:"", hoursToSpend: -5 },
                { name: '',country:"",city:"", hoursToSpend: -3 },
            ];
            expect(isValidPlacesTime(invalidPlaces)).toBe(false);
        });

        it('returns true for valid places', () => {
            const validPlaces: PlaceTime[] = [
                { name: 'Wrocław, Poland',country:"Poland",city:"Wrocław", hoursToSpend: 5 },
                { name: 'Poznań, Poland',country:"Poland",city:"Poznań", hoursToSpend: 1 },
            ];
            expect(isValidPlacesTime(validPlaces)).toBe(true);
        });
    });

    describe('isValidPlace', () => {
        it('returns false for null place', () => {
            expect(isValidPlace(null)).toBe(false);
        });

        it('returns false for empty name', () => {
            const invalidPlace: PlaceLocation = {  name: '',country:"Poland",city:"Wrocław", };
            expect(isValidPlace(invalidPlace)).toBe(false);
        });
        it('returns false for empty country', () => {
            const invalidPlace: PlaceLocation = {  name: 'Wrocław, Poland',country:"",city:"Wrocław", };
            expect(isValidPlace(invalidPlace)).toBe(false);
        });
        it('returns false for empty city', () => {
            const invalidPlace: PlaceLocation = {  name: 'Wrocław, Poland',country:"Poland",city:"", };
            expect(isValidPlace(invalidPlace)).toBe(false);
        });

        it('returns true for valid place', () => {
            const validPlace: PlaceLocation = {  name: 'Wrocław, Poland',country:"Poland",city:"Wrocław", };
            expect(isValidPlace(validPlace)).toBe(true);
        });
    });

    describe('isValidMaxHoursToSpend', () => {
        it('returns false for null date', () => {
            expect(isValidMaxHoursToSpend(null, 48)).toBe(false);
        });

        it('returns false if trip ends after one year from now', () => {
            const today = new Date();
            const maxHoursToSpend = 365 * 24 + 1; // Slightly more than one year
            expect(isValidMaxHoursToSpend(today, maxHoursToSpend)).toBe(false);
        });

        it('returns true if trip ends within one year from now', () => {
            const today = new Date();
            const maxHoursToSpend = 365 * 24; // Exactly one year
            expect(isValidMaxHoursToSpend(today, maxHoursToSpend)).toBe(true);
        });
    });

    describe('validateForm', () => {
        const validDate = new Date();
        validDate.setDate(validDate.getDate() + 1);

        const validPlaces: PlaceTime[] = [
            { name: 'Wrocław, Poland',country:"Poland",city:"Wrocław", hoursToSpend: 5 },
            { name: 'Poznań, Poland',country:"Poland",city:"Poznań", hoursToSpend: 1 },
        ];

        const validPlace: PlaceLocation = {  name: 'Wrocław, Poland',country:"Poland",city:"Wrocław", };

        it('returns all errors for invalid inputs', () => {
            const errors = validateForm(null, [{ name: '',country:"",city:"", hoursToSpend: 0 }], null, null, 0);
            expect(errors.dateError).toBe("Date can't be empty or in the past");
            expect(errors.placesTimeError).toBe("Places or time can't be empty");
            expect(errors.startPlaceError).toBe("Start place can't be empty");
            expect(errors.endPlaceError).toBe("End place can't be empty");
            expect(errors.maxHoursToSpendError).toBe(
                "The total travel duration can't end later than one year from now"
            );
        });

        it('returns no errors for valid inputs', () => {
            const errors = validateForm(validDate, validPlaces, validPlace, validPlace, 48);
            expect(errors.dateError).toBeNull();
            expect(errors.placesTimeError).toBeNull();
            expect(errors.startPlaceError).toBeNull();
            expect(errors.endPlaceError).toBeNull();
            expect(errors.maxHoursToSpendError).toBeNull();
        });

        it('returns specific errors for partially invalid inputs', () => {
            const errors = validateForm(validDate, [], null, validPlace, 0);
            expect(errors.dateError).toBeNull();
            expect(errors.placesTimeError).toBeNull();
            expect(errors.startPlaceError).toBe("Start place can't be empty");
            expect(errors.endPlaceError).toBeNull();
            expect(errors.maxHoursToSpendError).toBe(
                "The total travel duration can't end later than one year from now"
            );
        });
    });
});
