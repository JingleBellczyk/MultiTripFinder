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

export interface ValidationErrors {
    dateError: string | null,
    placesTimeError: string | null;
    startPlaceError: string | null;
    endPlaceError: string | null;
}

export function validateForm(
    selectedDate: Date | null,
    placesTimeList: PlaceTime[],
    startPlace: string | null,
    endPlace: string | null
): ValidationErrors {
    return {
        dateError: !selectedDate || !isDateValid(selectedDate) ? "Date can't be empty or in the past" : null,
        placesTimeError: !isValidPlacesTime(placesTimeList) ? "Places and time can't be empty" : null,
        startPlaceError: !isValidPlace(startPlace) ? "Start place can't be empty" : null,
        endPlaceError: !isValidPlace(endPlace) ? "End place can't be empty" : null,
    };
}