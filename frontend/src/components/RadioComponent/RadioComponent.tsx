import React from 'react';
import { Radio } from '@mantine/core';

interface RadioComponentProps {
    labels: string[]; // Dynamic list of labels (criteria)
    selectedValue: string; // The selected radio value
    onChange: (value: string) => void; // Callback to update the parent state
}

const RadioComponent: React.FC<RadioComponentProps> = ({ labels, selectedValue = "PRICE", onChange }) => {
    return (
        <Radio.Group
            value={selectedValue}
            onChange={onChange} // Trigger the callback when the value changes
            name="minimizedCriterion"
            withAsterisk
        >
            {labels.map((label) => (
                <Radio
                    key={label}
                    m="lg"
                    size="lg"
                    value={label.toUpperCase()} // Value should match what you want in selectedCriterion
                    label={label}
                />
            ))}
        </Radio.Group>
    );
};

export default RadioComponent;