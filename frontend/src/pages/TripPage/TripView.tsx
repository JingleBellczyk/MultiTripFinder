import React from 'react';
import { Card, Text, Group, Badge, Stack, Divider, MantineProvider, Title } from '@mantine/core';
import { SavedTripDTO, TransferDTO, TripPlaceDTO } from '../../types/TripDTO';
import { Tag } from "../../types/SearchDTO";
import { HeaderSearch } from "../../components/HeaderSearch/HeaderSearch";
import { Footer } from '../../components/Footer/Footer';
import { IconArrowDown } from '@tabler/icons-react';
import styles from './TripView.module.css';
import {useLocation} from "react-router-dom";
import { convertHoursToDays } from '../../utils/convertHoursToDays';

const SavedTripView: React.FC = () => {
    const location = useLocation();
    const trip = location.state?.trip as SavedTripDTO;


    if (!trip) {
        return <>
        <MantineProvider>
            <HeaderSearch />
            <div className={styles.background}>
                <Card shadow="sm" padding="lg" className={styles.card}>
                    <div>Travel not found!</div>
                </Card>
            </div>
            <Footer/>
        </MantineProvider>
        </>
    }

    const { places, transfers } = trip;
    const maxLength = Math.max(places.length, transfers.length);

    const renderPlaceTransfer = () => {
        const placeTransferArray: (TripPlaceDTO | TransferDTO)[] = [];

        // Interleave places and transfers
        for (let i = 0; i < maxLength; i++) {
            if (i < places.length) placeTransferArray.push(places[i]);
            if (i < transfers.length) placeTransferArray.push(transfers[i]);
        }

        return placeTransferArray.map((item, index) => {
            const isPlace = 'country' in item;
            const isFirstPlace = isPlace && index === 0;
            const isLastPlace = isPlace && index === maxLength+1;

            return (
                <div key={index}>
                    {isPlace ? (
                        // Render place
                        <Card shadow="xs" className={styles['card-place']}>
                            <Title><strong>{item.country}, {item.city}</strong></Title>
                            {!isFirstPlace && !isLastPlace && (
                                <>
                                    <Text><strong>Is Transfer?</strong> {item.isTransfer ? 'Yes' : 'No'}</Text>
                                    <Text><strong>Stay Duration:</strong> {convertHoursToDays(item.stayDuration)}</Text>
                                </>
                            )}
                        </Card>
                    ) : (
                        // Render transfer
                        <Card shadow="xs" className={styles['card-transfer']}>
                            <Text><strong>Carrier:</strong> {item.carrier}</Text>
                            <Text><strong>Transport:</strong> {item.transportMode}</Text>
                            <Text><strong>Start Address:</strong> {item.startAddress}</Text>
                            <Text><strong>Start Date & Time:</strong> {item.startDateTime}</Text>
                            <Text><strong>Duration:</strong> {convertHoursToDays(item.duration)}</Text>
                            <Text><strong>End Date & Time:</strong> {item.endDateTime}</Text>
                            <Text><strong>End Address:</strong> {item.endAddress}</Text>
                            <Text><strong>Cost:</strong> ${item.cost}</Text>
                        </Card>
                    )}
                    { index < maxLength+1 && <IconArrowDown className={styles.arrow} /> }
                </div>
            );
        });
    };

    return (
        <MantineProvider>
            <HeaderSearch />
            <div className={styles.background}>
                <Card shadow="sm" padding="lg" className={styles.card}>
                    <Text size="xl" className={styles.title}>{trip.name}</Text>
                    <Text size="sm" color="dimmed" className={styles.date}>Saved on: {trip.saveDate}</Text>

                    <Group className={styles.group}>
                        <Text size="md">Trip Duration: {convertHoursToDays(trip.duration)}</Text>
                        <Text size="md">Passenger Count: {trip.passengerCount}</Text>
                    </Group>

                    <Stack className={styles.tagssection}>
                        <Text size="md"><strong>Tags:</strong></Text>
                        <Group>
                            {trip.tags.map((tag: Tag, index: number) => (
                                <Badge key={index} color="blue" className={styles['tag-badge']}>{tag.name}</Badge>
                            ))}
                        </Group>
                    </Stack>

                    <Divider className={styles.divider} />

                    <Stack className={styles.tripdetails}>
                        <Text size="md"><strong>Trip Start:</strong> {trip.startDate}</Text>
                        <Text size="md"><strong>Trip End:</strong> {trip.endDate}</Text>
                        <Text size="md"><strong>Total Cost:</strong> ${trip.totalCost}</Text>
                        <Text size="md"><strong>Total Transfer Time:</strong> {trip.totalTransferTime} hours</Text>
                    </Stack>

                    <Divider className={styles.divider} />

                    <Stack className={styles.placesTransfersSection}>
                        <Text size="lg"><strong>Places & Transfers:</strong></Text>
                        {renderPlaceTransfer()}
                    </Stack>
                </Card>
            </div>
            <Footer />
        </MantineProvider>
    );
};

export default SavedTripView;
