import { PlaceTime, PlaceLocation } from "../types/SearchDTO";
import { validCountries } from "../constants/countries";

export const isDateValid = (selectedDate: Date | null): boolean => {
    if (!selectedDate) return false;
    console.log("valid", selectedDate)
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    return selectedDate > now;
};

export const isValidPlacesTime = (placesTime: PlaceTime[]): string | null => {
    if (placesTime.length > 0) {
        for (const placeTime of placesTime) {
            if (!placeTime.name || placeTime.name.trim() === "") {
                return "Place can't be empty";
            }

            if (!validCountries.includes(placeTime.country)) {
                return "Place must be in Europe";
            }

            if (placeTime.hoursToSpend <= 0) {
                return "Time can't be empty";
            }

            if (!placeTime.city || placeTime.city.trim() === "") {
                return "Place can't be empty";
            }
        }
    }
    return null;
};

export const isValidPlace = (place: PlaceLocation | null): string | null => {
    if (
        !place ||
        place.name.trim() === "" ||
        place.country.trim() === "" ||
        place.city.trim() === ""
    ) {
        return "Place can't be empty";
    }

    if (!validCountries.includes(place.country)) {
        return "Place must be in Europe";
    }

    return null;
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
    dateError: string | null;
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
    placesTimeError: string | null;
} {
    return {
        dateError: !selectedDate || !isDateValid(selectedDate) ? "Date can't be empty or in the past" : null,
        placesTimeError: isValidPlacesTime(placesTimeList),
        startPlaceError: isValidPlace(startPlace),
        endPlaceError: isValidPlace(endPlace),
        maxHoursToSpendError: !isValidMaxHoursToSpend(selectedDate, maxHoursToSpend) ? "The total travel duration can't end later than one year from now" : null
    };
}

export const formatDuration = (minutes: number) => {
    const hours = Math.floor(minutes / 60); // Convert minutes to hours
    const days = Math.floor(hours / 24); // Convert hours to days

    if (minutes >= 1440) { // More than or equal to 1 day
        const remainingHours = hours % 24; // Hours left after full days
        return `${days} day${days > 1 ? 's' : ''}${remainingHours > 0 ? ` and ${remainingHours} hour${remainingHours > 1 ? 's' : ''}` : ''}`;
    } else if (minutes >= 60) { // More than or equal to 1 hour, less than 1 day
        const remainingMinutes = minutes % 60; // Minutes left after full hours
        return `${hours} hour${hours > 1 ? 's' : ''}${remainingMinutes > 0 ? ` and ${remainingMinutes} minute${remainingMinutes > 1 ? 's' : ''}` : ''}`;
    } else { // Less than 1 hour
        return `${minutes} minute${minutes !== 1 ? 's' : ''}`;
    }
};
