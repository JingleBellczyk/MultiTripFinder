import React from 'react';
import { Navigate } from 'react-router-dom';
import LoadingPage from "../../pages/LoadingPage";

interface AuthGuardProps {
    children: React.ReactNode;
    isAuthenticated: boolean;
    loading: boolean;
}

const AuthGuard: React.FC<AuthGuardProps> = ({ children, isAuthenticated, loading }) => {

    if (loading) {
        return <LoadingPage/>
    }
    return isAuthenticated ? <>{children}</> : <Navigate to="/denied" />;

};

export default AuthGuard;
