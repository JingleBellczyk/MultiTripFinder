import {useState} from 'react';
import {Slider, Box} from '@mantine/core';
import classes from "./SliderComponent.module.css"

export function SliderComponent() {
    const [value, setValue] = useState(3); // Initial value
    const [endValue, setEndValue] = useState(3); // End value

    return (
        <Box
            className={classes.boxStyle}
        >
            <Slider
                value={value}
                onChange={setValue}
                onChangeEnd={setEndValue}
                min={1}
                max={20}
                step={1}
            />
        </Box>
    );
}