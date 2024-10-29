import { useState } from 'react';

export const useMaxTotalTime = (initialValue: number) => {
    const [maxTotalTime, setMaxTotalTime] = useState<number>(initialValue);

    const handleMaxTotalTimeChange = (value: number | string) => {
        const newMaxTotalTimeChange = typeof value === "number" ? value : parseInt(value, 10) || 0; // Convert to number
        setMaxTotalTime(newMaxTotalTimeChange * 24);
    };

    return { maxTotalTime, handleMaxTotalTimeChange };
};