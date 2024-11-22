import styles from './Table.module.css';
import { Table, CloseButton, Badge, Title, Text, Divider, Pagination, Center, Group} from '@mantine/core';
import {IconSearch, IconTrash } from '@tabler/icons-react';
import CustomTags from '../Tags/CustomTags';
import {PlaceLocation, PlaceTime, SavedSearch, SavedTag, SearchDTO} from "../../types/SearchDTO";
import {useEffect, useRef, useState} from "react";
import { useNavigate } from 'react-router-dom';
import {ICON_SIZE, MAX_TAG_LENGTH, PAGE_SIZE, SEARCH_LIST, SERVER, STROKE} from "../../constants/constants";
import axios, {AxiosError} from "axios";
import {User} from "../../types/UserDTO";
import NameTextInput from "../TextInput/NameTextInput";
import {convertHoursToDays} from "../../utils/convertHoursToDays";
import NameInput from "../NameInput/NameInput";
import {getBadgeColor, handleSearchForName} from "../../utils/commonListFunctions";
function DeleteIcon({ onClick }: { onClick: () => void }) {
    return <CloseButton onClick={onClick} icon={<IconTrash size={ICON_SIZE} stroke={STROKE} />} />;
}

type TransportBadgeProps = {
    transport: string | null;
};

function TransportBadge({ transport }: TransportBadgeProps) {
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

function ShowPlaces({ places, start, end }: ShowPlacesProps) {
    const [showDetails, setShowDetails] = useState(false);
    const containerRef = useRef<HTMLDivElement>(null);

    // Toggle the visibility of the details
    const toggleDetails = (e: React.MouseEvent) => {
        e.stopPropagation(); // Prevent click from triggering outside click handler
        setShowDetails((prev) => !prev);
    };

    // Close the details when clicking outside
    const handleClickOutside = (event: MouseEvent) => {
        if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
            setShowDetails(false);
        }
    };

    // Add event listener for outside click
    useEffect(() => {
        document.addEventListener('click', handleClickOutside);
        return () => {
            document.removeEventListener('click', handleClickOutside);
        };
    }, []);

    const formattedPlaces = [start.city, ...places.map((p) => p.city), end.city]
        .filter(Boolean)
        .join(' -> ');

    return (
        <div
            ref={containerRef}
            onClick={toggleDetails}
            style={{ cursor: 'pointer' }}
        >
            {!showDetails && (
                <Text key="places" size="md">
                    {formattedPlaces}
                </Text>
            )}
            {showDetails && (
                <div>
                    <Text key="start" size="md">
                        <b>
                            {start.country}, {start.city}
                        </b>
                    </Text>
                    {places.map((place, index) => (
                        <Text key={index} size="md">
                            <b>
                                {place.country}, {place.city}
                            </b>
                            : {convertHoursToDays(place.hoursToSpend)}
                        </Text>
                    ))}
                    <Text key="end" size="md">
                        <b>
                            {end.country}, {end.city}
                        </b>
                    </Text>
                </div>
            )}
        </div>
    );
}

type ShowDetailsProps = {
    criteria: string | null;
    passengerNum: number;
    maxTotalTime: number;
    startDate: Date;
};

function ShowDetails({ criteria, passengerNum, maxTotalTime, startDate }: ShowDetailsProps) {
    return (<>
            <Text size="md"><b>Passengers</b>: {passengerNum}</Text>
            <Text size="md"><b>Criteria</b>: {criteria}</Text>
            <Text size="md"><b>Max total time</b>: {convertHoursToDays(maxTotalTime)}</Text>
            <Text size="md"><b>Start date</b>: {startDate.toLocaleDateString()}</Text>
        </>
    );
}

type SearchTableProps = {
    user: User | null;
    searches: SavedSearch[];
    setSearches: React.Dispatch<React.SetStateAction<SavedSearch[]>>
    fetchTags: () => Promise<void>;
    fetchSearches: (endpoint: string) => Promise<void>;
    searchTags: SavedTag[];
    setIsModalOpen: (open: boolean) => void;
    totalPages: number;
    currentPage: number;
    setCurrentPage: (page: number) => void;
    setTotalPages: (pages: number) => void;
    selectedName: string;
    setSelectedName: (name: string) => void;
};

