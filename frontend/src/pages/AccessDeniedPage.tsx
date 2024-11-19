import React from 'react';
import {Button, Container, Title, Text, MantineProvider} from '@mantine/core';
import { useNavigate } from 'react-router-dom';

const AccessDeniedPage: React.FC = () => {
    const navigate = useNavigate();

    return (
        <MantineProvider theme={{primaryColor: 'blue'}}>
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
            <Title order={1}>
                Access Denied!
            </Title>
            <Text size="lg" style={{ marginTop: '1rem', marginBottom: '2rem' }}>
                You do not have permission to access this page.
            </Text>
            <Button size="md" onClick={() => navigate('/')}>
                Go to Homepage
            </Button>
        </Container>
        </MantineProvider>
    );
};

export default AccessDeniedPage;
