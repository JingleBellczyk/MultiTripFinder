import React, { useState } from 'react';
import dayjs from 'dayjs';
import { DateInput, DateInputProps } from '@mantine/dates';
import classes from './DatePicker.module.css';
import '@mantine/dates/styles.css';

// Custom date parser
const dateParser: DateInputProps['dateParser'] = (input) => {
    return dayjs(input, 'DD/MM/YYYY').toDate();
};

export function DatePicker({value, onDateChange}: {
    value: Date | null;    // Expect value prop
    onDateChange: (date: Date | null) => void;
}) {
    return (
        <DateInput
            className={classes.customInput}
            value={value}               // Bind value prop to DateInput
            dateParser={dateParser}
            onChange={onDateChange}     // Trigger onChange to update parent state
        />
    );
}