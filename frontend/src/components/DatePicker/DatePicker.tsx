import React, { useState } from 'react';
import dayjs from 'dayjs';
import { DateInput, DateInputProps } from '@mantine/dates';
import classes from './DatePicker.module.css';
import '@mantine/dates/styles.css';

// Custom date parser
const dateParser: DateInputProps['dateParser'] = (input) => {
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