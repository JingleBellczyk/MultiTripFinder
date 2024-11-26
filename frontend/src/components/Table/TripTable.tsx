import React, {useEffect, useState} from 'react';
import {Badge, Center, CloseButton, Divider, Group, Pagination, Table, Text, Title} from '@mantine/core';
import {IconEye, IconTrash} from '@tabler/icons-react';
import {SavedTripDTO, Place} from '../../types/TripDTO';
import {ICON_SIZE, MAX_TAG_LENGTH, PAGE_SIZE, SERVER, STROKE, TRIP_LIST} from '../../constants/constants';
import axios from 'axios';
import {User} from "../../types/UserDTO";
import {SavedTag} from "../../types/SearchDTO";
import styles from './Table.module.css';
import {useNavigate} from "react-router-dom";
import {convertHoursToDays} from "../../utils/convertHoursToDays";
import NameTextInput from "../TextInput/NameTextInput";
import CustomTags from "../Tags/CustomTags";
import NameInput from "../NameInput/NameInput";
import {getBadgeColor, handleSearchForName} from "../../utils/commonListFunctions";

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
    places: Place[];
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
        <Text size="md"><b>Start date</b>: {trip.startDate.toLocaleDateString()}</Text>
        <Text size="md"><b>End date</b>: {trip.endDate.toLocaleDateString()}</Text>
        <Text size="md"><b>Total cost</b>: {trip.totalCost} Euro</Text>
        <Text size="md"><b>Duration</b>: {convertHoursToDays(trip.duration)}</Text>
        <Text size="md"><b>Total transfer time</b>: {convertHoursToDays(trip.totalTransferTime)}</Text>
    </>
}

function ShowTransport(props: { transports: string[] }) {
    const { transports } = props;
    return (
        <>
            {transports.map((transport) => (
                transport && (
                    <div key={transport} className={styles.badgeStyle}>
                        <Badge variant="light" color={getBadgeColor(transport)}>
                            {transport}
                        </Badge>
                    </div>
                )
            ))}
        </>
    );
}

type TripTableProps = {
    user: User | null;
    trips: SavedTripDTO[];
    setTrips: React.Dispatch<React.SetStateAction<SavedTripDTO[]>>;
    fetchTags: () => Promise<void>;
    fetchTrips: (endpoint: string) => Promise<void>;
    tripTags: SavedTag[];
    setIsModalOpen: (open: boolean) => void;
    totalPages: number;
    currentPage: number;
    setCurrentPage: (page: number) => void;
    setTotalPages: (pages: number) => void;
    selectedName: string;
    setSelectedName: (name: string) => void;
};

