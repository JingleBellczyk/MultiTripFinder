import {ICON_SIZE, PAGE_SIZE, SERVER, STROKE} from "../../constants/constants";
import { Center, CloseButton, Divider, Pagination, Table, Text, Title} from "@mantine/core";
import styles from "./Table.module.css";
import {User, UserDTO} from "../../types/UserDTO";
import axios from "axios";
import {IconTrash} from "@tabler/icons-react";
import {useEffect, useState} from "react";
import ActionNotAllowedModal from "../Modal/ActionNotAllowedModal";
import ConfirmationModal from "../Modal/ConfirmationModal";

function DeleteIcon({ onClick }: { onClick: () => void }) {
    return <CloseButton onClick={onClick} icon={<IconTrash size={ICON_SIZE} stroke={STROKE} />} />;
}

type AdminTableProps = {
    user: User | null;
    users: UserDTO[];
    setUsers: React.Dispatch<React.SetStateAction<UserDTO[]>>;
    fetchUsers: (endpoint: string) => Promise<void>;
    totalPages: number;
    currentPage: number;
    setCurrentPage: (page: number) => void;
    disablePagination: boolean;
};

const AdminTable: React.FC<AdminTableProps> = ({
                                                   user,
                                                   users,
                                                   setUsers,
                                                   fetchUsers,
                                                   totalPages,
                                                   currentPage,
                                                   setCurrentPage,
                                                   disablePagination,
                                               }) => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalMessage, setModalMessage] = useState("");
    const [isConfirmationOpen, setIsConfirmationOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState<UserDTO | null>(null);

    useEffect(() => {}, [users]);

    const handleDeleteConfirmation = (userToDelete: UserDTO) => {
        if (userToDelete.id === user?.id) {
            setIsModalOpen(true);
            setModalMessage("You cannot delete your own account as an Admin.");
            return;
        }

        if (userToDelete.role === "A") {
            setIsModalOpen(true);
            setModalMessage("You cannot delete an Admin account.");
            return;
        }

        setSelectedUser(userToDelete);
        setIsConfirmationOpen(true);
    };

    const confirmDelete = async () => {
        if (!selectedUser) return;

        const { id, email, role } = selectedUser;

        try {
            await axios.delete(`${SERVER}/user/${id}`, { withCredentials: true });
            setUsers((prevUsers) => prevUsers.filter((u) => u.id !== id));

            if (users.length - 1 === 0 && currentPage > 0) {
                setCurrentPage(currentPage - 1);
            } else {
                const fetchUsersEndpoint = `${SERVER}/user?size=${PAGE_SIZE}&page=${currentPage - 1}`;
                await fetchUsers(fetchUsersEndpoint);
            }
        } catch (error) {
            console.error("Error deleting user:", error);
        } finally {
            setIsConfirmationOpen(false);
            setSelectedUser(null);
        }
    };

    const rows = users.map((user, index) => (
        <tr key={index}>
            <td>{user.id}</td>
            <td>{user.email}</td>
            <td>{user.role === 'U' ? 'USER' : user.role === 'A' ? 'ADMIN' : 'Unknown'}</td>
            <td>
                <DeleteIcon onClick={() => handleDeleteConfirmation(user)} />
            </td>
        </tr>
    ));

    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };

    return (
        <div className={styles.tableContainer}>
            <ActionNotAllowedModal isModalOpen={isModalOpen} setIsModalOpen={setIsModalOpen} warningMessage={modalMessage} />
            <ConfirmationModal
                isOpen={isConfirmationOpen}
                onClose={() => setIsConfirmationOpen(false)}
                onConfirm={confirmDelete}
                message={`Do you want to delete user ${selectedUser?.email}?`}
            />
            <div className={styles.titleContainer}>
                <Title>List of users</Title>
                <Text color="gray">As an admin, you can manage users here.</Text>
            </div>
            <Divider />
            <div>
                {users.length === 0 ? (
                    <Center>
                        <Text size="xl">No users available.</Text>
                    </Center>
                ) : (
                    <>
                        <Table highlightOnHover className={styles.table}>
                            <thead>
                            <tr>
                                <th><Text size="lg">User ID</Text></th>
                                <th><Text size="lg">Email</Text></th>
                                <th><Text size="lg">Role</Text></th>
                                <th><Text size="lg">Actions</Text></th>
                            </tr>
                            </thead>
                            <tbody>{rows}</tbody>
                        </Table>
                        <Pagination
                            total={totalPages}
                            onChange={handlePageChange}
                            value={currentPage}
                            disabled={disablePagination}
                        />
                    </>
                )}
            </div>
        </div>
    );
};

export default AdminTable;