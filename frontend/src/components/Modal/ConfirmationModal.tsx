import React from "react";
import { Modal, Text, Button, Group } from "@mantine/core";

type ConfirmationModalProps = {
    isOpen: boolean;
    onClose: () => void;
    onConfirm: () => void;
    message: string;
};

const ConfirmationModal: React.FC<ConfirmationModalProps> = ({ isOpen, onClose, onConfirm, message }) => {
    return (
        <Modal opened={isOpen} onClose={onClose} title="Confirm Deletion" centered>
            <Text>{message}</Text>
            <Group mt="md">
                <Button onClick={onClose} variant="outline">
                    No
                </Button>
                <Button onClick={onConfirm} color="red">
                    Yes
                </Button>
            </Group>
        </Modal>
    );
};

export default ConfirmationModal;
