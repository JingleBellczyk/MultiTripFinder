import { useState } from 'react';

export const usePassengersNumber = (initialValue: number) => {
    const [passengersNumber, setPassengersNumber] = useState<number>(initialValue);

    const handlePassengersNumberChange = (value: number | string) => {
        const numericValue = typeof value === "number" ? value : parseInt(value, 10) || 0;
        setPassengersNumber(numericValue);
    };

    return { passengersNumber, handlePassengersNumberChange };
};
