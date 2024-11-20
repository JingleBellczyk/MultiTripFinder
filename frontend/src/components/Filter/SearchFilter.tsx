import {useState} from 'react';
import {
    Title,
    Box,
    Button,
    Stack,
    Space,
    Center,
    Text,
    Autocomplete
} from '@mantine/core';
import '@mantine/dates/styles.css';
import { DatePickerInput } from '@mantine/dates';
import {SavedSearch, SavedTag} from "../../types/SearchDTO";
import {MINIMIZED_CRITERION, PAGE_SIZE, SERVER} from "../../constants/constants";
import {User} from "../../types/UserDTO";
import CustomCombobox from "../Combobox/CustomCombobox";
import TagFilter from './TagFilter';
import CustomCheckBox from '../CheckBox/CustomCheckBox';
import axios from "axios";
import NameInput from "../NameInput/NameInput";
import transformSearches from "../../hooks/transformSearches";

type ResetButtonProps = {
    onReset: () => void;
}

type SearchFilterProps = {
    searches: SavedSearch[];
    tags: SavedTag[];
    user: User | null;
    fetchSearches: (endpoint: string)=> Promise<void>;
    setSearches: (searches: SavedSearch[]) => void;
    setEndpoint: (endpoint: string) => void;
    setTotalPages: (pages: number) => void;
    setCurrentPage: (page: number) => void;
};


type ShowResultsProps = {
    filters: {
        airplaneChecked: boolean;
        busChecked: boolean;
        trainChecked: boolean;
        optimisationCriteria: string | null;
        selectedTags: string[];
        selectedName: string;
        dates: [Date | null, Date | null];
    };
    user: User | null;
    fetchSearches: (endpoint: string)=> Promise<void>;
    setSearches: (searches: SavedSearch[]) => void;
    setEndpoint: (endpoint: string) => void;
    setTotalPages: (pages: number) => void;
    setCurrentPage: (page: number) => void;
};

function ShowResults({ filters, user, fetchSearches, setSearches,  setEndpoint, setTotalPages, setCurrentPage }: ShowResultsProps) {
    const [loading, setLoading] = useState<boolean>(false);

    // Function to handle the fetching of results based on selected filters
    const fetchResults = async () => {
        setLoading(true);

        try {
            if (filters.selectedName){
                const encodedName = encodeURIComponent(filters.selectedName);
                const endpoint = `${SERVER}/searchList/${user?.id}/name/${encodedName}`;
                const response = await axios.get(endpoint, {withCredentials: true});
                const content = response.data;
                const transformed = transformSearches([content]);
                setSearches(transformed);
                setTotalPages(1);
                setCurrentPage(1);

            }
            else {
                const params = new URLSearchParams();
                params.append("size", PAGE_SIZE.toString());
                params.append("page", "0");

                // Add transport filters dynamically
                if (filters.airplaneChecked) params.append("preferredTransports", "PLANE");
                if (filters.busChecked) params.append("preferredTransports", "BUS");
                if (filters.trainChecked) params.append("preferredTransports", "TRAIN");

                // Add optimization criteria
                if (filters.optimisationCriteria) params.append("optimizationCriteria", filters.optimisationCriteria.toUpperCase())

                // Add tags
                filters.selectedTags?.forEach((tag) => {
                    params.append("searchTags", tag);
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

                const endpoint = `${SERVER}/searchList/${user?.id}?${params.toString()}`;
                setEndpoint(endpoint);
                setCurrentPage(1);
                await fetchSearches(endpoint);
            }

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
                disabled={loading} // Disable button when loading
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


export default function SearchFilter({ tags, searches, setSearches, setEndpoint, fetchSearches, user, setCurrentPage, setTotalPages }: SearchFilterProps) {
    const [airplaneChecked, setAirplaneChecked] = useState<boolean>(false);
    const [busChecked, setBusChecked] = useState<boolean>(false);
    const [trainChecked, setTrainChecked] = useState<boolean>(false);
    const [selectedTags, setSelectedTags] = useState<string[]>([]);
    const [selectedName, setSelectedName] = useState<string>('')
    const [dates, setDates] = useState<[Date | null, Date | null]>([null, null]);
    const [optimisationCriteria, setOptimisationCriteria] = useState<string|null>(null);
    const resetFilters = async () => {
        setAirplaneChecked(false);
        setBusChecked(false);
        setTrainChecked(false);
        setOptimisationCriteria(null);
        setSelectedTags([]);
        setSelectedName('')
        setDates([null, null]);

        const endpoint = `${SERVER}/searchList/${user?.id}?size=${PAGE_SIZE}&page=0`
        setEndpoint("");
        await fetchSearches(endpoint);
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
                <Title order={4}>
                    Filter
                </Title>
            </Center>
            <Text fw={500} size="sm">Transport</Text>
            <CustomCheckBox
                label="Airplane"
                checked={airplaneChecked}
                onChange={(event) => setAirplaneChecked(event.currentTarget.checked)}
            />
            <CustomCheckBox
                label="Bus"
                checked={busChecked}
                onChange={(event) => setBusChecked(event.currentTarget.checked)}
            />
            <CustomCheckBox
                label="Train"
                checked={trainChecked}
                onChange={(event) => setTrainChecked(event.currentTarget.checked)}
            />
            <Text fw={500} size="sm">Optimalization Criteria</Text>
            <CustomCombobox options={MINIMIZED_CRITERION} value={optimisationCriteria} setValue={setOptimisationCriteria}></CustomCombobox>
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
                userId={user?.id}
                value={selectedName}
                setValue={setSelectedName} searchData={searches}/>
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
                        airplaneChecked,
                        busChecked,
                        trainChecked,
                        optimisationCriteria,
                        selectedTags,
                        selectedName,
                        dates
                    }}
                    user={user}
                    fetchSearches={fetchSearches}
                    setSearches={setSearches}
                    setCurrentPage={setCurrentPage}
                    setTotalPages={setTotalPages}
                    setEndpoint={setEndpoint}
                />
                <ResetButton onReset={resetFilters}/>
            </Stack>
        </Box>
    );
}