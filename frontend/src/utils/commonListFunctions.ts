import { SERVER } from "../constants/constants";
import axios from "axios";
import transformSearches from "./transformSearches";
import { SavedSearch } from "../types/SearchDTO";
import { SavedTripDTO } from "../types/TripDTO";
import { User } from "../types/UserDTO";
import transformTrips from "./transformTrips";
import {Simulate} from "react-dom/test-utils";
import error = Simulate.error;
import {Dispatch, SetStateAction} from "react";

export const handleSearchForName = async (
    type: "searchList" | "tripList",
    selectedName: string,
    setElements: Dispatch<SetStateAction<SavedSearch[]>> | Dispatch<SetStateAction<SavedTripDTO[]>>,
    setTotalPages: (num: number) => void,
    setCurrentPage: (num: number) => void,
    user: User | null
) => {
    try {
        if (selectedName && user) {
            const encodedName = encodeURIComponent(selectedName);
            const endpoint = `${SERVER}/${type}/${user.id}/name/${encodedName}`;
            const response = await axios.get(endpoint, { withCredentials: true });

            const content = response.data;
            console.log(content);

            if (type === "searchList") {
                const transformed = transformSearches([content]); // Returns SavedSearch[]
                (setElements as Dispatch<SetStateAction<SavedSearch[]>>)(transformed);
            } else if (type === "tripList") {
                const transformed = transformTrips([content]); // Returns SavedTripDTO[]
                (setElements as Dispatch<SetStateAction<SavedTripDTO[]>>)(transformed);
            }

            setTotalPages(1); // Adjust based on response if needed
            setCurrentPage(1);
        } else {
            console.error("Invalid input: selectedName or user is missing.");
        }
    } catch (error) {
        console.error("Error in searching for name: ", error);
    }
};

export  const getBadgeColor = (element: string | null) => {
    switch (element) {
        case 'TRAIN':
            return 'red';
        case 'PLANE':
            return 'indigo';
        default:
            return 'green';
    }
};
