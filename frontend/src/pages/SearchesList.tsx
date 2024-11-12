import React, {useEffect, useState} from 'react';
import { Button, Group, Transition, MantineProvider } from '@mantine/core';
import SearchFilter from '../components/Filter/SearchFilter';
import SearchTable from '../components/Table/SearchTable';
import { HeaderSearch } from "../components/HeaderSearch/HeaderSearch";
import styles from './SearchesList.module.css';
import {SavedSearchDTO, Tag} from "../types/SearchDTO";
import {SERVER, User} from "../constants/constants";
import {Footer} from "../components/Footer/Footer";
import {EXAMPLE_SAVED_SEARCH_1, EXAMPLE_SAVED_SEARCH_2, EXAMPLE_TAGS} from "../constants/searchPostDto";

const exampleElements: SavedSearchDTO[] = [EXAMPLE_SAVED_SEARCH_1, EXAMPLE_SAVED_SEARCH_2];

type UserProps = {
    user: User | null;
}
const SearchesList: React.FC<UserProps> = ({user}) => {
    const [isFilterVisible, setIsFilterVisible] = useState<boolean>(true);
    const [isFullSearch, setIsFullSearch] = useState<boolean>(false);
    const [allTags, setAllTags] = useState<Tag[]>(EXAMPLE_TAGS);
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
    const [searches, setSearches] = useState<SavedSearchDTO[]>(exampleElements); // state for saved searches

    // useEffect(() => {
    //     const fetchSearches = async () => {
    //         try {
    //             const response = await axios.get(`${SERVER}/searches/user/${user.id}`); // Replace with actual endpoint
    //             setSearches(response.data); // Update state with the data received from backend
    //         } catch (error) {
    //             console.error('Error fetching searches:', error);
    //         }
    //     };
    //
    //     fetchSearches();
    // }, []); // Empty array ensures this runs only once after the component mounts

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
                                    searches={searches} // Use the searches data fetched from the backend
                                />
                            </div>
                        )}
                    </Transition>
                    <div className={styles.tableContainer}>
                        <SearchTable
                            searches={searches} // Pass the searches data to the table component
                            tags={allTags}
                            setTags={setAllTags}
                            setIsModalOpen={setIsModalOpen}
                        />
                    </div>
                </Group>
                <Footer></Footer>
            </div>
        </MantineProvider>
    );
};

export default SearchesList;
