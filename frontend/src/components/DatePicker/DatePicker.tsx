import dayjs from 'dayjs';
import {DateInput, DateInputProps} from '@mantine/dates';
import classes from "./DatePicker.module.css"

const dateParser: DateInputProps['dateParser'] = (input) => {
    if (input === 'WW2') {
        return new Date(1939, 8, 1);
    }

    return dayjs(input, 'DD/MM/YYYY').toDate();
};

export function DatePicker() {
    return (
        <DateInput
            className={classes.customInput}
        ></DateInput>
    );
}