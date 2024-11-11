import { useState } from 'react';
import { Button, Group, Transition, MantineProvider } from '@mantine/core';
import SearchFilter from '../components/Filter/SearchFilter';
import SearchTable from '../components/Table/SearchTable';
import { HeaderSearch } from "../components/HeaderSearch/HeaderSearch";
import styles from './SearchesList.module.css';
import {SavedSearchDTO, Tag} from "../types/SearchDTO";


const search1: SavedSearchDTO = {
    placesTime: [
        { place: "Polska, Kraków", hoursToSpend: 78 },
        { place: "Polska, Zakopane", hoursToSpend: 48 }
    ],
    start: "Polska, Wrocław",
    end: "Polska, Wrocław",
    maxTotalTime: 150,
    transport: "train",
    startDate: new Date("2025-12-20"),
    saveDate: new Date("2024-11-01"),
    preferredCriteria: "DURATION",
    tags: [
        { name: "TRIPS"},
        { name: "MOUNTAINS" }
    ],
    name: null,
    passengers: 3
};

const search2: SavedSearchDTO = {
    placesTime: [
        { place: "Berlin", hoursToSpend: 120 },
        { place: "Warsaw", hoursToSpend: 80 },
        { place: "Oslo", hoursToSpend: 90 },
        { place: "Madrid", hoursToSpend: 100 }
    ],
    start: "Rome",
    end: "Paris",
    maxTotalTime: 400,
    transport: "airplane",
    startDate: new Date("2025-01-30"),
    saveDate: new Date("2024-11-01"),
    preferredCriteria: "PRICE",
    tags: [
        { name: "WINTER TRAVEL"},
        { name: "2025" },
        { name: "TRIPS"}
    ],
    name: "My first trip",
    passengers: 5
};

const search3: SavedSearchDTO = {
    placesTime: [
        { place: "Berlin", hoursToSpend: 120 },
        { place: "Warsaw", hoursToSpend: 80 },
        { place: "Oslo", hoursToSpend: 90 },
        { place: "Madrid", hoursToSpend: 100 }
    ],
    start: "Rome",
    end: "Paris",
    maxTotalTime: 400,
    transport: "airplane",
    startDate: new Date("2025-01-30"),
    saveDate: new Date("2024-11-01"),
    preferredCriteria: "DURATION",
    tags: [

    ],
    name: "My first trip",
    passengers: 5
};

const exampleElements: SavedSearchDTO[] = [search1, search2, search3];
const exampleTags: Tag[] = [{name: "WINTER TRAVEL"},
    {name: "TEST"},
    {name: "2025"},
    {name: "2024"},
    {name: "TRIPS"},
    {name: "MOUNTAINS"}];

const SearchesList: React.FC = () => {
    const [isFilterVisible, setIsFilterVisible] = useState<boolean>(true);
    const [isFullSearch, setIsFullSearch] = useState<boolean>(false);
    const [allTags, setAllTags] = useState<Tag[]>(exampleTags);
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false);


    const toggleFilter = () => {
        setIsFilterVisible(!isFilterVisible);
        setIsFullSearch(!isFullSearch);
    };

    return (
        <MantineProvider theme={{primaryColor: 'blue'}}>
            <div className={styles.background}>
                <HeaderSearch/>
                <Button
                    onClick={toggleFilter}
                    className={styles.fixedButton}
                    disabled={isModalOpen}
                >
                    {isFilterVisible ? 'Hide' : 'Filter'}
                </Button>
                <Group className={styles.mainGroup} gap="xl" justify="center">
                    <Transition
                        mounted={isFilterVisible}
                        transition="slide-right"
                        duration={400}
                        timingFunction="ease"
                    >
                        {(transitionStyle) => (
                            <div style={transitionStyle} className={styles.filterContainer}>
                                <SearchFilter
                                    tags={allTags}
                                    searches={exampleElements}
                                />
                            </div>
                        )}
                    </Transition>
                    <div className={styles.tableContainer}>
                        <SearchTable
                            searches={exampleElements}
                            tags={allTags}
                            setTags={setAllTags}
                            setIsModalOpen={setIsModalOpen}
                        />
                    </div>
                </Group>
            </div>
        </MantineProvider>
    );
};

export default SearchesList;
