import React from 'react';
import { Radio } from '@mantine/core';

interface RadioComponentProps {
    labels: string[]; // Dynamic list of labels (criteria)
    selectedValue: string; // The selected radio value
    onChange: (value: string) => void; // Callback to update the parent state
}

const RadioComponent: React.FC<RadioComponentProps> = ({ labels, selectedValue, onChange }) => {
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
                    value={label.toLowerCase()} // Ensure the value is lowercase for consistency
                    label={label}
                />
            ))}
        </Radio.Group>
    );
};

export default RadioComponent;