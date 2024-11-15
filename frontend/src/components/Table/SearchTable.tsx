import styles from './SearchTable.module.css';
import { Table, CloseButton, Badge, Title, Text, Divider, Pagination, Center, TextInput, ActionIcon} from '@mantine/core';
import {IconCheck, IconPencil, IconSearch, IconTrash } from '@tabler/icons-react';
import CustomTags from '../Tags/CustomTags';
import {PlaceLocation, PlaceTime, SavedSearch, SavedSearchDTO, SavedTag, SearchDTO, Tag} from "../../types/SearchDTO";
import {useEffect, useState} from "react";
import { useNavigate } from 'react-router-dom';
import {PAGE_SIZE, SERVER, User} from "../../constants/constants";
import axios from "axios";
import transformSearches from "../../hooks/transformSearches";

const iconSize = 24;
const stroke = 1.3;

function DeleteIcon({ onClick }: { onClick: () => void }) {
    return <CloseButton onClick={onClick} icon={<IconTrash size={iconSize} stroke={stroke} />} />;
}

type TransportBadgeProps = {
    transport: string | null;
};

function TransportBadge({ transport }: TransportBadgeProps) {
    const getBadgeColor = (element: string | null) => {
        switch (element) {
            case 'Train':
                return 'red';
            case 'Plane':
                return 'indigo';
            default:
                return 'green';
        }
    };

    return (
        <>
            {transport && (
                <div className={styles.badgeStyle}>
                    <Badge variant="light" color={getBadgeColor(transport)}>
                        {transport}
                    </Badge>
                </div>
            )}
        </>
    );
}

type ShowPlacesProps = {
    places: PlaceTime[];
    start: PlaceLocation;
    end: PlaceLocation;
};


const hoursToDaysAndHours = (hours: number) => {
    const days = Math.floor(hours / 24);
    const remainingHours = hours % 24;
    return <><b>{days} day(s)</b>, <b>{remainingHours}h </b></>;
};

function ShowPlaces({ places, start, end }: ShowPlacesProps) {
    const [showDetails, setShowDetails] = useState(false); // State to control detail visibility

    const toggleDetails = () => {
        setShowDetails(!showDetails);
    };

    const formattedPlaces = [start.city, ...places.map(p => p.city), end.city].filter(Boolean).join("->  ");

    return (
        <div onClick={toggleDetails} style={{ cursor: 'pointer' }}>
            {!showDetails && <Text key="places" size="md">{formattedPlaces}</Text>}
            {showDetails && (
                <div>
                    <Text key="start" size="md">{start.country}, {start.city}</Text>
                    {places.map((place, index) => (
                        <Text key={index} size="md">{place.country}, {place.city} {hoursToDaysAndHours(place.hoursToSpend)}</Text>
                    ))}
                    <Text key={"end"} size="md">{end.country}, {end.city}</Text>
                </div>
            )}
        </div>
    );
}

type ShowDetailsProps = {
    criteria: string | null;
    passengerNum: number;
    name: string | null;
    maxTotalTime: number;
    startDate: Date;
    onNameChange: (newName: string) => void;
};

function ShowDetails({ criteria, passengerNum, maxTotalTime, name, startDate, onNameChange }: ShowDetailsProps) {
    const [isEditing, setIsEditing] = useState(false);
    const [currentName, setCurrentName] = useState(name || '');
    
    const handleEditClick = () => setIsEditing(true);
    const handleSaveClick = () => {
        setIsEditing(false);
        onNameChange(currentName);
    };

    return (
        <>
            {isEditing ? (
                <div style={{ display: 'flex', alignItems: 'center' }}>
                    <TextInput
                        value={currentName}
                        onChange={(e) => setCurrentName(e.currentTarget.value)}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') handleSaveClick();
                        }}
                        autoFocus
                        maxLength={100}
                    />
                    <ActionIcon onClick={handleSaveClick} color="blue" variant="filled" size="sm">
                        <IconCheck/>
                    </ActionIcon>
                </div>
            ) : (
                <div style={{ display: 'flex', alignItems: 'center' }}>
                    <Text size="md" style={{ fontStyle: 'italic', cursor: 'pointer' }} onClick={handleEditClick}>
                        {currentName}
                    </Text>
                    <ActionIcon onClick={handleEditClick} variant="transparent">
                        <IconPencil size={16} />
                    </ActionIcon>
                </div>
            )}
            <Text size="md">Passengers: <b>{passengerNum}</b></Text>
            <Text size="md">Optimization criteria: <b>{criteria}</b></Text>
            <Text size="md">Max total time: {hoursToDaysAndHours(maxTotalTime)}</Text>
            <Text size="md">Start date: <b>{startDate.toLocaleDateString()}</b></Text>
        </>
    );
}

