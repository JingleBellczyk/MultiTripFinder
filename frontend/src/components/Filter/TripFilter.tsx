import { useEffect, useState } from 'react';
import {
    Title,
    Box,
    Button,
    Stack,
    Space,
    Center,
    Autocomplete
} from '@mantine/core';
import '@mantine/dates/styles.css';
import { DatePickerInput } from '@mantine/dates';
import { SavedSearch, SavedSearchDTO, SavedTag, Tag } from "../../types/SearchDTO";
import { PAGE_SIZE, SERVER } from "../../constants/constants";
import { User } from "../../types/UserDTO";
import CustomCombobox from "../Combobox/CustomCombobox";
import TagFilter from "./TagFilter";

type ResetButtonProps = {
    onReset: () => void;
};


type TripFilterProps = {
    tags: SavedTag[];
    user: User | null;
    fetchTrips: (endpoint: string) => Promise<void>;
};

type ShowResultsProps = {
    filters: {
        selectedTags: string[];
        selectedName: string;
        dates: [Date | null, Date | null];
    };
    user: User | null;
    fetchTrips: (endpoint: string) => Promise<void>;
};

function ShowResults({ filters, user, fetchTrips }: ShowResultsProps) {
    const [loading, setLoading] = useState<boolean>(false);

    const fetchResults = async () => {
        setLoading(true);

        try {
            const params = new URLSearchParams();
            params.append("size", PAGE_SIZE.toString());
            params.append("page", "0");

            // Add tags
            filters.selectedTags?.forEach((tag) => {
                params.append("searchTags", tag);
            });

            // Add date range filters
            if (filters.dates) {
                const [startDate, endDate] = filters.dates;
                if (startDate && endDate) {
                    let currentDate = new Date(startDate);
                    const end = new Date(endDate);

                    currentDate.setHours(0, 0, 0, 0);
                    end.setHours(23, 59, 59, 999);

                    while (currentDate <= end) {
                        const formattedDate = currentDate.toISOString().split("T")[0];
                        params.append("saveDate", formattedDate);
                        currentDate.setDate(currentDate.getDate() + 1);
                    }
                }
            }

            const endpoint = `${SERVER}/searchList/${user?.id}?${params.toString()}`;
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



interface NameInputProps {
    names: string[];
    value: string;
    setValue: (value: string) => void;
}

const NameInput: React.FC<NameInputProps> = ({ names, value, setValue }) => {
    return (
        <Autocomplete
            value={value}
            onChange={setValue}
            label="Select or type a name"
            placeholder="Type a name"
            data={names}
        />
    );
};


export default function TripFilter({ tags, fetchTrips, user }: TripFilterProps) {
    const [selectedTags, setSelectedTags] = useState<string[]>([]);
    const [selectedName, setSelectedName] = useState<string>('');
    const [dates, setDates] = useState<[Date | null, Date | null]>([null, null]);

    const resetFilters = async () => {
        setSelectedTags([]);
        setSelectedName('');
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
            <NameInput
                names={[]}
                value={selectedName}
                setValue={setSelectedName}
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
                        selectedName,
                        dates
                    }}
                    user={user}
                    fetchTrips={fetchTrips}
                />
                <ResetButton onReset={resetFilters} />
            </Stack>
        </Box>
    );
}
