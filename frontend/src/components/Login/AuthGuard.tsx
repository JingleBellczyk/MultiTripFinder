// AuthGuard.tsx
import React from 'react';
import { Navigate } from 'react-router-dom';
import useAuth from './useAuth';
import {Loader, MantineProvider} from '@mantine/core';

interface AuthGuardProps {
    children: React.ReactNode;
}

const AuthGuard: React.FC<AuthGuardProps> = ({ children }) => {
    const { isAuthenticated, loading } = useAuth();

    if (loading) {
        return (
            <MantineProvider>
                <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                    <Loader />
                </div>
            </MantineProvider>);
    }

    return isAuthenticated ? <>{children}</> : <Navigate to="/" />;
};

export default AuthGuard;
