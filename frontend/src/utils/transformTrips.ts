import {SavedTripDTO, Place, Transfer} from "../types/TripDTO";


const transformTrips = (content: any[]): SavedTripDTO[] => {
    return content.map(trip => {

        const places = trip.places.map((place: any) : Place => ({
            city: place.city,
            country: place.country,
            isTransfer: place.isTransfer,
            stayDuration: place.stayDuration,
            visitOrder: place.visitOrder
        }));

        const transfers = trip.transfers.map((transfer: any) : Transfer => ({
            startDateTime: transfer.startDateTime,
            endDateTime: transfer.endDateTime,
            carrier: transfer.carrier,
            transportMode: transfer.transportMode,
            cost: transfer.cost,
            duration: transfer.duration,
            transferOrder: transfer.transferOrder,
            startAddress: transfer.startAddress,
            endAddress: transfer.endAddress
        }));

        const allTransports: string[] = Array.from(new Set(
            transfers.map((transfer: any) => transfer.transportMode)
        ));

        return {
            id: trip.id,
            name: trip.name,
            saveDate: new Date(trip.saveDate),
            tags: trip.tags.map((tag: string) => ({ name: tag })),
            startDate: new Date(trip.startDate),
            endDate: new Date(trip.endDate),
            passengerCount: trip.passengerCount,
            totalCost: trip.totalCost,
            totalTransferTime: trip.totalTransferTime,
            duration: trip.duration,
            allTransports: allTransports,
            transfers: transfers,
            places: places
        };
    });
};
export default transformTrips