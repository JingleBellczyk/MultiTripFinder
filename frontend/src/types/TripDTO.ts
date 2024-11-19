import {Tag} from "./SearchDTO";

export interface Transfer {
    startDateTime: string; // ISO format
    endDateTime: string; // ISO format
    carrier: string;
    transportMode: string;
    cost: number;
    duration: number;
    transferOrder: number;
    startAddress: string;
    endAddress: string;
}

export interface Place {
    country: string;
    city: string;
    isTransfer: boolean;
    stayDuration: number;
    visitOrder: number;
}

export interface Trip {
    id?: string | null;
    name?: string | null;
    saveDate?: string | null; // ISO format or null
    tags: string[];
    startDate: string; // ISO format
    endDate: string; // ISO format
    passengerCount: number;
    totalCost: number;
    totalTransferTime: number;
    duration: number;
    transfers: Transfer[];
    places: Place[];
}

export type TripList = Trip[];


export const sampleTrip: SavedTripDTO = {
    id: 1,
    name: "Summer Vacation 2024",
    saveDate: "2024-11-15",
    tags: [{name: "beach"}, {name: "relax"}],
    startDate: "2024-12-01",
    endDate: "2024-15-01",
    passengerCount: 2,
    totalCost: 300,
    totalTransferTime: 6,
    duration: 84,
    transfers: [
        {
            startDateTime: "2024-12-01, 08:00:00",
            endDateTime: "2024-12-01, 10:00:00",
            carrier: "Airline A",
            transportMode: "Flight",
            cost: 200,
            duration: 2,
            startAddress: "City A, Airport",
            endAddress: "City B, Airport"
        },
        {
            startDateTime: "2024-15-01, 08:00:00",
            endDateTime: "2024-15-01, 12:00:00",
            carrier: "Airline A",
            transportMode: "Flight",
            cost: 100,
            duration: 4,
            startAddress: "City A, Airport",
            endAddress: "City B, Airport"
        }
    ],
    places: [
        {country: "France", city: "Paris", isTransfer: false, stayDuration: 0},
        {country: "Italy", city: "Rome", isTransfer: false, stayDuration: 78},
        {country: "Poland", city: "Warsaw", isTransfer: false, stayDuration: 0}
    ]
}

export interface TripPlaceDTO {
    country: string;
    city: string;
    isTransfer: boolean;
    stayDuration: number;
}

export interface TransferDTO {
    startDateTime: string;
    endDateTime: string;
    carrier: string;
    transportMode: string;
    cost: number;
    duration: number;
    startAddress: string;
    endAddress: string;
}
export interface SavedTripDTO {
    id: number;
    name: string;
    saveDate: string;
    tags: Tag[];
    startDate: string;
    endDate: string;
    passengerCount: number;
    totalCost: number;
    totalTransferTime: number;
    duration: number;
    transfers: TransferDTO[];
    places: TripPlaceDTO[];
}