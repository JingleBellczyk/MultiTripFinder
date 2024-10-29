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
export interface SearchDTOPost {
    places: PlaceTime[];
    passengersNumber: number;
    hoursToSpend: number;
    startDate: Date | null;
    preferredTransport: string | null;
    preferredCriteria: string;
}
export interface PlaceTime {
    place: string;
    hoursToSpend: number;
}