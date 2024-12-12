import {Trip} from "../../types/TripDTO";
import axios from 'axios';

const BASE_URL: string = "http://mtf.norwayeast.cloudapp.azure.com/api";

export async function saveTripToBackend(trip: Trip, userId: number): Promise<{ isSuccess: boolean; errorMessage?: string }> {
    console.log("Saving trip to backend");
    console.log(trip);
    const url = `${BASE_URL}/tripList/${userId}`;

    try {
        // Axios POST request
        const response = await axios.post(url, trip, {
            headers: { 'Content-Type': 'application/json' },
            withCredentials: true,
        });
        console.log("Received response:", response.data);

        return { isSuccess: true };
    } catch (error: unknown) {
        if (axios.isAxiosError(error) && error.response) {
            console.log(error.response.data)
            console.error("Error saving trip:", error.response.data || "Unknown error");

            return {
                isSuccess: false,
                errorMessage: error.response.data || "An error occurred while saving the trip.",
            };
        } else if (error instanceof Error) {
            console.error("Error saving trip:", error.message);
            return {
                isSuccess: false,
                errorMessage: error.message,
            };
        } else {
            console.error("Unknown error occurred:", error);
            return {
                isSuccess: false,
                errorMessage: "An unknown error occurred",
            };
        }
    }
}

