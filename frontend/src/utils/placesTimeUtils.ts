import {PlaceTime} from "../types/SearchDTO";

/**
 * functions to valid search page
 */

export const isDateValid = (selectedDate: Date | null): boolean => {
    if (!selectedDate) return false; // If the date is null, it's not valid

    const now = new Date();
    now.setHours(0, 0, 0, 0);
    return selectedDate >= now; // Return true if selectedDate is today or in the future
};

export const isValidPlacesTime = (placesTime: PlaceTime[]): boolean => {
    for (const placeTime of placesTime) {
        if (!placeTime.place || placeTime.place.trim() === "") {
            return false;
        }
        if (placeTime.hoursToSpend <= 0) {
            return false;
        }
    }
    return true;
};

export const isValidPlace = (place: string | null ): boolean => {
    if (typeof place !== "string" || place === "") {
        return false;
    }
    return true;
};
// Validation function for max hours to spend relative to selected date
export const isValidMaxHoursToSpend = (selectedDate: Date | null, maxHoursToSpend: number): boolean => {
    if (!selectedDate) return false;

    const maxDaysToSpend = Math.floor(maxHoursToSpend / 24);
    const endDate = new Date(selectedDate);
    endDate.setDate(endDate.getDate() + maxDaysToSpend);

    const oneYearFromNow = new Date();
    oneYearFromNow.setFullYear(oneYearFromNow.getFullYear() + 1);
    return endDate <= oneYearFromNow;
};

export interface ValidationErrors {
    dateError: string | null,
    placesTimeError: string | null;
    startPlaceError: string | null;
    endPlaceError: string | null;
    maxHoursToSpendError: string | null;
}

export function validateForm(
    selectedDate: Date | null,
    placesTimeList: PlaceTime[],
    startPlace: string | null,
    endPlace: string | null,
    maxHoursToSpend: number
): {
    dateError: string | null;
    maxHoursToSpendError: string | null;
    endPlaceError: string | null;
    startPlaceError: string | null;
    placesTimeError: string | null
} {
    return {
        dateError: !selectedDate || !isDateValid(selectedDate) ? "Date can't be empty or in the past" : null,
        placesTimeError: !isValidPlacesTime(placesTimeList) ? "Places and time can't be empty" : null,
        startPlaceError: !isValidPlace(startPlace) ? "Start place can't be empty" : null,
        endPlaceError: !isValidPlace(endPlace) ? "End place can't be empty" : null,
        maxHoursToSpendError: !isValidMaxHoursToSpend(selectedDate, maxHoursToSpend) ? "The total travel duration can't end later than one year from now" : null
    };
}