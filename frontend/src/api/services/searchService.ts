import {SearchDTOPost, SearchDTOSave} from "../../types/SearchDTO"
import {TripList, Trip} from "../../types/TripDTO"
import tripsJsonData from './trips.json';
import {SERVER} from "../../constants/constants";
import axios from "axios";

interface PostResponse {
    id: number;
}

export const postSearch = async (dto: SearchDTOPost): Promise<Trip[]> => {
    const url = `${SERVER}/search`;

    try {
        const response = await axios.post(url, dto, {
            headers: { 'Content-Type': 'application/json' },
        });

        const trips: Trip[] = response.data.content;
        // const trips: Trip[] = tripsJsonData.content as Trip[]
        return trips;

    } catch (error) {
        if (axios.isAxiosError(error) && error.response) {
            const errorContent = error.response.data || 'An error occurred without detailed content';
            throw errorContent;
        } else {
            throw new Error('An unexpected error occurred');
        }
    }
};


export const postSearchSave = async (dto: SearchDTOSave, userId: number) => {
    const url = `${SERVER}/searchList/${userId}`;
    console.log(url);

    try {
        const response = await axios.post(url, dto, {
            headers: { 'Content-Type': 'application/json' },
            withCredentials: true,
        });

        console.log("Received response:", response.data);
        return { success: true, data: response.data };

    } catch (error: unknown) {
        if (axios.isAxiosError(error) && error.response) {

            if (error.response.status === 409) {
                console.warn("Conflict: A record with similar data already exists.");
                return { success: false, error: "Conflict: This name already exists" };
            }

            console.error('Error sending search to backend:', error.response.data.message || 'Unknown error');
            return { success: false, error: error.response.data.message || 'Failed to save search' };
        } else if (error instanceof Error) {
            console.error('Error sending search to backend:', error.message);
            return { success: false, error: error.message };
        } else {
            console.error('Unknown error occurred:', error);
            return { success: false, error: 'An unknown error occurred' };
        }
    }
};