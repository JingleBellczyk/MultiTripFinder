import React, { useEffect, useState } from 'react';
import { MantineProvider } from '@mantine/core';
import { HeaderSearch } from '../../components/HeaderSearch/HeaderSearch';
import { SERVER, PAGE_SIZE } from '../../constants/constants';
import { Footer } from '../../components/Footer/Footer';
import axios from 'axios';
import styles from './List.module.css';
import AdminTable from '../../components/Table/AdminTable';
import {User, UserDTO, UserProps} from "../../types/UserDTO";
import LoadingPage from "../LoadingPage";

const AdminList: React.FC<UserProps> = ({user}) => {
    const [userData, setUserData] = useState<UserDTO[]>([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);

    const fetchUsers = async (endpoint: string) => {
        setLoading(true);
        try {
            const response = await axios.get(endpoint, { withCredentials: true });
            setTotalPages(response.data.totalPages);

            setUserData(response.data.content);
        } catch (error) {
            console.error('Error fetching users:', error);
        }
        finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        const fetchUsersEndpoint = `${SERVER}/user?size=${PAGE_SIZE}&page=${currentPage-1}`;
        fetchUsers(fetchUsersEndpoint);
        console.log(currentPage)
    }, [currentPage]);

    return (
        <MantineProvider theme={{ primaryColor: 'blue' }}>
            <div className={styles.background}>
                {loading ? (
                    <LoadingPage />
                ) : (
                    <>
                        <HeaderSearch />
                        <div className={styles.mainGroup}>
                            <div className={styles.tableContainer}>
                                <AdminTable
                                    user={user}
                                    users={userData}
                                    setUsers={setUserData}
                                    totalPages={totalPages}
                                    setCurrentPage={setCurrentPage}
                                    disablePagination={loading}
                                />
                            </div>
                        </div>
                        <Footer />
                    </>
                )}
            </div>
        </MantineProvider>
    );
};

export default AdminList;
