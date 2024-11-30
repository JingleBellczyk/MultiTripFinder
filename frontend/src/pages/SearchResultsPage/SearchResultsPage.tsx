import React from 'react';
import {Accordion, Box, Group, Text, Title} from '@mantine/core';
import {Place, Transfer, Trip} from "../../types/TripDTO";
import styles from "./SearchResultPage.module.css";
import {formatDateToReadableString, formatTime, formatTimeToDaysAndHours} from "../../utils/formatDateToReadableString";
import {SaveSearchTripModal} from "../../components/SaveSearchTripModal/SaveSearchTripModal";
import {transportIcons, TransportMode} from "../../constants/constants";
import {saveTripToBackend} from "../../api/services/tripService";
import useAuth from "../../hooks/useAuth";

interface AccordionLabelProps {
    trip: Trip;
}

const AccordionLabel: React.FC<AccordionLabelProps> = ({trip}) => {
    const {startDate, endDate, passengerCount, totalCost, totalTransferTime, duration, places} = trip;

    const filteredPlaces = places.filter(place => !place.isTransfer);
    const sortedPlaces = filteredPlaces.sort((a, b) => a.visitOrder - b.visitOrder);
    const placesString = sortedPlaces.map(place => `${place.city}`).join(" → ");

    return (
        <div>
            <Group className={styles.tripInfo}>
                <div className={styles.tripLeft}>
                    <Text className={styles.tripPlaces}>{placesString}</Text> |
                    <Text className={styles.tripDates}>{startDate} - {endDate}</Text>
                </div>
                <div className={styles.tripRight}>
                    <Text className={styles.tripPassengers}>Passengers: {passengerCount}</Text> |
                    <Text className={styles.tripTotalCost}>Total {totalCost}€</Text> |
                    <Text className={styles.tripTransferTime}>Transfer Time: {formatTime(totalTransferTime)}</Text> |
                    <Text className={styles.tripDuration}>Duration: {formatTimeToDaysAndHours(duration)}</Text>
                </div>
            </Group>
        </div>
    );
};

interface AccordionPanelProps {
    places: Place[];
    transfers: Transfer[];
}

const AccordionPanel: React.FC<AccordionPanelProps> = ({ places, transfers }) => {
    const tripDetails: { type: 'place' | 'transfer'; place?: Place; transfer?: Transfer }[] = [];

    let placesIndex = 0;
    let transfersIndex = 0;

    while (placesIndex < places.length || transfersIndex < transfers.length) {
        if (placesIndex < places.length) {
            tripDetails.push({ type: 'place', place: places[placesIndex] });
            placesIndex++;
        }
        if (transfersIndex < transfers.length) {
            tripDetails.push({ type: 'transfer', transfer: transfers[transfersIndex] });
            transfersIndex++;
        }
    }

    return (
        <Box>
            {tripDetails.map((detail, index) => (
                <Group key={index} className={styles.tripRow}>
                    <Group key={index} className={styles.tripRow}>
                        {detail.type === 'place' && detail.place && (
                            <div className={`${styles.place} ${detail.place.isTransfer ? styles.changeText : ''}`}>
                                <Group align="center">
                                    {detail.place.isTransfer && <Text className={styles.change}>Change in:</Text>}
                                    <Text
                                        className={`${styles.placeText} ${detail.place.isTransfer ? styles.changeText : ''}`}>
                                        {detail.place.city}, {detail.place.country}
                                        {detail.place.stayDuration > 0 && ` (${detail.place.stayDuration} days)`} {/* Only show stayDuration if it's greater than 0 */}
                                    </Text>
                                </Group>
                            </div>
                        )}
                    </Group>
                    {detail.type === 'transfer' && detail.transfer && (
                        <div className={styles.transfer}>
                            <Text className={styles.firstInside}>
                                {formatDateToReadableString(detail.transfer.startDateTime)} : {detail.transfer.startAddress}
                            </Text>
                            <Text className={styles.transferInfo}>
                                Transfer time: {detail.transfer.duration} min<br />
                                {/* Dodanie ikony transportu z transportIcons */}
                                {transportIcons[detail.transfer.transportMode as TransportMode]} {detail.transfer.transportMode}: {detail.transfer.carrier}<br />
                                Cost: {detail.transfer.cost}€
                            </Text>
                            <Text className={styles.firstInside}>
                                {formatDateToReadableString(detail.transfer.endDateTime)}: {detail.transfer.endAddress}
                            </Text>
                        </div>
                    )}
                </Group>
            ))}
        </Box>
    );
};


interface SearchResultPageProps {
    trips: Trip[];
}

const SearchResultPage: React.FC<SearchResultPageProps> = ({trips}) => {
    const { isAuthenticated, token, user, loading } = useAuth();

    const items = trips.map((trip, index) => (
        <Box key={trip.startDate + trip.endDate + index} mb="md">
            <Accordion.Item className={styles.accordionRounded} value={trip.startDate + trip.endDate + index}>
                <div className={styles.labelStyle}>
                    <Accordion.Control style={{ flexGrow: 1 }}>
                        <AccordionLabel trip={trip} />
                    </Accordion.Control>
                    {isAuthenticated && user &&(
                        <Box className={styles.saveBox}>
                            <SaveSearchTripModal
                                entityType="trip"
                                onSave={(name) => saveTripToBackend({ ...trip, name },user?.id)}
                            />
                        </Box>
                    )}
                </div>
                <Accordion.Panel>
                    <AccordionPanel places={trip.places} transfers={trip.transfers}/>
                </Accordion.Panel>
            </Accordion.Item>
        </Box>
    ));

    return (
        <div className={styles.background}>
            <Title className={styles.searchHeader} size="h1" ta="center">
                Search results
            </Title>
            <Accordion chevronPosition="right" variant="contained">
                {items}
            </Accordion>
        </div>
    );
};

export default SearchResultPage;