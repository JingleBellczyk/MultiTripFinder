import { useDisclosure } from '@mantine/hooks';
import { Modal, Button, ActionIcon } from '@mantine/core';
import { IconX } from '@tabler/icons-react';
import { Tag } from "../../types/SearchDTO";

interface ButtonModalProps {
    tagToDelete: Tag;
    onDeleteConfirm: (tagToRemove: Tag) => void;
}

function ButtonModal({ tagToDelete, onDeleteConfirm }: ButtonModalProps) {
    const [opened, { open, close }] = useDisclosure(false);

    const handleConfirm = () => {
        onDeleteConfirm(tagToDelete)
        close(); // Close the modal after confirmation
    };

    return (
        <>
            <ActionIcon
                onClick={(e) => {
                    e.stopPropagation();
                    open();
                }}
                color="red"
                variant="outline"
            >
                <IconX size={16} />
            </ActionIcon>

            <Modal
                opened={opened}
                onClose={close}
                size="auto"
                title={`Do you want to delete the tag "${tagToDelete?.name}"?`}
                withCloseButton={false}
            >
                <p>This will delete the tag in all searches.</p>
                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px', marginTop: '16px' }}>
                    <Button onClick={close} variant="outline">
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

export default ButtonModal;
