import '@mantine/core/styles.css';
import React, { useMemo } from 'react';
import { Container, Group, Burger } from '@mantine/core';
import { Link, useLocation } from 'react-router-dom';
import { useDisclosure } from '@mantine/hooks';
import classes from './HeaderSearch.module.css';
import { Logo } from '../Logo/Logo';
import Login from "../Login/Login";
import useAuth from "../../hooks/useAuth";
import { AUTHENTICATED_LINKS, UNAUTHENTICATED_LINKS, ADMIN_LINKS } from "../../constants/constants";

export const HeaderSearch: React.FC = React.memo(() => {
    const { isAuthenticated, token, user } = useAuth();
    const [opened, { toggle }] = useDisclosure(false);
    const location = useLocation();

    const loginComponent = useMemo(() => (
        <Login isAuthenticated={isAuthenticated} token={token} user={user}/>
    ), [isAuthenticated, token, user]);

    const items = useMemo(() => {
        let linksToDisplay = isAuthenticated ? AUTHENTICATED_LINKS : UNAUTHENTICATED_LINKS;

        if (isAuthenticated && user?.role === "ADMIN") {
            linksToDisplay = [...AUTHENTICATED_LINKS, ...ADMIN_LINKS];
        }

        return linksToDisplay.map((link) => (
            <Link
                key={link.label}
                to={link.link}
                className={classes.link}
                data-active={location.pathname === link.link || undefined}
            >
                {link.label}
            </Link>
        ));
    }, [isAuthenticated, user?.role, location.pathname]);

    return (
        <header className={classes.header}>
            <Container size="lg" className={classes.inner}>
                <Logo />
                <Group gap={5} align="center" visibleFrom="md">
                    {items}
                </Group>
                <div className={classes.login}>
                    {loginComponent}
                </div>
                <Burger
                    opened={opened}
                    onClick={toggle}
                    hiddenFrom="xs"
                    size="sm"
                    className={classes.burger}
                />
            </Container>
        </header>
    );
});
