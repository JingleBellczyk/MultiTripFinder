export interface SearchDTOPost {
    placesToVisit: PlaceTimePost[];
    startPlace: PlaceLocationPost;
    endPlace: PlaceLocationPost;
    passengerCount: number;
    maxTripDuration: number;
    tripStartDate: Date | null;
    preferredTransport: string | null;
    optimizationCriteria: string;
}
export interface SearchDTOSave {
    id?: string | null;
    name?: string | null;
    saveDate?: string | null; // ISO format or null
    tags: string[];
    placesToVisit: PlaceTimePost[];
    startPlace: PlaceLocationPost;
    endPlace: PlaceLocationPost;
    passengerCount: number;
    maxTripDuration: number;
    tripStartDate: Date | null;
    preferredTransport: string | null;
    optimizationCriteria: string;
}

export interface PlaceLocation {
    name: string;
    country: string,
    city: string;
}
export interface PlaceLocationPost {
    country: string;
    city: string;
}

export interface PlaceTime extends PlaceLocation {
    hoursToSpend: number;
}
export interface PlaceTimePost extends PlaceLocationPost {
    stayDuration: number;
    entryOrder: number;
}

export interface SearchDTO {
    placesTime: PlaceTime[];
    start: PlaceLocation;
    end: PlaceLocation;
    maxTotalTime: number;
    transport: string | null;
    startDate: Date | null;
    passengersNumber: number;
    preferredCriteria: string;
}

export interface SavedSearchDTO {
    placesTime: PlaceTime[];
    start: PlaceLocation;
    end: PlaceLocation;
    maxTotalTime: number;
    transport: string | null;
    startDate: Date;
    saveDate: Date;
    passengersNumber: number;
    preferredCriteria: string;
    name: string | null;
    tags: Tag[];
}

export interface Tag {
    name: string;
}