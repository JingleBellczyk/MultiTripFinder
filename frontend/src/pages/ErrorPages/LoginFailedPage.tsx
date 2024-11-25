import React from 'react';
import ErrorPage from './ErrorPage';

const LoginFailedPage: React.FC = () => {
    return (
        <ErrorPage
            title="Login failed!"
            message="Something went wrong with logging in. You have to agree to share your data (name, surname, e-mail and profile picture) in order to successfully log in."
            buttonText="Go to Homepage"
            buttonRoute="/"
        />
    );
};

export default LoginFailedPage;
