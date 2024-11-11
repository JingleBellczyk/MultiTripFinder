import styles from './SearchTable.module.css';
import { Table, CloseButton, Badge, Title, Text, Divider, Pagination, Center, TextInput, ActionIcon} from '@mantine/core';
import {IconCheck, IconPencil, IconSearch, IconTrash } from '@tabler/icons-react';
import CustomTags from '../Tags/CustomTags';
import {PlaceTime, SavedSearchDTO, SearchDTOPost, Tag} from "../../types/SearchDTO";
import { useState } from "react";

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
            case 'train':
                return 'red';
            case 'airplane':
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
    start: string;
    end: string;
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

    const formattedPlaces = [start, ...places.map(p => p.place), end].filter(Boolean).join("->  ");

    return (
        <div onClick={toggleDetails} style={{ cursor: 'pointer' }}>
            {!showDetails && <Text key="places" size="md">{formattedPlaces}</Text>}
            {showDetails && (
                <div>
                    <Text key="start" size="md">{start}</Text>
                    {places.map((place, index) => (
                        <Text key={index} size="md">{place.place} {hoursToDaysAndHours(place.hoursToSpend)}</Text>
                    ))}
                    <Text key={"end"} size="md">{end}</Text>
                </div>
            )}
        </div>
    );
}

type ShowPagesProps = {
    numOfElements: number;
};

function ShowPages({ numOfElements }: ShowPagesProps) {
    const pages = Math.ceil(numOfElements / 10);
    return <Pagination total={pages} />;
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
    searches: SavedSearchDTO[];
    tags: Tag[];
    setTags: React.Dispatch<React.SetStateAction<Tag[]>>;
    setIsModalOpen: (open: boolean) => void;
};

export default function SearchTable({ searches, tags, setTags, setIsModalOpen }: SearchTableProps) {
    const [searchData, setSearchData] = useState(searches);

    const createSearchDTO = (searchIndex: number) => {
        const search = searchData[searchIndex];

        // Construct goalPlacesTime array dynamically
        const goalPlacesTime = [
            { place: search.start, hoursToSpend: 0 },
            ...search.placesTime,
            { place: search.end, hoursToSpend: 0 }
        ];

        // Construct the DTO object
        const dto: SearchDTOPost = {
            places: goalPlacesTime,
            passengersNumber: search.passengers,
            maxHoursToSpend: search.maxTotalTime,
            startDate: search.startDate,
            preferredTransport: search.transport,
            preferredCriteria: search.preferredCriteria
        };

        // Log or pass the DTO to further functions
        console.log(dto); // Or set it in state or use as needed
    };

    const deleteSearch = (index: number) => {
        const updatedSearchData = searchData.filter((_, i) => i !== index);
        setSearchData(updatedSearchData);
    };
    function SearchIcon({ index }: { index: number }) {
        const handleClick = () => {
            createSearchDTO(index);
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

    const addTagToSearch = (searchIndex: number, tagName: string) => {
        // Create a copy of the current searches and tags data
        const updatedSearches = [...searchData];
        const updatedTags = [...tags];
        tagName = tagName.trim().toUpperCase()
        // Create a new tag object
        const newTag = { name: tagName};

        // Check if the tag already exists in the global list of tags
        const tagExistsInAllTags = updatedTags.some((tag) => tag.name === tagName);

        // If the tag doesn't exist in allTags, add it
        if (!tagExistsInAllTags) {
            updatedTags.push(newTag);
            setTags(updatedTags);
        }

        // Check if the tag already exists in the specific search's tags
        const tagExistsInSearchTags = updatedSearches[searchIndex].tags?.some((tag) => tag.name === tagName);

        // If the tag doesn't exist in the search's tags, add it
        if (!tagExistsInSearchTags) {
            updatedSearches[searchIndex].tags = [...(updatedSearches[searchIndex].tags || []), newTag];
            setSearchData(updatedSearches);
        }
    };


    const removeTagFromSearch = (searchIndex: number, tagName: string) => {
        const updatedSearches = [...searchData];
        updatedSearches[searchIndex].tags = updatedSearches[searchIndex].tags?.filter((tag) => tag.name !== tagName) || [];
        setSearchData(updatedSearches);
    };

    const handleGlobalTagRemove = (tagToRemove: Tag) => {
        // Usuwamy tag z globalnej listy tagów
        setTags((prevTags) => prevTags.filter((tag) => tag.name !== tagToRemove.name));

        // Usuwamy tag ze wszystkich zapisanych wyszukiwań
        const updatedSearches = searchData.map((search) => ({
            ...search,
            tags: search.tags?.filter((tag) => tag.name !== tagToRemove.name) || [],
        }));

        // Aktualizujemy stan wyszukiwań
        setSearchData(updatedSearches);
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
                    passengerNum={search.passengers}
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
                <DeleteIcon onClick={() => {deleteSearch(index)}}/>
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
                        <ShowPages numOfElements={searchData.length} />
                    </>
                )}
            </div>
        </div>
    );
}