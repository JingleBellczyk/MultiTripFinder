import {PlaceTime, SavedSearch, SavedSearchDTO, SavedTag} from "../types/SearchDTO";


const transformSearches = (content: any[]): SavedSearch[] => {
    console.log(content);
    return content.map(search => {
        const placesTime = search.placesToVisit.slice(1, -1).map((place: any) : PlaceTime => ({
            name: place.city,
            city: place.city,
            country: place.country,
            hoursToSpend: place.stayDuration
        }));
        console.log("ok")
        const startPlace = search.placesToVisit[0]
        const endPlace = search.placesToVisit[search.placesToVisit.length - 1]
        const start = { name: '', city: startPlace.city, country: startPlace.country };
        const end = { name: '', city: endPlace.city, country: endPlace.country };

        return {
            id: search.id,
            placesTime,
            start: start,
            end: end,
            maxTotalTime: search.maxTripDuration,
            transport: search.preferredTransport,
            startDate: new Date(search.tripStartDate),
            saveDate: new Date(search.saveDate),
            preferredCriteria: search.optimizationCriteria,
            tags: search.tags.map((tag: string) => ({ name: tag })),
            name: search.name,
            passengersNumber: search.passengerCount
        };
    });
};
export default transformSearches;