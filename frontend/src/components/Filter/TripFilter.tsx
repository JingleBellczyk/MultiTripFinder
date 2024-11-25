import { useState } from 'react';
import {
    Title,
    Box,
    Button,
    Stack,
    Space,
    Center,
} from '@mantine/core';
import '@mantine/dates/styles.css';
import { DatePickerInput } from '@mantine/dates';
import { SavedTag} from "../../types/SearchDTO";
import {PAGE_SIZE, SERVER, TRIP_LIST} from "../../constants/constants";
import { User } from "../../types/UserDTO";
import TagFilter from "./TagFilter";

type ResetButtonProps = {
    onReset: () => void;
};

type ShowResultsProps = {
    filters: {
        selectedTags: string[];
        dates: [Date | null, Date | null];
    };
    user: User | null;
    fetchTrips: (endpoint: string) => Promise<void>;
    setEndpoint: (endpoint: string) => void;
    setCurrentPage: (page: number) => void;
};

function ShowResults({ filters, user, fetchTrips, setEndpoint, setCurrentPage }: ShowResultsProps) {
    const [loading, setLoading] = useState<boolean>(false);

    const fetchResults = async () => {
        setLoading(true);

        try {
            const params = new URLSearchParams();
            params.append("size", PAGE_SIZE.toString());
            params.append("page", "0");

            filters.selectedTags?.forEach((tag) => {
                params.append("tripTags", tag);
            });

            // Add date range filters
            if (filters.dates) {
                const [fromDate, toDate] = filters.dates;
                if (fromDate && toDate) {
                    const start = new Date(fromDate);
                    const end = new Date(toDate);

                    start.setHours(23, 59, 59, 999);
                    end.setHours(23, 59, 59, 999);
                    const formattedStartDate = start.toISOString().split("T")[0];
                    const formattedEndDate = end.toISOString().split("T")[0];
                    params.append("fromDate", formattedStartDate);
                    params.append("toDate", formattedEndDate);
                    console.log("fromDate", formattedStartDate);
                    console.log("toDate", formattedEndDate);
                }
            }

            const endpoint = `${SERVER}/${TRIP_LIST}/${user?.id}?${params.toString()}`;
            setEndpoint(endpoint);
            setCurrentPage(1);
            await fetchTrips(endpoint);
        } catch (error) {
            console.error('Error fetching data', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Button
            onClick={fetchResults}
            size="md"
            radius="lg"
            variant="filled"
            fullWidth
            disabled={loading}
        >
            {loading ? 'Loading...' : 'Show results'}
        </Button>
    );
}

function ResetButton({ onReset }: ResetButtonProps) {
    return (
        <Button onClick={onReset} radius="lg" size="md" variant="outline" fullWidth>
            Reset
        </Button>
    );
}
type TripFilterProps = {
    tags: SavedTag[];
    user: User | null;
    fetchTrips: (endpoint: string) => Promise<void>;
    setEndpoint: (endpoint: string) => void;
    setCurrentPage: (page: number) => void;
};

export default function TripFilter({ tags, fetchTrips, user, setEndpoint, setCurrentPage }: TripFilterProps) {
    const [selectedTags, setSelectedTags] = useState<string[]>([]);
    const [dates, setDates] = useState<[Date | null, Date | null]>([null, null]);

    const resetFilters = async () => {
        setSelectedTags([]);
        setDates([null, null]);

        await fetchTrips(`${SERVER}/tripList/${user?.id}?size=${PAGE_SIZE}&page=0`);
    };

    const tagNames = tags.map(tag => tag.name);

    return (
        <Box
            style={{
                backgroundColor: 'white',
                color: 'black',
                padding: '20px',
                borderRadius: '20px',
            }}
        >
            <Center>
                <Title order={4}>Filter</Title>
            </Center>
            <TagFilter
                list={tagNames}
                label="Choose tags"
                value={selectedTags}
                onValueChange={(val, isRemove) => {
                    if (isRemove) {
                        setSelectedTags((current) => current.filter((item) => item !== val));
                    } else {
                        setSelectedTags((current) => (current.includes(val) ? current : [...current, val]));
                    }
                }}
            />
            <DatePickerInput
                type="range"
                label="Pick dates range"
                placeholder="Pick dates range"
                value={dates}
                onChange={setDates}
            />
            <Space h="lg" />
            <Stack align="center" justify="center">
                <ShowResults
                    filters={{
                        selectedTags,
                        dates
                    }}
                    user={user}
                    fetchTrips={fetchTrips}
                    setCurrentPage={setCurrentPage}
                    setEndpoint={setEndpoint}
                />
                <ResetButton onReset={resetFilters} />
            </Stack>
        </Box>
    );
}
