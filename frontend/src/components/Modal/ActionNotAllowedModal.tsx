import {Button, Modal, Text} from "@mantine/core";


type modalProps = {
    isModalOpen: boolean,
    setIsModalOpen: (bool: boolean) => void,
    warningMessage: string
}

export default function ActionNotAllowedModal({isModalOpen, setIsModalOpen, warningMessage} : modalProps) {


    return (
        <>
        <Modal
            opened={isModalOpen}
            onClose={() => setIsModalOpen(false)}
            title="Action Not Allowed"
            centered
        >
            <Text color="red" size="sm">
                {warningMessage}
            </Text>
            <Button mt="sm" onClick={() => setIsModalOpen(false)}>
                Close
            </Button>
        </Modal>
    </>);
}