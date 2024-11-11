import '@mantine/core/styles.css';
import {Button, Group, Text, Avatar} from '@mantine/core';
import React from 'react';
import axios from "axios";
import {User} from "../../constants/constants";

const SERVER = "http://localhost:8080"
const CLIENT = "http:///localhost:3000"

type LoginProps = {
    isAuthenticated: boolean;
    token: string | null;
    user: User | null;
};
const Login: React.FC<LoginProps> = ({ isAuthenticated, token, user }) => {
    const googleLogin = () => {
        window.location.href = `${SERVER}/oauth2/authorization/google`;
    };

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
                    <Group align="center" gap={20}>
                        <Text c="white" fw={500} size="xl" >{user.given_name}</Text>
                        <div>
                            {user.picture && (
                                <Avatar
                                    src={user.picture}
                                    alt="User Profile"
                                    radius="xl"
                                    size="lg"
                                />
                            )}
                        </div>
                        <Button onClick={handleLogout} color="blue" size="md">
                            Logout
                        </Button>
                    </Group>
                </>
            ) : (
                <Button onClick={googleLogin} color="blue" size="md">Login with Google</Button>
            )}
        </div>
    );
};

export default Login;
