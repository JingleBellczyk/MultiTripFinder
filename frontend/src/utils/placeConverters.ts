import {SearchDTOPost, SearchDTO, PlaceLocation, PlaceTime, PlaceTimePost} from '../types/SearchDTO';

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

    const transport = dtoPost.preferredTransport ? transportMapping[dtoPost.preferredTransport.toUpperCase()] : null;

    const startPlace = dtoPost.startPlace
    const endPlace = dtoPost.endPlace

    const startLocation: PlaceLocation = {
        name: `${startPlace.city}, ${startPlace.country}`,
        country: startPlace.country,
        city: startPlace.city
    };

    const endLocation: PlaceLocation = {
        name: `${endPlace.city}, ${endPlace.country}`,
        country: endPlace.country,
        city: endPlace.city
    };

    return {
        placesTime: placesTime,
        start: startLocation,
        end: endLocation,
        maxTotalTime: dtoPost.maxTripDuration/24,
        transport: transport,
        startDate: dtoPost.tripStartDate,
        passengersNumber: dtoPost.passengerCount,
        preferredCriteria: dtoPost.optimizationCriteria,
    };
}

export function convertToPlaceTimePost(places: PlaceTime[]): PlaceTimePost[] {
    return places.map((place, index) => ({
        country: place.country,
        city: place.city,
        stayDuration: place.hoursToSpend,
        entryOrder: index + 1
    }));
}
