import { useState, useEffect } from 'react';
import axios from 'axios';
import {SERVER} from "../constants/constants";
import {User} from "../types/UserDTO";



const fetchUser = async (
    setUser: React.Dispatch<React.SetStateAction<User | null>>,
    setIsAuthenticated: React.Dispatch<React.SetStateAction<boolean>>
) => {
    try {
        const response = await axios.get(`${SERVER}/auth/user-info`, { withCredentials: true });
        setUser(response.data);
        setIsAuthenticated(true);
        console.log("User data fetched:", response.data);
    } catch (error) {
        if (axios.isAxiosError(error)) {
            console.error("Error fetching user data:", error.response?.data || error.message);
        } else {
            console.error("Unexpected error:", error);
        }
        setIsAuthenticated(false); // Reset authentication if fetching fails
    }
};

const useAuth = () => {
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [loading, setLoading] = useState<boolean>(true);
    const [token, setToken] = useState<string | null>(null);
    const [user, setUser] = useState<User | null>(null);

    useEffect(() => {
        const fetchTokenAndUser = async () => {
            try {
                const response = await axios.get(`${SERVER}/auth/token`, { withCredentials: true });
                setToken(response.data);

                await fetchUser(setUser, setIsAuthenticated);
            } catch (error) {
                console.error("Error fetching token or user data:", error);
                setIsAuthenticated(false);
            } finally {
                setLoading(false);
            }
        };

        fetchTokenAndUser();
    }, []);

    return { isAuthenticated, token, user, loading };
};

export default useAuth;
