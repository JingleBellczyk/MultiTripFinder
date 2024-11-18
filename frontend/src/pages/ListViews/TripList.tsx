import React, { useEffect, useState } from 'react';
import { Button, Group, Transition, MantineProvider } from '@mantine/core';
import TripTable from '../../components/Table/TripTable'; // Adjust this component if needed
import { HeaderSearch } from "../../components/HeaderSearch/HeaderSearch";
import styles from './List.module.css';
import { PAGE_SIZE, SERVER } from "../../constants/constants";
import { Footer } from "../../components/Footer/Footer";
import axios from "axios";
// import transformTrips from "../../hooks/transformTrips"; // Adjust if needed
import { UserProps } from "../../types/UserDTO";
import { SavedTag } from '../../types/SearchDTO';
import {sampleTrip, SavedTripDTO} from "../../types/TripDTO";
import TripFilter from "../../components/Filter/TripFilter";

class SavedTrip {
}

const TripList: React.FC<UserProps> = ({ user }) => {
    const [isFilterVisible, setIsFilterVisible] = useState<boolean>(true);
    const [isFullTrip, setIsFullTrip] = useState<boolean>(false);
    const [allTags, setAllTags] = useState<SavedTag[]>([]);
    const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
    const [tripData, setTripData] = useState<SavedTripDTO[]>([sampleTrip]); // Change from searchData to tripData
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);


    // const fetchTripsListEndpoint = `${SERVER}/tripList/${user?.id}?size=${PAGE_SIZE}&page=${currentPage - 1}`; // Adjust endpoint to fetch trips
    const fetchTags = async () => {
        if (!user?.id) return;
        try {
            const response = await axios.get(`${SERVER}/tripTag/${user.id}`, { withCredentials: true }); // Adjust endpoint for trips
            console.log(response);
            setAllTags(response.data);
        }
        catch (error) {
            console.error(error);
        }
    };

    const fetchTrips = async (endpoint: string) => {
        try {
            const response = await axios.get(endpoint, { withCredentials: true });
            setTotalPages(response.data.totalPages);
            const content = response.data.content;
            //const visibleTrips = transformTrips(content);
            //setTripData(visibleTrips);
            console.log(tripData);
        } catch (error) {
            console.error('Error fetching trips:', error);
        }
    };

    useEffect(() => {
        // fetchTags();
        // fetchTrips(fetchTripsListEndpoint);
    }, [user, currentPage]);

    const toggleFilter = () => {
        setIsFilterVisible(!isFilterVisible);
        setIsFullTrip(!isFullTrip);
    };

    return (
        <MantineProvider theme={{ primaryColor: 'blue' }}>
            <div className={styles.background}>
                <HeaderSearch />
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
                                <TripFilter
                                    user={user}
                                    tags={allTags}
                                    fetchTrips={fetchTrips}
                                />
                            </div>
                        )}
                    </Transition>
                    <div className={styles.tableContainer}>
                        <TripTable
                            user={user}
                            trips={tripData}
                            setTrips={setTripData}
                            tripTags={allTags}
                            fetchTags={fetchTags}
                            setIsModalOpen={setIsModalOpen}
                            totalPages={totalPages}
                            setCurrentPage={setCurrentPage}
                        />
                    </div>
                </Group>
                <Footer />
            </div>
        </MantineProvider>
    );
};

export default TripList;