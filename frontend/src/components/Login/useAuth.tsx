// useAuth.tsx
import { useState, useEffect } from 'react';
import axios from 'axios';

const SERVER = "http://localhost:8080";

const useAuth = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);
    const [token, setToken] = useState<string | null>(null);

    useEffect(() => {
        const fetchToken = async () => {
            try {
                const response = await axios.get(`${SERVER}/auth/token`, { withCredentials: true });
                setToken(response.data);
                setIsAuthenticated(true);
            } catch (error) {
                setIsAuthenticated(false);
            } finally {
                setLoading(false);
            }
        };

        fetchToken();
    }, []);

    return { isAuthenticated, token, loading };
};

export default useAuth;
