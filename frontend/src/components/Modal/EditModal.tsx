import { Modal, Button, ActionIcon, TextInput } from '@mantine/core';
import { IconEdit } from '@tabler/icons-react';  // Using the edit icon
import {SavedTag, Tag} from "../../types/SearchDTO";
import { useState } from "react";
import styles from './ButtonModal.module.css';
import {MAX_TAG_LENGTH} from "../../constants/constants";

type EditTagModalProps = {
    tagToEdit: SavedTag;
    onEditConfirm: (tagToEdit: SavedTag, newTagName: string) => Promise<void>;
    setIsModalOpen: (open: boolean) => void;
    onModalOpen: () => void;
};

function EditTagModal({ tagToEdit, onEditConfirm, setIsModalOpen, onModalOpen }: EditTagModalProps) {
    const [opened, setOpened] = useState(false);
    const [newTagName, setNewTagName] = useState(tagToEdit?.name || "");  // Initialize with current tag name

    const handleOpen = () => {
        onModalOpen();
        setIsModalOpen(true);
        setOpened(true);
    };

    const handleClose = () => {
        setOpened(false);
        setIsModalOpen(false);
    };

    const handleConfirm = () => {
        onEditConfirm(tagToEdit, newTagName);
        handleClose();
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        // Convert input to uppercase and update state
        setNewTagName(e.target.value);
    };

    return (
        <>
            <ActionIcon size="sm"
                        onClick={(e) => {
                            e.stopPropagation();
                            handleOpen();
                        }}
                        color="blue"
                        variant="outline"
            >
                <IconEdit size={16} />
            </ActionIcon>
            <Modal
                opened={opened}
                onClose={handleClose}
                size="auto"
                title={`Editing tag "${tagToEdit?.name}".`}
                withCloseButton={false}
            >
                <p>This will change the name of the tag in all searches.</p>
                <TextInput
                    label="New Tag Name"
                    value={newTagName}
                    onChange={handleInputChange}
                    autoFocus
                    maxLength={MAX_TAG_LENGTH}
                />
                <div className={styles.modalButtonsContainer}>
                    <Button onClick={handleClose} variant="outline">
                        Cancel
                    </Button>
                    <Button onClick={handleConfirm} color="blue" disabled={!newTagName.trim()}>
                        Save
                    </Button>
                </div>
            </Modal>
        </>
    );
}

export default EditTagModal;
