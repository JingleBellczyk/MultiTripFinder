import React, { useState, useEffect, useRef } from 'react';
import {TextInput, ActionIcon, Text, Alert} from '@mantine/core';
import {IconAlertCircle, IconCheck, IconPencil, IconX} from '@tabler/icons-react';

interface NameTextInputProps {
    name: string;
    onNameChange: (newName: string) => Promise<number>;
}

const NameTextInput: React.FC<NameTextInputProps> = ({ name, onNameChange }) => {
    const [isEditing, setIsEditing] = useState(false);
    const [currentName, setCurrentName] = useState(name);
    const inputRef = useRef<HTMLDivElement>(null);
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    // Handle entering edit mode
    const handleEditClick = () => {
        setIsEditing(true);
        setErrorMessage(null);
    }

    const handleSaveClick = async () => {
        const trimmedName = currentName.trim().replace(/\s+/g, ' ');

        // Prevent saving empty or whitespace-only names
        if (!trimmedName) {
            setErrorMessage('Name cannot be empty.');
            return;
        }

        // Prevent redundant API calls for unchanged names
        if (trimmedName === name) {
            setIsEditing(false);
            return;
        }

        // Set the trimmed name to state before calling the API
        setCurrentName(trimmedName);

        try {
            const status = await onNameChange(trimmedName);
            if (status === 200) {
                setErrorMessage(null);
                setIsEditing(false); // Exit edit mode on success
            } else if (status === 409) {
                setErrorMessage('Search with this name already exists. Choose another name.');
                setIsEditing(true); // Stay in edit mode for retry
            } else {
                setErrorMessage('An unexpected error occurred. Please try again.');
                setIsEditing(true);
            }
        } catch (error) {
            setErrorMessage('An unexpected error occurred. Please try again.');
            setIsEditing(true);
        }
    };

    const handleCancelClick = () => {
        setIsEditing(false);
        setCurrentName(name); // Revert to original name
        setErrorMessage(null);
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
                        styles={{ input: { maxWidth: 300 } }}
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
            {errorMessage && (
                <Alert
                    icon={<IconAlertCircle size={16} />}
                    title="Error"
                    color="red"
                    mt="sm"
                    withCloseButton
                    onClose={() => setErrorMessage(null)}
                >
                    {errorMessage}
                </Alert>
            )}
        </div>
    );
};

export default NameTextInput;
