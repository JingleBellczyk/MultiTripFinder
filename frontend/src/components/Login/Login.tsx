import '@mantine/core/styles.css';
import {Button, Group, Text, Avatar} from '@mantine/core';
import React, {useState, useEffect} from 'react';
import axios from "axios";

const SERVER = "http://localhost:8080"
const CLIENT = "http:///localhost:3000"

type LoginProps = {
    isAuthenticated: boolean;
    token: string | null;
};
const Login: React.FC<LoginProps> = ({ isAuthenticated, token }) => {
    const [user, setUser] = useState<{ given_name: string , picture: string} | null>(null);
    const googleLogin = () => {
        window.location.href = `${SERVER}/oauth2/authorization/google`;
    };

    useEffect(() => {
        console.log("useEffect triggered - isAuthenticated:", isAuthenticated, "token:", token); // Debugging log
        if (isAuthenticated && token) {
            const fetchUser = async () => {
                try {
                    const response = await axios.get(`${SERVER}/auth/user-info`, {
                        // headers: {
                        //     Authorization: `Bearer ${token}`
                        // },
                        withCredentials: true
                    });
                    setUser(response.data);
                    console.log("User data fetched:", response.data);
                } catch (error) {
                    console.error("User not authenticated:", error);
                }
            };
            fetchUser()
        }
    }, [isAuthenticated, token]);
    const handleLogout = async () => {
        try {
            await axios.post(`${SERVER}/logout`, {}, { withCredentials: true });
            console.log("Logout successful");
            window.location.href = CLIENT;
        } catch (error) {
            console.error("Logout failed:", error);
        }
    };

    return (
        <div>
            {isAuthenticated && user ? (
                <>
                <Group align="center">
                    <Text c="white" fw={500} size="sm" >{user.given_name}</Text>
                    <div>
                        {user.picture && (
                            <Avatar
                                src={user.picture}
                                alt="User Profile"
                                radius="xl"
                                size="lg"
                                style={{ marginTop: '8px' }}
                            />
                        )}
                    </div>
                    <Button onClick={handleLogout} color="blue">
                        Logout
                    </Button>
                </Group>
                </>
            ) : (
                <Button onClick={googleLogin} color="blue">Login with Google</Button>
            )}
        </div>
    );
};

export default Login;
