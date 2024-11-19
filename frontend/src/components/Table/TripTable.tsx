import React, { useState, useEffect } from 'react';
import {Table, CloseButton, Pagination, Center, Title, Divider, Text, TextInput, ActionIcon} from '@mantine/core';
import {IconCheck, IconEye, IconPencil, IconTrash} from '@tabler/icons-react';
import { SavedTripDTO, TripPlaceDTO} from '../../types/TripDTO';
import {ICON_SIZE, SERVER, STROKE} from '../../constants/constants';
import axios from 'axios';
import {User} from "../../types/UserDTO";
import {SavedTag} from "../../types/SearchDTO";
import styles from './Table.module.css';
import {useNavigate} from "react-router-dom";
import {convertHoursToDays} from "../../utils/convertHoursToDays";
import NameTextInput from "../TextInput/NameTextInput";

const ViewIcon = ({ onClick }: { onClick: () => void }) => (
    <CloseButton
        onClick={onClick}
        icon={<IconEye size={ICON_SIZE} stroke={STROKE} />}
    />
);

const DeleteIcon = ({ onClick }: { onClick: () => void }) => (
    <CloseButton onClick={onClick} icon={<IconTrash size={ICON_SIZE} stroke={STROKE} />} />
);

type PlacesProps = {
    places: TripPlaceDTO[];
}
const ShowPlaces = ({places} : PlacesProps )=> {

    const formattedPlaces = places.map(place => place.city).filter(Boolean).join(' -> ');

    return <Text size="md">{formattedPlaces}</Text>;
};

type DetailsProps = {
    trip: SavedTripDTO
}

const ShowTripDetails = ({trip} : DetailsProps) => {

    return <>
        <Text size="md"><b>Passengers</b>: {trip.passengerCount}</Text>
        <Text size="md"><b>Start date</b>: {trip.startDate}</Text>
        <Text size="md"><b>End date</b>: {trip.endDate}</Text>
        <Text size="md"><b>Total cost</b>: {trip.totalCost} Euro</Text>
        <Text size="md"><b>Duration</b>: {convertHoursToDays(trip.duration)}</Text>
        <Text size="md"><b>Total transfer time</b>: {convertHoursToDays(trip.totalTransferTime)}</Text>
    </>
}

type TripTableProps = {
    user: User | null;
    trips: SavedTripDTO[];
    setTrips: React.Dispatch<React.SetStateAction<SavedTripDTO[]>>;
    fetchTags: () => Promise<void>;
    tripTags: SavedTag[];
    setIsModalOpen: (open: boolean) => void;
    totalPages: number;
    setCurrentPage: (page: number) => void;
};

