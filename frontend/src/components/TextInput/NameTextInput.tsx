import React, { useState, useEffect, useRef } from 'react';
import { TextInput, ActionIcon, Text } from '@mantine/core';
import { IconCheck, IconPencil, IconX } from '@tabler/icons-react';

interface NameTextInputProps {
    name: string;
    onNameChange: (newName: string) => void;
}

const NameTextInput: React.FC<NameTextInputProps> = ({ name, onNameChange }) => {
    const [isEditing, setIsEditing] = useState(false);
    const [currentName, setCurrentName] = useState(name);
    const inputRef = useRef<HTMLDivElement>(null);

    // Handle entering edit mode
    const handleEditClick = () => setIsEditing(true);

    // Handle saving the new name
    const handleSaveClick = () => {
        setIsEditing(false);
        onNameChange(currentName);
    };

    // Handle canceling edit without saving
    const handleCancelClick = () => {
        setIsEditing(false);
        setCurrentName(name); // Revert to the original name
    };

    // Handle clicks outside the component
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (isEditing && inputRef.current && !inputRef.current.contains(event.target as Node)) {
                handleCancelClick();
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [isEditing]);

    // Update `currentName` if the `name` prop changes
    useEffect(() => {
        setCurrentName(name);
    }, [name]);

    return (
        <div ref={inputRef}>
            {isEditing ? (
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <TextInput
                        value={currentName}
                        onChange={(e) => setCurrentName(e.currentTarget.value)}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter') handleSaveClick();
                        }}
                        autoFocus
                        maxLength={100}
                    />
                    <ActionIcon onClick={handleSaveClick} color="blue" variant="filled" size="sm">
                        <IconCheck />
                    </ActionIcon>
                    <ActionIcon onClick={handleCancelClick} color="red" variant="filled" size="sm">
                        <IconX />
                    </ActionIcon>
                </div>
            ) : (
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Text
                        size="md"
                        style={{ fontStyle: 'italic', cursor: 'pointer' }}
                        onClick={handleEditClick}
                    >
                        {currentName}
                    </Text>
                    <ActionIcon onClick={handleEditClick} variant="transparent">
                        <IconPencil size={16} />
                    </ActionIcon>
                </div>
            )}
        </div>
    );
};

export default NameTextInput;