type SearchTableProps = {
    user: User | null;
    //searches: SavedSearchDTO[];
    tags: SavedTag[];
    setTags: React.Dispatch<React.SetStateAction<SavedTag[]>>;
    setIsModalOpen: (open: boolean) => void;
};

export default function SearchTable({ user, tags, setTags, setIsModalOpen }: SearchTableProps) {
    const [searchData, setSearchData] = useState<SavedSearch[]>([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);

    const fetchSearches = async () => {
        try {
            const response = await axios.get(`${SERVER}/searchList/${user?.id}?size=${PAGE_SIZE}&page=${currentPage - 1}`, { withCredentials: true });
            setTotalPages(response.data.totalPages);
            const content = response.data.content;
            const visibleSearches = transformSearches(content)
            setSearchData(visibleSearches);
            console.log(searchData)
        } catch (error) {
            console.error('Error fetching searches:', error);
        }
    };

    useEffect(() => {
        fetchSearches();
    }, [user, currentPage]);


    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };
    const deleteSearch = async (id: number) => {
        try {
            await axios.delete(`${SERVER}/searchList/${user?.id}/${id}`, {withCredentials: true});
            fetchSearches();
        }
        catch(error) {
            console.error('Error deleting searches:', error);
        }
    };

    const createSearchDTO = (index: number): SearchDTO => {
        const search = searchData[index];
        return {
            placesTime: search.placesTime,
            start: search.start,
            end: search.end,
            maxTotalTime: search.maxTotalTime,
            transport: search.transport,
            startDate: search.startDate,
            passengersNumber: search.passengersNumber,
            preferredCriteria: search.preferredCriteria
        };
    };

    function SearchIcon({ index }: { index: number }) {
        const navigate = useNavigate();

        const handleClick = () => {
            const data = createSearchDTO(index);
            navigate('/search', { state: data });
        };

        return (
            <CloseButton onClick={handleClick} icon={<IconSearch size={iconSize} stroke={stroke} />} />
        );
    }
    const updateName = (index: number, newName: string) => {
        const updatedSearches = [...searchData];
        updatedSearches[index].name = newName;
        setSearchData(updatedSearches);
    };

    const addTagToSearch = async (searchIndex: number, tagName: string) => {
        try {
            const tags = [...searchData[searchIndex].tags];
            const searchId = searchData[searchIndex].id;
            tagName = tagName.trim().toUpperCase()
            const newTag = { name: tagName};
            const allTags = [...tags, newTag];
            const requestTags = allTags.map(tag => tag.name);
            const requestBody = {
                "name": searchData[searchIndex].name,
                "tags": requestTags
            }
            console.log(requestBody)
            await axios.put(`${SERVER}/searchList/${user?.id}/${searchId}`, requestBody, {withCredentials: true});
            setSearchData(prevData => {
                const updatedData = [...prevData];
                updatedData[searchIndex] = {
                    ...updatedData[searchIndex],
                    tags: allTags,
                };
                return updatedData;
            });
        }
        catch(error){
            console.error("Error while adding tag to search:", error);
        }
    };

    const removeTagFromSearch = async (index: number, tagName: string) => {
        try {
            const tags = [...searchData[index].tags];
            console.log(tags);
            console.log(tagName);
            const searchId = searchData[index].id;
            if (tags.some(tag => tag.name == tagName)) {
                const newTags = tags.filter(tag => tag.name !== tagName);
                const requestTags = newTags.map(tag => tag.name);
                const requestBody = {
                    "name": searchData[index].name,
                    "tags": requestTags
                }
                console.log(requestBody)
                await axios.put(`${SERVER}/searchList/${user?.id}/${searchId}`, requestBody, {withCredentials: true});
                setSearchData(prevData => {
                    const updatedData = [...prevData];
                    updatedData[index] = {
                        ...updatedData[index],
                        tags: newTags,
                    };
                    return updatedData;
                });
            }
        }
        catch (error){
            console.error("Error when removing tag from search:", error);
        }
    };

    const handleGlobalTagRemove = async (tagToRemove: SavedTag) => {
        try {
            const {id, name} = tagToRemove;
            await axios.delete(`${SERVER}/searchTag/${user?.id}/${id}`, {withCredentials: true});
            setTags((prevTags) => prevTags.filter((tag) => tag.name !== tagToRemove.name));
            const updatedSearches = searchData.map((search) => ({
                ...search,
                tags: search.tags?.filter((tag) => tag.name !== tagToRemove.name) || [],
            }));

            setSearchData(updatedSearches);
        }
        catch(error) {
            console.log(error);
        }
    };

    const handleGlobalEditTag = (oldTagName: string, newTagName: string) => {
        // Check if the new tag name already exists in the global tags list
        const tagExistsInAllTags = tags.some((tag) => tag.name === newTagName);

        // If the new tag doesn't exist, update the global tags list
        if (!tagExistsInAllTags) {
            setTags((prevTags) =>
                prevTags.map((tag) =>
                    tag.name === oldTagName ? { ...tag, name: newTagName } : tag
                )
            );
        }

        // Update the tags in all searches where the old tag name exists
        const updatedSearches = searchData.map((search) => ({
            ...search,
            tags: search.tags?.map((tag) =>
                tag.name === oldTagName ? { name: newTagName } : tag
            ) || [],
        }));

        // Update the search data state
        setSearchData(updatedSearches);
    };


    const rows = searchData.map((search, index) => (
        <tr key={index}>
            <td>
                <ShowPlaces start={search.start} end={search.end} places={search.placesTime} />
            </td>
            <td className={styles.tableHead}>
                <ShowDetails
                    criteria={search.preferredCriteria}
                    passengerNum={search.passengersNumber}
                    maxTotalTime={search.maxTotalTime}
                    name={search.name}
                    startDate={search.startDate}
                    onNameChange={(newName) => updateName(index, newName)}
                />
            </td>
            <td className={styles.transportColumn}>
                <TransportBadge transport={search.transport} />
            </td>
            <td className={styles.tagsColumn}>
                <CustomTags
                    myTags={search.tags || []}
                    allTags={tags}
                    onTagRemoveFromGlobalList={handleGlobalTagRemove}
                    onTagEditGlobalList={handleGlobalEditTag}
                    onTagRemoveFromSearch={removeTagFromSearch}
                    onAddTag={addTagToSearch}
                    setIsModalOpen={setIsModalOpen}
                    index={index}/>
            </td>
            <td className={styles.smallHead}>
                <Text size="md">{search.saveDate.toLocaleDateString()}</Text>
            </td>
            <td className={styles.smallHead}>
                <SearchIcon index={index}/>
            </td>
            <td className={styles.smallHead}>
                <DeleteIcon onClick={() => {deleteSearch(search.id)}}/>
            </td>
        </tr>
    ));

    return (
        <div className={styles.tableContainer}>
            <div className={styles.titleContainer}>
                <Title>My Search History</Title>
                <Text color="gray">Keep track of your searches.</Text>
            </div>
            <Divider />
            <div>
                {searchData.length === 0 ? (
                    <Center>
                        <Text size="xl">No saved searches available.</Text>
                    </Center>
                ) : (
                    <>
                        <Table highlightOnHover className={styles.table}>
                            <thead>
                            <tr>
                                <th><Text size="lg">Places</Text></th>
                                <th className={styles.tableHead}><Text size="lg">Details</Text></th>
                                <th className={styles.transportColumn}><Text size="lg">Transport</Text></th>
                                <th className={styles.tagColumn}><Center><Text size="lg">Tags</Text></Center></th>
                                <th className={styles.smallHead}><Text size="lg">Date of Search</Text></th>
                                <th className={styles.smallHead}></th>
                                <th className={styles.smallHead}></th>
                            </tr>
                            </thead>
                            <tbody>
                            {rows}
                            </tbody>
                        </Table>
                        <Pagination total={totalPages} onChange={handlePageChange} />
                    </>
                )}
            </div>
        </div>
    );
}