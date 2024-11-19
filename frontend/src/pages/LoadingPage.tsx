import React from "react";
import { Loader, MantineProvider } from "@mantine/core";

const LoadingPage: React.FC = () => {
    return (
        <MantineProvider theme={{ primaryColor: 'blue' }}>
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <Loader size="xl" color="blue" variant="dots" />
            </div>
        </MantineProvider>
    );
};

export default LoadingPage;
