import React, {useState} from 'react';
import {Modal, Tooltip, Button, TextInput, Text, Box} from '@mantine/core';
import styles from "./SaveSearchTripModal.module.css";

interface SaveSearchTripModalProps {
    entityType: string;
    onSave: (name: string) => Promise<{ isSuccess: boolean; errorMessage?: string }>;
}

export function SaveSearchTripModal({entityType, onSave}: SaveSearchTripModalProps) {
    const [opened, setOpened] = useState(false);
    const [name, setName] = useState("");
    const [error, setError] = useState("");

    const handleSave = async (e: React.MouseEvent) => {
        e.stopPropagation();

        if (!name) {
            setError("Name cannot be empty");
            return;
        }

        const {isSuccess, errorMessage} = await onSave(name);

        if (isSuccess) {
            resetFields();
            setOpened(false);
        } else {
            setError(errorMessage || "An error occurred while saving.");
        }
    };

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        event.stopPropagation();
        setName(event.currentTarget.value);
    };

    const resetFields = () => {
        setName("");
        setError("");
    };

    const handleClose = (e?: React.MouseEvent) => {
        e?.stopPropagation();
        resetFields();
        setOpened(false);
    };

    return (
        <>
            <Modal
                opened={opened}
                onClose={handleClose}
                title={<span className={styles.modalTitle}>{`Save ${entityType}`}</span>}
                onClick={(e) => e.stopPropagation()}
            >
                <TextInput
                    label="Name"
                    placeholder={`Enter ${entityType} name...`}
                    value={name}
                    onChange={handleInputChange}
                    required
                    onClick={(e) => e.stopPropagation()}
                />
                {error && <Text color="red">{error}</Text>}

                <Box mt="lg" className={styles.buttonGroup} onClick={(e) => e.stopPropagation()}>
                    <Button
                        className={styles.pinkButton}
                        onClick={handleSave}
                    >
                        Save
                    </Button>
                    <Button
                        className={styles.pinkButton}
                        variant="outline"
                        onClick={handleClose}
                    >
                        Cancel
                    </Button>
                </Box>
            </Modal>

            <Tooltip label={entityType === "trip" ? "Save Trip" : "Save Search"} position="top" withArrow>
                <Button
                    className={styles.saveButton}
                    onClick={(e) => {
                        e.stopPropagation();
                        setOpened(true);
                    }}
                >
                    {entityType === "trip" ? "💾" : "Save search 💾"}
                </Button>
            </Tooltip>
        </>
    );
}