export default function TripTable({user, trips, setTrips, tripTags, fetchTags, fetchTrips, setIsModalOpen, totalPages, currentPage, setCurrentPage, setTotalPages, selectedName, setSelectedName}: TripTableProps) {
    const [tripData, setTripData] = useState<SavedTripDTO[]>(trips);
    const [tags, setTags] = useState<SavedTag[]>(tripTags);

    const navigate = useNavigate();

    useEffect(() => {
        setTags(tripTags);
        setTripData(trips);
    }, [tripTags, trips, setCurrentPage, currentPage, setTrips]);

    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };


    const deleteTrip = async (id: number) => {
        try {
            await axios.delete(`${SERVER}/tripList/${user?.id}/${id}`, {withCredentials: true});

            setTrips((prevData) => prevData.filter((data) => data.id !== id));

            if(tripData.length -1 ==0 && currentPage>0){
                setCurrentPage(currentPage-1);
            }
            else {
                const endpoint = `${SERVER}/tripList/${user?.id}?size=${PAGE_SIZE}&page=${currentPage - 1}`
                await fetchTrips(endpoint);
            }
        }
        catch(error) {
            console.error('Error deleting searches:', error);
        }
    };

    const handleViewDetails = (trip: SavedTripDTO) => {
        navigate(`/trip/${trip.id}`, { state: { trip } });
    };

    const updateTripName = async (index: number, newName: string) => {
        if (tripData[index].name === newName) {
            return 200;
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
            const response = await axios.put(`${SERVER}/tripList/${user?.id}/${tripId}`, requestBody, { withCredentials: true });
            if (response.status == 409){
                return 409;
            }
            setTrips(updatedTrips);
            return 200;
        } catch (error) {
            console.error('Errors while updating trip name:', error);
            return 300;
        }
    };

    const addTagToTrip = async (tripIndex: number, tagName: string) => {
        try {
            const tags = [...tripData[tripIndex].tags];
            const tripId = tripData[tripIndex].id;
            const newTag = { name: tagName};
            const allTags = [...tags, newTag];
            const requestTags = allTags.map(tag => tag.name);
            const requestBody = {
                "name": tripData[tripIndex].name,
                "tags": requestTags
            }
            console.log("REQUEST ADD", requestBody);
            await axios.put(`${SERVER}/tripList/${user?.id}/${tripId}`, requestBody, {withCredentials: true});
            setTrips(prevData => {
                return prevData.map((data, i) =>
                    i === tripIndex ? {...data, tags: allTags} : data
                );
            });
            await fetchTags();
        }
        catch(error){
            console.error("Error while adding tag to search:", error);
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
                return prevData.map((data, i) =>
                    i === index ? {...data, tags: newTags} : data
                );
            });
        } catch (error) {
            console.error('Error removing tag from trip:', error);
        }
    };

    const editTagInTrip = async (oldTag: string, newTag: string, index: number) => {
        if (oldTag == newTag) {
            return;
        }
        const tags = [...tripData[index].tags];
        const searchId = tripData[index].id;
        if (newTag.length <= MAX_TAG_LENGTH && !tags.some((tag) => tag.name === newTag)) {
            const updatedTags = tags.map((tag) =>
                tag.name === oldTag ? {...tag, name: newTag} : tag
            );

            const requestTags = updatedTags.map(tag => tag.name);

            const requestBody = {
                "name": tripData[index].name,
                "tags": requestTags
            }

            await axios.put(`${SERVER}/tripList/${user?.id}/${searchId}`, requestBody, {withCredentials: true});
            setTrips(prevData => {
                const updatedData = prevData.map((data, i) =>
                    i === index ? { ...data, tags: updatedTags } : data
                );
                console.log("Updated trip data", updatedData);
                return updatedData;
            });
            await fetchTags();

        }
    };

    const handleGlobalTagRemove = async (tagToRemove: SavedTag) => {
        try {
            const {id} = tagToRemove;
            await axios.delete(`${SERVER}/tripTag/${user?.id}/${id}`, {withCredentials: true});
            setTags((prevTags) => prevTags.filter((tag) => tag.name !== tagToRemove.name));
            const updatedTrips = tripData.map((trip) => ({
                ...trip,
                tags: trip.tags?.filter((tag) => tag.name !== tagToRemove.name) || [],
            }));

            setTrips(updatedTrips);
            await fetchTags();
        }
        catch(error) {
            console.log(error);
        }
    };

    const handleGlobalTagEditing = async (tagToEdit: SavedTag, newTagName: string) => {
        try {
            const tagNameExists = tags.some((tag) => tag.name.toUpperCase() === newTagName);

            if (!tagNameExists) {
                const {id} = tagToEdit;

                const requestBody = {
                    name: newTagName
                };

                await axios.put(`${SERVER}/tripTag/${user?.id}/${id}`, requestBody, {withCredentials: true});

                setTags((prevTags) =>
                    prevTags.map((tag) =>
                        tag.name === tagToEdit.name ? {...tag, name: newTagName} : tag
                    )
                );
                const updatedTrips = tripData.map((trip) => ({
                    ...trip,
                    tags: trip.tags?.map((tag) =>
                        tag.name === tagToEdit.name ? {name: newTagName} : tag
                    ) || [],
                }));

                setTrips(updatedTrips);

                await fetchTags();
            }
        } catch (error) {
            console.error("Error while editing tag globally:", error);
        }
    };

    return (
        <div className={styles.tableContainer}>
            <Group>
                <div className={styles.titleContainer}>
                    <Title>My Travel History</Title>
                    <Text color="gray">Keep track of your trips.</Text>
                </div>
                <NameInput
                    userId={user?.id}
                    value={selectedName}
                    setValue={setSelectedName}
                    data={trips}
                    type={TRIP_LIST}
                    handleSearchClick={() => handleSearchForName(TRIP_LIST, selectedName, setTripData, setTotalPages, setCurrentPage, user)}
                />
            </Group>
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
                                <th className={styles.placesHead}><Text size="lg">Places</Text></th>
                                <th className={styles.tableHead}><Text size="lg">Trip name</Text></th>
                                <th className={styles.tableHead}><Text size="lg">Details</Text></th>
                                <th className={styles.smallHead}><Text size="lg">Transport</Text></th>
                                <th className={styles.tagColumn}><Center><Text size="lg">Tags</Text></Center></th>
                                <th className={styles.smallHead}><Text size="lg">Save date</Text></th>
                                <th className={styles.smallHead}></th>
                                <th className={styles.smallHead}></th>
                            </tr>
                            </thead>
                            <tbody>
                            {tripData.map((trip, index) => (
                                <tr key={trip.id}>
                                    <td className={styles.placesColumn}>
                                        <ShowPlaces places={trip.places} />
                                    </td>
                                    <td className={styles.tableHead}>
                                        <NameTextInput name={trip.name} onNameChange={(newName) => updateTripName(index, newName)}/>
                                    </td>
                                    <td className={styles.tableHead}>
                                        <ShowTripDetails trip={trip}/>
                                    </td>
                                    <td className={styles.smallHead}>
                                        <ShowTransport transports={trip.allTransports}/>
                                    </td>
                                    <td className={styles.tagColumn}>
                                            <CustomTags
                                                myTags={trip.tags || []}
                                                allTags={tags}
                                                onTagRemoveFromGlobalList={handleGlobalTagRemove}
                                                onTagEditGlobalList={handleGlobalTagEditing}
                                                onTagRemove={removeTagFromTrip}
                                                onEditTag={editTagInTrip}
                                                onAddTag={addTagToTrip}
                                                setIsModalOpen={setIsModalOpen}
                                                index={index}/>
                                    </td>
                                    <td className={styles.smallHead}>
                                        <Text size="md">{trip.saveDate.toLocaleDateString()}</Text>
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
                        <Pagination total={totalPages} onChange={handlePageChange} />
                    </>
                    )}
            </div>
        </div>
);
}