export default function SearchTable({
                                        user,
                                        searches,
                                        setSearches,
                                        searchTags,
                                        fetchTags,
                                        fetchSearches,
                                        setIsModalOpen,
                                        totalPages,
                                        setTotalPages,
                                        currentPage,
                                        setCurrentPage,
                                        selectedName,
                                        setSelectedName
                                    }: SearchTableProps) {
    const [searchData, setSearchData] = useState<SavedSearch[]>(searches);
    const [tags, setTags] = useState<SavedTag[]>(searchTags);

    useEffect(() => {
        setTags(searchTags);
        setSearchData(searches);
    }, [searchTags, searches, setSearches, setCurrentPage, currentPage]);

    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };
    const deleteSearch = async (id: number) => {
        try {
            await axios.delete(`${SERVER}/searchList/${user?.id}/${id}`, {withCredentials: true});

            setSearches((prevData) => prevData.filter((data) => data.id !== id));

            if(searchData.length -1 ==0 && currentPage>0){
                setCurrentPage(currentPage-1);
            }
            else {
                const endpoint = `${SERVER}/searchList/${user?.id}?size=${PAGE_SIZE}&page=${currentPage - 1}`
                await fetchSearches(endpoint);
            }
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
            <CloseButton onClick={handleClick} icon={<IconSearch size={ICON_SIZE} stroke={STROKE} />} />
        );
    }
    const updateName = async (index: number, newName: string) => {
        try {
            const cleanName = newName.trim().replace(/\s+/g, ' ');
            const tags = [...searchData[index].tags];
            const searchId = searchData[index].id;
            const updatedSearches = [...searchData];
            updatedSearches[index].name = cleanName;
            const requestTags = tags.map(tag => tag.name);
            const requestBody = {
                "name": cleanName,
                "tags": requestTags
            }
            const response = await axios.put(`${SERVER}/searchList/${user?.id}/${searchId}`, requestBody, {withCredentials: true});
            setSearches(updatedSearches);
            return 200;
        }
        catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                console.error("Errors while updating search name:", error.response.data);
                return error.response.status;
            } else {
                // Handle other unexpected errors
                console.error("Unexpected error:", error);
                return 500;
            }
        }
    };

    const addTagToSearch = async (searchIndex: number, tagName: string) => {
        try {
            const tags = [...searchData[searchIndex].tags];
            const searchId = searchData[searchIndex].id;
            const newTag = { name: tagName};
            const allTags = [...tags, newTag];
            const requestTags = allTags.map(tag => tag.name);
            const requestBody = {
                "name": searchData[searchIndex].name,
                "tags": requestTags
            }
            console.log("REQUEST ADD", requestBody);
            await axios.put(`${SERVER}/searchList/${user?.id}/${searchId}`, requestBody, {withCredentials: true});
            setSearches(prevData => {
                return prevData.map((data, i) =>
                    i === searchIndex ? {...data, tags: allTags} : data
                );
            });
            await fetchTags();
        }
        catch(error){
            console.error("Error while adding tag to search:", error);
        }
    };

    const removeTagFromSearch = async (index: number, tagName: string) => {
        try {
            const tags = [...searchData[index].tags];
            const searchId = searchData[index].id;
            if (tags.some(tag => tag.name == tagName)) {
                const newTags = tags.filter(tag => tag.name !== tagName);
                const requestTags = newTags.map(tag => tag.name);
                const requestBody = {
                    "name": searchData[index].name,
                    "tags": requestTags
                }
                await axios.put(`${SERVER}/searchList/${user?.id}/${searchId}`, requestBody, {withCredentials: true});
                setSearches(prevData => {
                    return prevData.map((data, i) =>
                        i === index ? {...data, tags: newTags} : data
                    );
                });
            }
        }
        catch (error){
            console.error("Error when removing tag from search:", error);
        }
    };

    const editTagInSearch = async (oldTag: string, newTag: string, index: number) => {
        if (oldTag == newTag) {
            return;
        }
        const tags = [...searchData[index].tags];
        const searchId = searchData[index].id;
        if (newTag.length <= MAX_TAG_LENGTH && !tags.some((tag) => tag.name === newTag)) {
            const updatedTags = tags.map((tag) =>
                tag.name === oldTag ? {...tag, name: newTag} : tag
            );

            const requestTags = updatedTags.map(tag => tag.name);

            const requestBody = {
                "name": searchData[index].name,
                "tags": requestTags
            }

            await axios.put(`${SERVER}/searchList/${user?.id}/${searchId}`, requestBody, {withCredentials: true});
            setSearches(prevData => {
                const updatedData = prevData.map((data, i) =>
                    i === index ? { ...data, tags: updatedTags } : data
                );
                console.log("Updated search data", updatedData);
                return updatedData;
            });
            await fetchTags();

        }
    };

    const handleGlobalTagRemove = async (tagToRemove: SavedTag) => {
        try {
            const {id} = tagToRemove;
            await axios.delete(`${SERVER}/searchTag/${user?.id}/${id}`, {withCredentials: true});
            setTags((prevTags) => prevTags.filter((tag) => tag.name !== tagToRemove.name));
            const updatedSearches = searchData.map((search) => ({
                ...search,
                tags: search.tags?.filter((tag) => tag.name !== tagToRemove.name) || [],
            }));

            setSearches(updatedSearches);
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

                await axios.put(`${SERVER}/searchTag/${user?.id}/${id}`, requestBody, {withCredentials: true});

                setTags((prevTags) =>
                    prevTags.map((tag) =>
                        tag.name === tagToEdit.name ? {...tag, name: newTagName} : tag
                    )
                );
                const updatedSearches = searchData.map((search) => ({
                    ...search,
                    tags: search.tags?.map((tag) =>
                        tag.name === tagToEdit.name ? {name: newTagName} : tag
                    ) || [],
                }));

                setSearches(updatedSearches);

                await fetchTags();
            }
        } catch (error) {
            console.error("Error while editing tag globally:", error);
        }
    };


    const rows = searchData.map((search, index) => (
        <tr key={index}>
            <td className={styles.placesColumn}>
                <ShowPlaces start={search.start} end={search.end} places={search.placesTime} />
            </td>
            <td className={styles.tableHead}>
                <NameTextInput name={search.name} onNameChange={(newName) => updateName(index, newName)}/>
            </td>
            <td className={styles.tableHead}>
                <ShowDetails
                    criteria={search.preferredCriteria}
                    passengerNum={search.passengersNumber}
                    maxTotalTime={search.maxTotalTime}
                    startDate={search.startDate}
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
                    onTagEditGlobalList={handleGlobalTagEditing}
                    onTagRemove={removeTagFromSearch}
                    onEditTag={editTagInSearch}
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
            <Group>
                <div className={styles.titleContainer}>
                    <Title>My Search History</Title>
                    <Text color="gray">Keep track of your searches.</Text>
                </div>
                <NameInput
                    userId={user?.id}
                    value={selectedName}
                    setValue={setSelectedName}
                    data={searches}
                    type={SEARCH_LIST}
                    handleSearchClick={() => handleSearchForName(SEARCH_LIST, selectedName, setSearchData, setTotalPages, setCurrentPage, user)}
                />
            </Group>

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
                                <th className={styles.placesHead}><Text size="lg">Places</Text></th>
                                <th className={styles.tableHead}><Text size="lg">Search name</Text></th>
                                <th className={styles.tableHead}><Text size="lg">Details</Text></th>
                                <th className={styles.smallHead}><Text size="lg">Transport</Text></th>
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
                        <Pagination total={totalPages} value={currentPage} onChange={handlePageChange} />
                    </>
                )}
            </div>
        </div>
    );
}