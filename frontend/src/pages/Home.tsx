import { MantineProvider, Text, Container, Button, Group, Divider, SimpleGrid } from "@mantine/core";
import { HeaderSearch } from "../components/HeaderSearch/HeaderSearch";
import React, { useMemo } from "react";
import logoSvg from "../assets/icons/mtfLogo.png";
import { useNavigate } from 'react-router-dom';
import styles from './Home.module.css';
import {User} from "../constants/constants";
import {Footer} from "../components/Footer/Footer";

type LoginProps = {
    isAuthenticated: boolean;
    token: string | null;
    user: User | null;
};

const Home: React.FC<LoginProps> = ({ isAuthenticated, token, user }) => {
    const navigate = useNavigate();

    const mainContent = useMemo(() => {
        if (isAuthenticated) {
            if (user?.role ==="A") {
                return (
                    <SimpleGrid cols={1}>
                        <Text className={styles.welcomeText}>Welcome, Admin!</Text>
                        <Text className={styles.descriptionText}>
                            As an admin, you have control over the system. You can manage user accounts and configure settings.
                        </Text>
                    </SimpleGrid>
                );
            } else {
                return (
                    <SimpleGrid cols={1}>
                        <Text className={styles.welcomeText}>Welcome back, {user?.given_name}! You're logged in.</Text>
                        <Text className={styles.descriptionText}>As a logged-in user, you can:</Text>
                        <ul>
                            <li><Text className={styles.descriptionText}>Save your searches for easy access later, edit, and delete them whenever you want.</Text></li>
                            <li><Text className={styles.descriptionText}>Store your travels to keep everything organized.</Text></li>
                            <li><Text className={styles.descriptionText}>View all your searches and travels in separate lists.</Text></li>
                            <li><Text className={styles.descriptionText}>Filter and sort them by your preferred criteria.</Text></li>
                        </ul>
                        <Text className={styles.descriptionText}>
                            With these features, planning your next trip is easier and more efficient.
                        </Text>
                    </SimpleGrid>
                );
            }
        } else {
            return (
                <>
                    <SimpleGrid cols={1}>
                        <Text className={styles.descriptionText}>
                            Please log in to access more features.
                        </Text>
                        <Text className={styles.descriptionText}>
                            To unlock additional functionalities, including saving your searches and travels, please log in.
                        </Text>
                    </SimpleGrid>
                    <Divider className={styles.divider} />
                    <Container className={styles.buttonContainer}>
                        <Button variant="filled" size="md" onClick={() => navigate('/search')}>
                            Click to go to searching page
                        </Button>
                    </Container>
                </>
            );
        }
    }, [isAuthenticated, user, navigate]);

    return (
        <MantineProvider theme={{ primaryColor: 'blue' }}>
            <HeaderSearch />
            <Container className={styles.container}>
                <Divider size={20} color="white" className={styles.divider}></Divider>
                <Group align="center" className={styles.headerGroup}>
                    <Text size="xl" className={styles.welcomeText}>
                        Welcome to MultiTripFinder!
                    </Text>
                    <Text className={styles.descriptionText}>
                        MultiTripFinder is your go-to tool for finding the best travel routes across Europe. It helps you discover the most efficient ways to travel between multiple destinations, whether by flight, train, or bus. The app makes it easy to plan your journey, saving you time and money while optimizing your route.
                    </Text>
                    {mainContent}
                </Group>
                <Group>
                    <img src={logoSvg} alt="MultiTripFinder Logo" className={styles.logoImage} />
                </Group>
            </Container>
            <Footer></Footer>
        </MantineProvider>
    );
};

export default Home;
