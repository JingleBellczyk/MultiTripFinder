import {PlaceLocation, SearchDTO, SearchDTOPost} from '../types/SearchDTO'; // Adjust the path as necessary

export const EXAMPLE_SEARCH_POST_DTO: SearchDTOPost = {
    placesToVisit: [

        {
            country: "Italy",
            city: "Lazio",
            stayDuration: 80,
            entryOrder: 1
        },
        {
            country: "United Kingdom",
            city: "London",
            stayDuration: 105,
            entryOrder: 2
        }
    ],
    startPlace:         {
        country: "Poland",
        city: "Poznań",
    },
    endPlace:         {
        country: "Poland",
        city: "Poznań",
    },
    passengerCount: 5,
    maxTripDuration: 240,
    tripStartDate: new Date("2024-11-28T16:14:52.213Z"),
    preferredTransport: "BUS",
    optimizationCriteria: "DURATION",
}

const defaultPlaceLocation: PlaceLocation = {
    name: '',
    country: '',
    city: '',
};

export const EXAMPLE_SEARCH_DTO: SearchDTO = {
    placesTime: [
        {name: 'Place A', country: "Poland", city: 'City A', hoursToSpend: 124},
        {name: 'Place B',  country: "Poland", city: 'City B', hoursToSpend: 167},
    ],
    start: {name: 'Place A', country: "Poland", city: 'City A'},
    end: {name: 'Place A', country: "Poland", city: 'City A'},
    maxTotalTime: 6,
    transport: "Bus",
    startDate: new Date(),
    passengersNumber: 6,
    preferredCriteria: 'DURATION'
};

export const EMPTY_SEARCH_DTO: SearchDTO = {
    placesTime: [],
    start: defaultPlaceLocation,
    end: defaultPlaceLocation,
    maxTotalTime: 1,
    transport: null,
    startDate: null,
    passengersNumber: 1,
    preferredCriteria: 'PRICE'
};