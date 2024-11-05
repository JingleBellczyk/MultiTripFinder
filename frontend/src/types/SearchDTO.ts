export interface SearchDTO {
    placesTime: PlaceTime[];
    start: string;
    end: string;
    maxTotalTime: number;
    transport: string | null;
    startDate: Date | null;
    discounts: Record<string, number>;
    preferredCriteria: string;
}

export interface SavedSearchDTO {
    placesTime: PlaceTime[];
    start: string;
    end: string;
    maxTotalTime: number;
    passengers: number;
    transport: string | null;
    startDate: Date;
    saveDate: Date;
    preferredCriteria: string;
    tags: Tag[]|null;
    name: string | null;

}
export interface SearchDTOPost {
    places: PlaceTime[];
    passengersNumber: number;
    maxHoursToSpend: number;
    startDate: Date | null;
    preferredTransport: string | null;
    preferredCriteria: string;
}
export interface PlaceTime {
    place: string;
    hoursToSpend: number;
}

export interface Tag {
    name: string;
}