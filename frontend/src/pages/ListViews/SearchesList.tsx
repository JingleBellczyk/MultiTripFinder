import React, {useEffect, useState} from 'react';
import { Button, Group, Transition, MantineProvider } from '@mantine/core';
import SearchFilter from '../../components/Filter/SearchFilter';
import SearchTable from '../../components/Table/SearchTable';
import { HeaderSearch } from "../../components/HeaderSearch/HeaderSearch";
import styles from './List.module.css';
import {SavedSearch, SavedTag} from "../../types/SearchDTO";
import {PAGE_SIZE, SERVER} from "../../constants/constants";
import {Footer} from "../../components/Footer/Footer";
import axios from "axios";
import transformSearches from "../../utils/transformSearches";
import {UserProps} from "../../types/UserDTO";


const SearchesList: React.FC<UserProps> = ({user}) => {
    const [isFilterVisible, setIsFilterVisible] = useState<boolean>(true);
    const [isFullSearch, setIsFullSearch] = useState<boolean>(false);
    const [allTags, setAllTags] = useState<SavedTag[]>([]);
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
    const [searchData, setSearchData] = useState<SavedSearch[]>([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);
    const [selectedName, setSelectedName] = useState<string>('')

    const [filterEndpoint, setFilterEndpoint] = useState("");

    const fetchTags = async () => {
        if (!user?.id) return;
        try {
            const response = await axios.get(`${SERVER}/searchTag/${user.id}`, { withCredentials: true});
            console.log(response);
            setAllTags(response.data);
        }
        catch(error) {
            console.error(error);
        }
    }
    const fetchSearches = async (endpoint: string) => {
        try {
            const response = await axios.get(endpoint, { withCredentials: true });
            setTotalPages(response.data.totalPages);
            const content = response.data.content;
            const visibleSearches = transformSearches(content)
            setSearchData(visibleSearches);
            setSelectedName('');
            console.log(searchData)
        } catch (error) {
            console.error('Error fetching searches:', error);
        }
    };
    useEffect(() => {
        if (!filterEndpoint) {
            const endpoint = `${SERVER}/searchList/${user?.id}?size=${PAGE_SIZE}&page=${currentPage - 1}`;
            fetchSearches(endpoint);

        } else {
            const endpoint = filterEndpoint.replace("page=0", `page=${currentPage - 1}`);
            fetchSearches(endpoint);
        }
        fetchTags();

    }, [user, currentPage])

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
                                    user={user}
                                    tags={allTags}
                                    setCurrentPage={setCurrentPage}
                                    fetchSearches={fetchSearches}
                                    setEndpoint={setFilterEndpoint}
                                />
                            </div>
                        )}
                    </Transition>
                    <div className={styles.tableContainer}>
                        <SearchTable
                            user={user}
                            searches={searchData}
                            setSearches={setSearchData}
                            searchTags={allTags}
                            fetchTags={fetchTags}
                            fetchSearches={fetchSearches}
                            setIsModalOpen={setIsModalOpen}
                            totalPages={totalPages}
                            currentPage={currentPage}
                            setCurrentPage={setCurrentPage}
                            setTotalPages={setTotalPages}
                            selectedName={selectedName}
                            setSelectedName={setSelectedName}
                        />
                    </div>
                </Group>
                <Footer></Footer>
            </div>
        </MantineProvider>
    );
};

export default SearchesList;
