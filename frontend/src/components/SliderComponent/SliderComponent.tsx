import { useEffect } from 'react';
import { useState } from 'react';
import { Slider, Box } from '@mantine/core';
import classes from "./SliderComponent.module.css";

// Accept value and onChange as props
interface SliderComponentProps {
    value: number; // Current value of the slider
    onChange: (value: number) => void; // Function to handle value changes
}

export function SliderComponent({ value, onChange }: SliderComponentProps) {
    const [sliderValue, setSliderValue] = useState(value); // State for the slider

    // Update local slider value when prop changes
    useEffect(() => {
        setSliderValue(value);
    }, [value]);

    return (
        <Box className={classes.boxStyle}>
            <Slider
                value={sliderValue}
                onChange={(val) => {
                    setSliderValue(val);
                    onChange(val);
                }}
                min={1}
                max={20}
                step={1}
            />
        </Box>
    );
}