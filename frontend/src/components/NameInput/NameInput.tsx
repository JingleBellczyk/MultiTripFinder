import React, { useEffect, useState } from 'react';
import { Autocomplete } from '@mantine/core';
import axios from 'axios';
import {SERVER} from "../../constants/constants";
import {SavedSearch} from "../../types/SearchDTO";

interface NameInputProps {
    userId: number | undefined;
    value: string;
    setValue: (value: string) => void;
    searchData: SavedSearch[];

}


const NameInput: React.FC<NameInputProps> = ({ userId, value, setValue, searchData }) => {
    const [names, setNames] = useState<string[]>([]); // State for fetched names
    const [loading, setLoading] = useState<boolean>(false); // State for loading indicator

    useEffect(() => {
        if (userId) {
            const fetchNames = async () => {
                setLoading(true);
                try {
                    const endpoint = `${SERVER}/searchList/${userId}/names`;
                    const response = await axios.get(endpoint, { withCredentials: true });
                    setNames(response.data || []);
                } catch (error) {
                    console.error("Error fetching names:", error);
                } finally {
                    setLoading(false);
                }
            };

            fetchNames();
        }
    }, [searchData]);

    return (
        <Autocomplete
            value={value}
            onChange={setValue}
            label="Select or type a name"
            placeholder="Type a name"
            data={names}
            disabled={loading || !userId}
            limit={5}
        />
    );
};

export default NameInput;
