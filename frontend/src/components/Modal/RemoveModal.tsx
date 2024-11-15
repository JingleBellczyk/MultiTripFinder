import { Modal, Button, ActionIcon } from '@mantine/core';
import { IconX } from '@tabler/icons-react';
import {SavedTag, Tag} from "../../types/SearchDTO";
import { useState } from "react";
import styles from './ButtonModal.module.css';

type ButtonModalProps = {
    tagToDelete: SavedTag;
    onDeleteConfirm: (tag: SavedTag, isRemove?: boolean) => void;
    setIsModalOpen: (open: boolean) => void;
    onModalOpen: () => void;
};

function RemoveModal({ tagToDelete, onDeleteConfirm, setIsModalOpen, onModalOpen }: ButtonModalProps) {
    const [opened, setOpened] = useState(false);

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
        onDeleteConfirm(tagToDelete);
        handleClose();
    };

    return (
        <>
            <ActionIcon
                onClick={(e) => {
                    e.stopPropagation();
                    handleOpen();
                }}
                color="red"
                variant="outline"
                size="sm"
            >
                <IconX size={16} />
            </ActionIcon>
            <Modal
                opened={opened}
                onClose={handleClose}
                size="auto"
                title={`Do you want to delete the tag "${tagToDelete?.name}"?`}
                withCloseButton={false}
            >
                <p>This will delete the tag in all searches.</p>
                <div className={styles.modalButtonsContainer}>
                    <Button onClick={handleClose} variant="outline">
                        No
                    </Button>
                    <Button onClick={handleConfirm} color="red">
                        Yes
                    </Button>
                </div>
            </Modal>
        </>
    );
}

export default RemoveModal;
