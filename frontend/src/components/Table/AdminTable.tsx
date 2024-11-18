
import {ICON_SIZE, SERVER, STROKE} from "../../constants/constants";
import {Center, CloseButton, Divider, Pagination, Table, Text, Title} from "@mantine/core";
import styles from "./Table.module.css";
import {User, UserDTO} from "../../types/UserDTO";
import axios from "axios";
import {IconTrash} from "@tabler/icons-react";
import {useEffect} from "react";



function DeleteIcon({ onClick }: { onClick: () => void }) {
    return <CloseButton onClick={onClick} icon={<IconTrash size={ICON_SIZE} stroke={STROKE} />} />;
}

type AdminTableProps = {
    user: User | null;
    users: UserDTO[];
    setUsers: React.Dispatch<React.SetStateAction<UserDTO[]>>;
    totalPages: number;
    setCurrentPage: (page: number) => void;
    disablePagination: boolean;
};

const AdminTable: React.FC<AdminTableProps> = ({ user, users, setUsers, totalPages, setCurrentPage, disablePagination }) => {


    useEffect(() => {
    }, [users]);
    const deleteUser = async (id: number) => {
        if (id === user?.id) {
            console.error("As an Admin, you cannot delete your own account.");
            return;
        }

        try {
            await axios.delete(`${SERVER}/user/${id}`, { withCredentials: true });
            setUsers((prevUsers) => prevUsers.filter((user) => user.id !== id));
        } catch (error) {
            console.error('Error deleting user:', error);
        }
    };

    const rows = users.map((user, index) => (
        <tr key={index}>
            <td>{user.id}</td>
            <td>{user.email}</td>
            <td>{user.role}</td>
            <td>

                <DeleteIcon onClick={() => deleteUser(user.id)} />
            </td>
        </tr>
    ));

    const handlePageChange = (page: number) => {
        console.log(page);
        setCurrentPage(page);
    };

    return (
        <div className={styles.tableContainer}>
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
                                <th>User ID</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>{rows}</tbody>
                        </Table>
                        <Pagination total={totalPages} onChange={handlePageChange} disabled={disablePagination}/>
                    </>
                )}
            </div>
        </div>
    );
};

export default AdminTable;