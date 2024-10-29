import {Button, CloseButton, Text} from '@mantine/core';
import {useListState} from '@mantine/hooks';
import classes from './DndListHandle.module.css';
import React, {useState} from 'react';
import {PlaceTime, SearchDTO} from "../../types/SearchDTO"; // Importuj odpowiednie typy
import {DaysInputs} from '../DaysInputs/DaysInputs';
import {LocationSelector} from '../PlaceAutocompleteInput/LocationSelector'
import { useEffect } from 'react';

// Define a type for the data items
interface Place {
    id: number;
    name: string;
    hours: number;
}

// Initial data
const initialData: Place[] = [
    { id: 1, name: "", hours: 0 },
];

// Component for an individual list item
interface ListItemProps {
    item: Place;
    onRemove: () => void;
    onHoursChange: (id: number, hours: number) => void; // Nowa prop dla zmiany godzin
    onNameChange: (id: number, name: string) => void; // Nowa prop dla zmiany nazwy
}

const ListItem: React.FC<ListItemProps> = ({ item, onRemove, onHoursChange, onNameChange }) => (
    <div className={classes.item}>
        <LocationSelector
            value={item.name} // Pass the current item name as the value
            onChange={(newValue:string) => onNameChange(item.id, newValue)} // Update the item name on change
        />
        <DaysInputs value={item.hours}  onChange={(hours) => onHoursChange(item.id, hours)}></DaysInputs>
        {/* CloseButton do usunięcia elementu */}
        <CloseButton onClick={onRemove} />
    </div>
);

// Component for the starting and ending list item
const ListItemStartEnd: React.FC<{ id: string; item: string; value: string; onNameChange: (name: string) => void; }> = ({ id, item, value, onNameChange }) => (
    <div className={classes.item} id={id}>
        <Text mx={"md"}>{item}</Text>
        <LocationSelector
            value={value} // Pass the current item name as the value
            onChange={(newValue:string) => onNameChange(newValue)} // Update the item name on change
        />
    </div>
);

/*
    Handleable list
 */
interface DndListHandleProps {
    dto: SearchDTO; // Adjust this type according to your actual type definition
    onUpdate: (updatedDto: SearchDTO) => void; // The function to call when the DTO is updated
    placesTimeError: string | null; // Update the type as necessary
    startPlaceError: string | null; // Update the type as necessary
    endPlaceError: string | null; // Update the type as necessary
}

export const DndListHandle: React.FC<DndListHandleProps> = ({ dto, onUpdate, placesTimeError, startPlaceError, endPlaceError, }) => {
    // State do zarządzania listą miejsc
    const [state, handlers] = useListState<Place>(initialData);
    const [startItemName, setStartItemName] = useState(dto.start); // Stan dla nazwy elementu startowego
    const [endItemName, setEndItemName] = useState(dto.end); // Stan dla nazwy elementu końcowego

    useEffect(() => {
        if (dto.placesTime.length === 0) {  // Update dto only if placesTime is empty
            const placesTime = state.map(place => ({
                place: place.name,
                hoursToSpend: place.hours,
            }));
            onUpdate({ ...dto, placesTime, start: startItemName, end: endItemName });
        }
    }, []);

    const addPlace = () => {
        if (state.length < 5) {
            const newId = state.length + 1;
            const updatedState = [
                ...state,
                { id: newId, name: "", hours: 0 },
            ];

            handlers.setState(updatedState);

            // Update dto with new placesTime
            const placesTime = updatedState.map(place => ({
                place: place.name,
                hoursToSpend: place.hours
            }));
            onUpdate({ ...dto, placesTime, start: startItemName, end: endItemName });
        }
    };

    const removePlace = (id: number) => {
        if (state.length > 1) { // Only allow removal if there's more than one item
            const newState = state
                .filter(item => item.id !== id)
                .map((item, index) => ({ ...item, id: (index + 1) })); // Re-index IDs

            handlers.setState(newState);

            // Update dto with re-indexed placesTime
            const placesTime = newState.map(place => ({
                place: place.name,
                hoursToSpend: place.hours
            }));

            onUpdate({ ...dto, placesTime, start: startItemName, end: endItemName });
        }
    };
    // Funkcja do obsługi zmian godzin
    const handleHoursChange = (id: number, hours: number) => {
        const updatedPlaces = state.map(item =>
            item.id === id ? { ...item, hours } : item
        );
        handlers.setState(updatedPlaces);

        // Aktualizuj dto z nowymi wartościami
        const placesTime: PlaceTime[] = updatedPlaces.map(place => ({
            place: place.name,
            hoursToSpend: place.hours // Używamy hours
        }));
        // Aktualizuj dto z nowymi nazwami start i end
        onUpdate({ ...dto, placesTime, start: startItemName, end: endItemName });
    };

    // Funkcja do obsługi zmian nazwy dla elementów listy
    const handleNameChange = (id: number, name: string) => {
        const updatedPlaces = state.map(item =>
            item.id === id ? { ...item, name } : item
        );
        handlers.setState(updatedPlaces);

        const placesTime: PlaceTime[] = updatedPlaces.map(place => ({
            place: place.name,
            hoursToSpend: place.hours
        }));
        onUpdate({ ...dto, placesTime, start: startItemName, end: endItemName });
        // handleHoursChange(id, updatedPlaces.find(item => item.id === id)?.hours || 0); // Upewnij się, że godziny również są zaktualizowane
    };

    // Funkcja do obsługi zmian nazwy dla elementów startowych i końcowych
    const handleStartNameChange = (name: string) => {
        setStartItemName(name);
        onUpdate({ ...dto, placesTime: dto.placesTime, start: name, end: endItemName }); // Aktualizuj dto z nową nazwą start
    };

    const handleEndNameChange = (name: string) => {
        setEndItemName(name);
        onUpdate({ ...dto, placesTime: dto.placesTime, start: startItemName, end: name }); // Aktualizuj dto z nową nazwą end
    };

    return (
        <div>
            <ListItemStartEnd id="start-item" item={"Start"} value={startItemName} onNameChange={handleStartNameChange} />
            {startPlaceError  && <Text color="red" size="sm">{startPlaceError }</Text>}

            {/* Mapowanie przez elementy, aby renderować komponenty ListItem */}
            {state.map((item, index) => (
                <ListItem
                    key={item.id}
                    item={item}
                    onRemove={() => removePlace(item.id)}
                    onHoursChange={handleHoursChange}
                    onNameChange={handleNameChange}
                />
            ))}
            {placesTimeError  && <Text color="red" size="sm">{placesTimeError }</Text>}

            <Button onClick={addPlace} className={classes.pinkButton} disabled={state.length >= 5}>
                Add Place
            </Button>

            <ListItemStartEnd id="end-item" item={"End"} value={endItemName} onNameChange={handleEndNameChange} />
            {endPlaceError && <Text color="red" size="sm">{endPlaceError}</Text>}
        </div>
    );
};