export default function TripTable({
                                       user,
                                       trips,
                                       setTrips,
                                       tripTags,
                                       fetchTags,
                                       setIsModalOpen,
                                       totalPages,
                                       setCurrentPage,
                                   }: TripTableProps) {
    const [tripData, setTripData] = useState<SavedTripDTO[]>(trips);
    const [tags, setTags] = useState<SavedTag[]>(tripTags);

    const navigate = useNavigate();

    useEffect(() => {
        setTags(tripTags);
        setTripData(trips);
    }, [tripTags, trips]);

    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };

    const deleteTrip = async (id: number) => {
        try {
            await axios.delete(`${SERVER}/tripList/${user?.id}/${id}`, { withCredentials: true });
        } catch (error) {
            console.error('Error deleting trip:', error);
        }
    };

    const handleViewDetails = (trip: SavedTripDTO) => {
        navigate(`/trip/${trip.id}`, { state: { trip } });
    };

    const updateTripName = async (index: number, newName: string) => {
        if (tripData[index].name === newName) {
            return;
        }
        try {
            const tags = [...tripData[index].tags];
            const tripId = tripData[index].id;
            const updatedTrips = [...tripData];
            updatedTrips[index].name = newName;
            const requestBody = {
                name: newName,
                tags: tags.map((tag) => tag.name),
            };
            await axios.put(`${SERVER}/tripList/${user?.id}/${tripId}`, requestBody, { withCredentials: true });
            setTrips(updatedTrips);
        } catch (error) {
            console.error('Errors while updating trip name:', error);
        }
    };

    const addTagToTrip = async (tripIndex: number, tagName: string) => {
        try {
            const tags = [...tripData[tripIndex].tags];
            tagName = tagName.trim().toUpperCase();
            const newTag = { name: tagName };
            const allTags = [...tags, newTag];
            const requestTags = allTags.map((tag) => tag.name);
            const requestBody = {
                name: tripData[tripIndex].name,
                tags: requestTags,
            };
            await axios.put(`${SERVER}/tripList/${user?.id}/${tripData[tripIndex].id}`, requestBody, { withCredentials: true });
            setTrips((prevData) => {
                const updatedData = prevData.map((data, i) =>
                    i === tripIndex ? { ...data, tags: allTags } : data
                );
                return updatedData;
            });
            await fetchTags();
        } catch (error) {
            console.error('Error while adding tag to trip:', error);
        }
    };

    const removeTagFromTrip = async (index: number, tagName: string) => {
        try {
            const tags = [...tripData[index].tags];
            const tripId = tripData[index].id;
            const newTags = tags.filter((tag) => tag.name !== tagName);
            const requestTags = newTags.map((tag) => tag.name);
            const requestBody = {
                name: tripData[index].name,
                tags: requestTags,
            };
            await axios.put(`${SERVER}/tripList/${user?.id}/${tripId}`, requestBody, { withCredentials: true });
            setTrips((prevData) => {
                const updatedData = prevData.map((data, i) =>
                    i === index ? { ...data, tags: newTags } : data
                );
                return updatedData;
            });
        } catch (error) {
            console.error('Error removing tag from trip:', error);
        }
    };

    return (
        <div className={styles.tableContainer}>
            <div className={styles.titleContainer}>
                <Title>My Travels</Title>
                <Text>Keep track of your trips.</Text>
            </div>
            <Divider />
            <div>
                {tripData.length === 0 ? (
                    <Center>
                        <Text size="lg">No saved trips available.</Text>
                    </Center>
                ) : (
                    <>
                        <Table highlightOnHover className={styles.table}>
                            <thead>
                            <tr>
                                <th><Text size="lg">Places</Text></th>
                                <th><Text size="lg">Trip name</Text></th>
                                <th><Text size="lg">Details</Text></th>
                                <th><Text size="lg">Tags</Text></th>
                                <th><Text size="lg">Save date</Text></th>
                                <th className={styles.smallHead}></th>
                                <th className={styles.smallHead}></th>
                            </tr>
                            </thead>
                            <tbody>
                            {tripData.map((trip, index) => (
                                <tr key={trip.id}>
                                    <td>
                                        <ShowPlaces places={trip.places} />
                                    </td>
                                    <td><NameTextInput name={trip.name} onNameChange={(newName) => updateTripName(index, newName)}/></td>
                                    <td>
                                        <ShowTripDetails trip={trip}/>
                                    </td>
                                    <td>
                                            {/*<CustomTags*/}
                                            {/*    myTags={trip.tags || []}*/}
                                            {/*    allTags={tags}*/}
                                            {/*    onTagRemoveFromGlobalList={handleGlobalTagRemove}*/}
                                            {/*    onTagEditGlobalList={handleGlobalTagEditing}*/}
                                            {/*    onTagRemove={removeTagFromTrip}*/}
                                            {/*    onEditTag={editTagInTrip}*/}
                                            {/*    onAddTag={addTagToTrip}*/}
                                            {/*    setIsModalOpen={setIsModalOpen}*/}
                                            {/*    index={index}/>*/}
                                    </td>
                                    <td>
                                        <Text size="md">{trip.saveDate}</Text>
                                    </td>
                                    <td className={styles.smallHead}>
                                        <ViewIcon onClick={() => handleViewDetails(trip)} />
                                    </td>
                                    <td className={styles.smallHead}>
                                        <DeleteIcon onClick={() => deleteTrip(trip.id)} />
                                    </td>
                                </tr>
                                ))}
                            </tbody>
                        </Table>
                        <Center>
                            <Pagination total={totalPages} onChange={handlePageChange} />
                        </Center>
                    </>
                    )}
            </div>
        </div>
);
}



