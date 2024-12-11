import React from 'react';
import {Card, Text, Group, Badge, Stack, Divider, MantineProvider, Title, Container, Space} from '@mantine/core';
import {SavedTripDTO, Transfer, Place} from '../../types/TripDTO';
import { Tag } from "../../types/SearchDTO";
import { HeaderSearch } from "../../components/HeaderSearch/HeaderSearch";
import { Footer } from '../../components/Footer/Footer';
import { IconArrowDown } from '@tabler/icons-react';
import styles from './TripView.module.css';
import {useLocation} from "react-router-dom";
import { convertHoursToDays } from '../../utils/convertHoursToDays';
import busImg from "../../assets/icons/bus.png";
import trainImg from "../../assets/icons/train.png";
import planeImg from "../../assets/icons/plane.png";
import classes from "../../components/Logo/Logo.module.css";
import {formatDuration} from "../../utils/placesTimeUtils";
import {formatTime} from "../../utils/formatDateToReadableString";

export function formatDateTime(
    dateTime: string | Date,
    options?: Intl.DateTimeFormatOptions
): string {
    const date = typeof dateTime === 'string' ? new Date(dateTime) : dateTime;

    const year = date.getUTCFullYear();
    const month = String(date.getUTCMonth() + 1).padStart(2, '0');
    const day = String(date.getUTCDate()).padStart(2, '0');

    const time = new Intl.DateTimeFormat('en-GB', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false,
        timeZone: 'UTC',
        ...options,
    }).format(date);

    return `${day}.${month}.${year} ${time}`;
}

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
        const placeTransferArray: (Place | Transfer)[] = [];

        // Interleave places and transfers
        for (let i = 0; i < maxLength; i++) {
            if (i < places.length) placeTransferArray.push(places[i]);
            if (i < transfers.length) placeTransferArray.push(transfers[i]);
        }

        const length = placeTransferArray.length;

        return placeTransferArray.map((item, index) => {
            const isPlace = 'country' in item;
            const isFirstPlace = isPlace && index === 0;
            const isLastPlace = isPlace && index === length-1;

            return (
                <div key={index}>
                    {isPlace ? (
                        // Render place
                        <Card shadow="xs" className={styles['card-place']}>
                            <Group>
                                <Title>{item.country}, {item.city}</Title>
                                {item.isTransfer ? <Title order={2}> - Transfer city</Title> : <></>}
                            </Group>

                            {!isFirstPlace && !isLastPlace && (
                                <>
                                    <Text><strong>Stay Duration:</strong> {formatDuration(item.stayDuration)}</Text>
                                </>
                            )}
                        </Card>
                    ) : (
                        // Render transfer
                        <Card shadow="xs" className={styles['card-transfer']}>
                            <Group justify="space-between">
                                <Stack>
                                    <Text><strong>Carrier:</strong> {item.carrier}</Text>
                                    <Text><strong>Transport:</strong> {item.transportMode}</Text>
                                    <Text><strong>Start Address:</strong> {item.startAddress}</Text>
                                    <Text><strong>Start Date & Time:</strong> {formatDateTime(item.startDateTime)}</Text>
                                    <Text><strong>Duration:</strong> {formatDuration(item.duration)}</Text>
                                    <Text><strong>End Date & Time:</strong> {formatDateTime(item.endDateTime)}</Text>
                                    <Text><strong>End Address:</strong> {item.endAddress}</Text>
                                    <Text><strong>Cost:</strong> {item.cost.toFixed(2)}€</Text>
                                </Stack>
                                {item.transportMode === 'BUS' && (
                                    <img
                                        src={busImg}
                                        alt="Bus icon"
                                        className={styles.img}
                                    />
                                )}
                                {item.transportMode === 'PLANE' && (
                                    <img
                                        src={planeImg}
                                        alt="Plane icon"
                                        className={styles.img}
                                    />
                                )}
                                {item.transportMode === 'TRAIN' && (
                                    <img
                                        src={trainImg}
                                        alt="Train icon"
                                        className={styles.img}
                                    />
                                )}
                                <Space/>
                            </Group>
                        </Card>
                    )}
                    { index < length-1 && <IconArrowDown className={styles.arrow} /> }
                </div>
            );
        });
    };

    return (
        <MantineProvider>
            <HeaderSearch />
            <div className={styles.background}>
                <Card shadow="sm" padding="lg" className={styles.card}>
                    <Group>
                        <Text size="xl" className={styles.title}>{trip.name}</Text>
                        <Text size="sm" color="dimmed" className={styles.date}>Saved on: {trip.saveDate.toLocaleDateString()}</Text>
                    </Group>
                    <Divider className={styles.divider}/>
                    <Text size="md"><strong>Trip Duration:</strong> {convertHoursToDays(trip.duration)}</Text>
                    <Text size="md"><strong>Passenger Count:</strong> {trip.passengerCount}</Text>
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
                        <Text size="md" key="start"><strong>Trip Start:</strong> {trip.startDate.toLocaleDateString()}</Text>
                        <Text size="md" key="end"><strong>Trip End:</strong> {trip.endDate.toLocaleDateString()}</Text>
                        <Text size="md" key="cost"><strong>Total Cost:</strong> {trip.totalCost.toFixed(2)}€</Text>
                        <Text size="md"><strong>Total Transfer Time:</strong> {formatTime(trip.totalTransferTime)}</Text>
                    </Stack>
                    <Divider className={styles.divider} />
                    <Stack className={styles.placesTransfersSection}>
                        <Text size="lg"><strong>Places & Transfers:</strong></Text>
                        {renderPlaceTransfer()}
                    </Stack>
                </Card>
            </div>
            <Footer />S
        </MantineProvider>
    );
};

export default SavedTripView;
