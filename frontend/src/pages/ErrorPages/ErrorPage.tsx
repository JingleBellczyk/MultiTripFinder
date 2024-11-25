import React from 'react';
import { Button, Container, Title, Text, MantineProvider } from '@mantine/core';
import { useNavigate } from 'react-router-dom';

interface ErrorPageProps {
    title: string;
    message: string;
    buttonText: string;
    buttonRoute: string;
}

const ErrorPage: React.FC<ErrorPageProps> = ({ title, message, buttonText, buttonRoute }) => {
    const navigate = useNavigate();

    return (
        <MantineProvider theme={{ primaryColor: 'blue' }}>
            <Container
                style={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                    height: '100vh',
                    textAlign: 'center',
                }}
            >
                <Title order={1}>{title}</Title>
                <Text size="lg" style={{ marginTop: '1rem', marginBottom: '2rem' }}>
                    {message}
                </Text>
                <Button size="md" onClick={() => navigate(buttonRoute)}>
                    {buttonText}
                </Button>
            </Container>
        </MantineProvider>
    );
};

export default ErrorPage;
