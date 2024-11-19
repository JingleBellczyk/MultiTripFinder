import {SearchDTOPost, SearchDTOSave} from "../../types/SearchDTO"
import {TripList, Trip} from "../../types/TripDTO"
import tripsJsonData from './trips.json';
import {SERVER} from "../../constants/constants";
import axios from "axios";

interface PostResponse {
    id: number;
}

export const postSearch = async (dto: SearchDTOPost): Promise<TripList> => {
    const url = `${SERVER}/search`;

    // const response = await fetch(url, {
    //     method: 'POST',
    //     headers: {
    //         'Content-Type': 'application/json',
    //     },
    //     body: JSON.stringify(dto),
    // });

    // if (!response.ok) {
    //     throw new Error('Network response was not ok');
    // }
    // const data = await response.json();
    // const trips: Trip[] = data.content as Trip[]

    // for tests
    const trips: Trip[] = tripsJsonData.content as Trip[]

    console.log(trips);
    return trips;
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