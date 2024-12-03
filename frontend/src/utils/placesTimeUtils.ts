import {PlaceTime, PlaceLocation} from "../types/SearchDTO";

/**
 * functions to valid search page
 */

export const isDateValid = (selectedDate: Date | null): boolean => {
    if (!selectedDate) return false; // If the date is null, it's not valid
    console.log("valid", selectedDate)
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    return selectedDate > now; // Return true if selectedDate is today or in the future
};

export const isValidPlacesTime = (placesTime: PlaceTime[]): boolean => {
    if(placesTime.length > 0){
        for (const placeTime of placesTime) {
            if (!placeTime.name || placeTime.name.trim() === "") {
                return false;
            }
            if (placeTime.hoursToSpend <= 0) {
                return false;
            }
        }
    }
    return true;
};

export const isValidPlace = (place: PlaceLocation | null ): boolean => {
    if (typeof place?.name !== "string" || place?.name === "" || place?.country === "" || place?.city === "") {
        return false;
    }
    return true;
};

export const isValidMaxHoursToSpend = (selectedDate: Date | null, maxHoursToSpend: number): boolean => {
    if (!selectedDate || !maxHoursToSpend || maxHoursToSpend === 0) return false;

    const daysToSpend = Math.floor(maxHoursToSpend / 24);
    const hoursToSpend = maxHoursToSpend % 24;

    const endDate = new Date(selectedDate);
    endDate.setDate(endDate.getDate() + daysToSpend);
    endDate.setHours(endDate.getHours() + hoursToSpend);

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
    startPlace: PlaceLocation | null,
    endPlace: PlaceLocation | null,
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
        placesTimeError: !isValidPlacesTime(placesTimeList) ? "Places or time can't be empty" : null,
        startPlaceError: !isValidPlace(startPlace) ? "Start place can't be empty" : null,
        endPlaceError: !isValidPlace(endPlace) ? "End place can't be empty" : null,
        maxHoursToSpendError: !isValidMaxHoursToSpend(selectedDate, maxHoursToSpend) ? "The total travel duration can't end later than one year from now" : null
    };
}