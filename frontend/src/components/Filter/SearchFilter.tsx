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

type ResetButtonProps = {
    onReset: () => void;
}

type SearchFilterProps = {
    searches: SavedSearch[];
    tags: SavedTag[];
    user: User | null;
    fetchSearches: (endpoint: string)=> Promise<void>;
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
};

function ShowResults({ filters, user, fetchSearches }: ShowResultsProps) {
    const [loading, setLoading] = useState<boolean>(false);

    // Function to handle the fetching of results based on selected filters
    const fetchResults = async () => {
        setLoading(true);

        try {
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
                const [startDate, endDate] = filters.dates;
                if (startDate && endDate) {
                    let currentDate = new Date(startDate);
                    const end = new Date(endDate);

                    // Set currentDate to the start of the day (midnight) to avoid timezone issues
                    currentDate.setHours(0, 0, 0, 0);
                    end.setHours(23, 59, 59, 999);

                    while (currentDate <= end) {
                        const formattedDate = currentDate.toISOString().split("T")[0]; // YYYY-MM-DD
                        params.append("saveDate", formattedDate);
                        currentDate.setDate(currentDate.getDate() + 1);
                    }
                }
            }

            const endpoint = `${SERVER}/searchList/${user?.id}?${params.toString()}`;
            await fetchSearches(endpoint);
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


export default function SearchFilter({ tags, searches, fetchSearches, user }: SearchFilterProps) {
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

        await fetchSearches(`${SERVER}/searchList/${user?.id}?size=${PAGE_SIZE}&page=0`);
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
                names={[]}
                value={selectedName}
                setValue={setSelectedName}/>

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
                />
                <ResetButton onReset={resetFilters}/>
            </Stack>
        </Box>
    );
}