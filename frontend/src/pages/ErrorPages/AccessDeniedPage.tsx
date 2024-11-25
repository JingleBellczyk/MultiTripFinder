import React from 'react';
import ErrorPage from './ErrorPage';

const AccessDeniedPage: React.FC = () => {
    return (
        <ErrorPage
            title="Access Denied!"
            message="You do not have permission to access this page."
            buttonText="Go to Homepage"
            buttonRoute="/"
        />
    );
};

export default AccessDeniedPage;
