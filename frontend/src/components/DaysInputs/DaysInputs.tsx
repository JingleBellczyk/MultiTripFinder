import classes from "./DaysInputs.module.css";
import { Box, NumberInput } from '@mantine/core';
import { useState, useEffect } from "react";
interface DaysInputsProps {
    value: number; // Initial value in hours
    onChange: (value: number) => void; // Callback function with total hours
}
export function DaysInputs({ value, onChange }: DaysInputsProps) {

    // Independent state for days and hours
    const [daysValue, setDaysValue] = useState(0);
    const [hoursValue, setHoursValue] = useState(0);

    // Update total hours whenever days or hours change
    const handleDaysChange = (val: string | number) => {
        const newDaysValue = typeof val === "number" ? val : parseInt(val, 10) || 0; // Convert to number
        setDaysValue(newDaysValue);
        onChange(newDaysValue * 24 + hoursValue); // Call onChange with updated total hours
    };

    const handleHoursChange = (val: string | number) => {
        const newHoursValue = typeof val === "number" ? val : parseInt(val, 10) || 0; // Convert to number
        setHoursValue(newHoursValue);
        onChange(daysValue * 24 + newHoursValue); // Call onChange with updated total hours
    };

    return (
        <Box className={classes.boxStyle}>
            <NumberInput
                size="md"
                min={0}
                max={365}
                label="Days"
                value={daysValue}
                onChange={handleDaysChange}
            />

            {/* Hours Input */}
            <NumberInput
                size="md"
                min={0}
                max={23}
                label="Hours"
                value={hoursValue}
                onChange={handleHoursChange}
            />
        </Box>
    );
}
