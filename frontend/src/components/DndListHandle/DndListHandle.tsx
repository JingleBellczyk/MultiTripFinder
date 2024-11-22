import {Button, CloseButton, Text} from '@mantine/core';
import {useListState} from '@mantine/hooks';
import classes from './DndListHandle.module.css';
import React, {useEffect, useMemo, useState} from 'react';
import {PlaceLocation, SearchDTO} from '../../types/SearchDTO';
import {DaysInputs} from '../DaysInputs/DaysInputs';
import {LocationSelector} from '../PlaceAutocompleteInput/LocationSelector';

interface Place extends PlaceLocation {
    id: number;
    hours?: number;
}

interface ListItemProps {
    item: Place;
    onRemove: () => void;
    onHoursChange: (id: number, hours: number) => void;
    onPlaceChange: (id: number, name: string, country: string, city: string) => void;
}

const ListItem: React.FC<ListItemProps> = ({ item, onRemove, onHoursChange, onPlaceChange }) => (
    <div className={classes.item}>
        <LocationSelector
            value={item.name}
            onChange={(name, country, city) => onPlaceChange(item.id, name, country, city)}
            label="Place to visit"
        />
        <DaysInputs value={item.hours || 0} onChange={(hours) => onHoursChange(item.id, hours)} />
        <CloseButton onClick={onRemove} />
    </div>
);

interface StartEndItemProps {
    id: string;
    label: string;
    place: PlaceLocation;
    onPlaceChange: (name: string, country: string, city: string) => void;
}

const StartEndItem: React.FC<StartEndItemProps> = ({ id, label, place, onPlaceChange }) => (
    <div className={classes.item} id={id}>
        <Text fw={500} mx="md">{label}</Text>
        <LocationSelector
            value={place.name}
            onChange={(name, country:string,  city) => onPlaceChange(name, country, city)}
            label=""
        />
    </div>
);

interface DndListHandleProps {
    dto: SearchDTO;
    onUpdate: (updatedDto: SearchDTO) => void;
    placesTimeError?: string | null;
    startPlaceError?: string | null;
    endPlaceError?: string | null;
}

export const DndListHandle: React.FC<DndListHandleProps> = ({
                                                                dto, onUpdate, placesTimeError, startPlaceError, endPlaceError
                                                            }) => {
    const initialData = dto.placesTime.map((place, index) => ({
        id: index + 1,
        name: place.name,
        country: place.country,
        city: place.city,
        hours: place.hoursToSpend ?? 0,
    }));

    const [state, handlers] = useListState<Place>(initialData);
    const [startPlace, setStartPlace] = useState<PlaceLocation>(dto.start);
    const [endPlace, setEndPlace] = useState<PlaceLocation>(dto.end);

    const placesTime = useMemo(() => state.map(({ name,country, city, hours }) => ({
        name,
        country,
        city,
        hoursToSpend: hours ?? 0,
    })), [state]);

    useEffect(() => {
        onUpdate({ ...dto, placesTime, start: startPlace, end: endPlace });
    }, [placesTime, startPlace, endPlace]);

    const addPlace = () => {
        if (state.length < 5) {
            const newId = state.length ? Math.max(...state.map(item => item.id)) + 1 : 1;
            handlers.append({ id: newId, name: "", country: "", city: "", hours: 0 });
        }
    };

    const removePlace = (id: number) => {
        const index = state.findIndex((item) => item.id === id);
        if (index !== -1) {
            handlers.remove(index);
        }
    };

    const handleHoursChange = (id: number, hours: number) => {
        const index = state.findIndex((item) => item.id === id);
        if (index !== -1) {
            handlers.setItem(index, { ...state[index], hours });
        }
    };

    const handlePlaceChange = (id: number, name: string, country:string, city: string) => {
        const index = state.findIndex((item) => item.id === id);
        if (index !== -1) {
            handlers.setItem(index, { ...state[index], name, country, city});
        }
    };

    const handleStartPlaceChange = (name: string, country:string, city: string) => setStartPlace({ name, country, city});
    const handleEndPlaceChange = (name: string, country:string, city: string) => setEndPlace({ name, country, city});

    return (
        <div>
            <StartEndItem id="start-item" label="Start" place={startPlace} onPlaceChange={handleStartPlaceChange} />
            {startPlaceError && <Text color="red" size="sm">{startPlaceError}</Text>}

            {state.map((item) => (
                <ListItem
                    key={item.id}
                    item={item}
                    onRemove={() => removePlace(item.id)}
                    onHoursChange={handleHoursChange}
                    onPlaceChange={handlePlaceChange}
                />
            ))}
            {placesTimeError && <Text color="red" size="sm">{placesTimeError}</Text>}

            <Button onClick={addPlace} className={classes.pinkButton} disabled={state.length >= 5}>
                Add Place
            </Button>

            <StartEndItem id="end-item" label="End" place={endPlace} onPlaceChange={handleEndPlaceChange} />
            {endPlaceError && <Text color="red" size="sm">{endPlaceError}</Text>}
        </div>
    );
}