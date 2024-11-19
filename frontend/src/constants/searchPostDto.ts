import {PlaceLocation,SavedSearchDTO, SearchDTO, SearchDTOPost,Tag, SearchDTOSave} from '../types/SearchDTO'; // Adjust the path as necessary

export const EXAMPLE_SEARCH_POST_DTO: SearchDTOPost = {
    placesToVisit: [
        {
            country: "Poland",
            city: "Wrocław",
            stayDuration: 0,
            entryOrder: 1
        },
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
        },
        {
            country: "Poland",
            city: "Poznań",
            stayDuration: 0,
            entryOrder: 3
        }
    ],
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
export const EXAMPLE_SAVED_SEARCH_1: SavedSearchDTO = {
    placesTime: [
        { name: "Warszawa", city: "Warszawa", country: "Poland", hoursToSpend: 78 },
        { name: "Zakopane", city: "Zakopane", country: "Poland", hoursToSpend: 48 }
    ],
    start: {name: "Wroclaw", city: "Wroclaw", country: "Poland"},
    end: {name: "Wroclaw", city: "Wroclaw", country: "Poland"},
    maxTotalTime: 150,
    transport: "Train",
    startDate: new Date("2025-12-20"),
    saveDate: new Date("2024-11-01"),
    preferredCriteria: "DURATION",
    tags: [
        { name: "TRIPS"},
        { name: "MOUNTAINS" }
    ],
    name: "Mountain trip",
    passengersNumber: 3
};

export const EXAMPLE_SAVED_SEARCH_2: SavedSearchDTO = {
    placesTime: [
        { name: "Madrid", city: "Madrid", country: "Spain", hoursToSpend: 80 },
        { name: "Barcelona", city: "Barcelona", country: "Spain", hoursToSpend: 90 },
        { name: "Seville", city: "Seville", country: "Spain", hoursToSpend: 70 }
    ],
    start: { name: "Lisbon", city: "Lisbon", country: "Portugal" },
    end: { name: "Helsinki", city: "Helsinki", country: "Finland" },
    maxTotalTime: 300,
    transport: "Bus",
    startDate: new Date("2025-07-05"),
    saveDate: new Date("2024-11-12"),
    preferredCriteria: "PRICE",
    tags: [
        { name: "ROAD TRIP" },
        { name: "SUMMER 2025" }
    ],
    name: null,
    passengersNumber: 6
};

export const EXAMPLE_TAGS: Tag[] = [
    { name: "ROAD TRIP"},
    { name: "SUMMER 2025"},
    { name: "TRIPS"},
    { name: "MOUNTAINS"}
]

export const INITIAL_SEARCH_DTO_SAVE: SearchDTOSave = {
    id: null,
    name: null,
    saveDate: null,
    tags: [],
    placesToVisit: [],
    passengerCount: 0,
    maxTripDuration: 0,
    tripStartDate: null,
    preferredTransport: null,
    optimizationCriteria: ""
};
