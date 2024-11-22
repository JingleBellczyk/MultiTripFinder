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

export interface SavedTripDTO {
    id: number;
    name: string;
    saveDate: Date;
    tags: Tag[];
    startDate: Date;
    endDate: Date;
    passengerCount: number;
    totalCost: number;
    totalTransferTime: number;
    duration: number;
    allTransports: string[];
    transfers: Transfer[];
    places: Place[];
}