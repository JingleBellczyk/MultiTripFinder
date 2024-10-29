import {SearchDTOPost} from "../../types/SearchDTO"

interface PostResponse {
    id: number;
}
const URL: string = "http://localhost:8080/search"
export const postSearch = async (dto: SearchDTOPost): Promise<PostResponse> => {

    const response = await fetch(URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(dto),
    });

    if (!response.ok) {
        throw new Error('Network response was not ok');
    }

    const data = await response.json();
    return data;
};