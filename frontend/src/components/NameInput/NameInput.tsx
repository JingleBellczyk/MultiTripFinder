import React, { useEffect, useState } from 'react';
import { Autocomplete, Button, InputWrapper } from '@mantine/core';
import axios from 'axios';
import { SERVER } from "../../constants/constants";
import { SavedSearch } from "../../types/SearchDTO";
import {SavedTripDTO} from "../../types/TripDTO";

interface NameInputProps {
    userId: number | undefined;
    value: string;
    setValue: (value: string) => void;
    data: SavedSearch[] | SavedTripDTO[];
    type: string;
    handleSearchClick: () => Promise<void>;
}

const NameInput: React.FC<NameInputProps> = ({ userId, value, setValue, type, data , handleSearchClick}) => {
    const [names, setNames] = useState<string[]>([]); // State for fetched names
    const [loading, setLoading] = useState<boolean>(false); // State for loading indicator

    useEffect(() => {
        if (userId) {
            const fetchNames = async () => {
                setLoading(true);
                try {
                    const endpoint = `${SERVER}/${type}/${userId}/names`;
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
    }, [data, userId]);

    return (
        <InputWrapper
            style={{ display: 'flex', alignItems: 'center' }}
        >
            <Autocomplete
                value={value}
                onChange={setValue}
                placeholder="Type or choose an name..."
                data={names}
                disabled={loading || !userId}
                limit={5}
            />
            <Button
                variant="outline"
                onClick={handleSearchClick}
                disabled={loading || !userId || !value}
                style={{ marginLeft: '10px' }}
            >
                Search
            </Button>
        </InputWrapper>
    );
};

export default NameInput;
