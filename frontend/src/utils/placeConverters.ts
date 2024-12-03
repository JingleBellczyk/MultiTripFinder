import {SearchDTOPost, SearchDTO, PlaceLocation, PlaceTime, PlaceTimePost, SavedSearch, Tag} from '../types/SearchDTO';
import {stringToDate} from "./formatDateToReadableString";

const transportMapping: { [key: string]: string } = {
    'BUS': 'Bus',
    'TRAIN': 'Train',
    'PLANE': 'Plane'
};
export function convertSearchDTOPostToSearchDTO(dtoPost: SearchDTOPost): SearchDTO {

    const placesTime: PlaceTime[] = dtoPost.placesToVisit.map((place: PlaceTimePost) => ({
        name: `${place.city}, ${place.country}`, // Tworzenie name poprzez połączenie city i country
        country: place.country,
        city: place.city,
        hoursToSpend: place.stayDuration
    }));

    const startPlace: PlaceLocation = {
        name: `${placesTime[0].city}, ${placesTime[0].country}`,
        country: placesTime[0].country,
        city: placesTime[0].city,
    };

    const endPlace: PlaceLocation = {
        name: `${placesTime[placesTime.length - 1].city}, ${placesTime[placesTime.length - 1].country}`,
        country: placesTime[placesTime.length - 1].country,
        city: placesTime[placesTime.length - 1].city,
    };

    const trimmedPlacesTime = placesTime.slice(1, placesTime.length - 1);

    return {
        placesTime: trimmedPlacesTime,
        start: startPlace,
        end: endPlace,
        maxTotalTime: dtoPost.maxTripDuration / 24,
        transport: dtoPost.preferredTransport ? transportMapping[dtoPost.preferredTransport.toUpperCase()] : null,
        startDate: stringToDate(dtoPost.tripStartDate),
        passengersNumber: dtoPost.passengerCount,
        preferredCriteria: dtoPost.optimizationCriteria,
    };
}
export function convertSavedSearchToSearchDTO(savedSearch: SavedSearch): SearchDTO {
    const placesTime: PlaceTime[] = savedSearch.placesTime.map((place: PlaceTime) => ({
        name: `${place.city}, ${place.country}`, // Tworzenie name poprzez połączenie city i country
        country: place.country,
        city: place.city,
        hoursToSpend: place.hoursToSpend
    }));

    const startPlace: PlaceLocation = {
        name: `${savedSearch.start.city}, ${savedSearch.start.country}`,
        country: savedSearch.start.country,
        city:savedSearch.start.city,
    };

    const endPlace: PlaceLocation = {
        name: `${savedSearch.end.city}, ${savedSearch.end.country}`,
        country: savedSearch.end.country,
        city: savedSearch.end.city,
    };

    return {
        placesTime: placesTime,
        start: startPlace,
        end: endPlace,
        maxTotalTime: savedSearch.maxTotalTime / 24,
        transport: savedSearch.transport ? transportMapping[savedSearch.transport.toUpperCase()] : null,
        startDate: savedSearch.startDate,
        passengersNumber: savedSearch.passengersNumber,
        preferredCriteria: savedSearch.preferredCriteria,
    };
}

export function convertToPlaceTimePost(
    places: PlaceTime[],
    startPlace: PlaceLocation,
    endPlace: PlaceLocation
): PlaceTimePost[] {
    const fullPlacesList = [
        { country: startPlace.country, city: startPlace.city, hoursToSpend: 0 },
        ...places,
        { country: endPlace.country, city: endPlace.city, hoursToSpend: 0 },
    ];

    return fullPlacesList.map((place, index) => ({
        country: place.country,
        city: place.city,
        stayDuration: place.hoursToSpend,
        entryOrder: index + 1,
    }));
}

