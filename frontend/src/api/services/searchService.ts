import {SearchDTOPost, SearchDTOSave} from "../../types/SearchDTO"
import {TripList, Trip} from "../../types/TripDTO"
import tripsJsonData from './trips.json';

interface PostResponse {
    id: number;
}

const URL: string = "http://localhost:8080/search"
export const postSearch = async (dto: SearchDTOPost): Promise<TripList> => {

    // const response = await fetch(URL, {
    //     method: 'POST',
    //     headers: {
    //         'Content-Type': 'application/json',
    //     },
    //     body: JSON.stringify(dto),
    // });

    // if (!response.ok) {
    //     throw new Error('Network response was not ok');
    // }
        const trips: Trip[] = tripsJsonData.content as Trip[]

        console.log(trips);
        return trips;
    // const data = await response.json();
};

export const postSearchSave = async (dto: SearchDTOSave) => {
    try {
        const response = await fetch('/api/save-search', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dto),
        });

        if (!response.ok) {
            throw new Error('Failed to save search');
        }

        const data = await response.json();
        return data;  // Zakładamy, że backend zwraca odpowiedź w formacie { success: boolean }
    } catch (error) {
        console.error('Error sending search to backend:', error);
        return { success: false };  // Jeśli wystąpi błąd, zwrócimy success: false
    }
};