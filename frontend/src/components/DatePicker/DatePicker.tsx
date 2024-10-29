import React, { useState } from 'react';
import dayjs from 'dayjs';
import { DateInput, DateInputProps } from '@mantine/dates';
import classes from './DatePicker.module.css';

// Custom date parser
const dateParser: DateInputProps['dateParser'] = (input) => {
    if (input === 'WW2') {
        return new Date(1939, 8, 1);
    }

    return dayjs(input, 'DD/MM/YYYY').toDate();
};

// DatePicker component with an onChange handler to capture the date
export function DatePicker({ onDateChange }: { onDateChange: (date: Date | null) => void }) {
    return (
        <DateInput
            className={classes.customInput}
            dateParser={dateParser} // Use the custom date parser
            onChange={onDateChange} // Pass the parsed date to the parent component
        />
    );
}