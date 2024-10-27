import '@mantine/core/styles.css';

import {useState} from 'react';
import {Container, Group, Burger, Text, MantineProvider, Flex} from '@mantine/core';
import {Link, useLocation} from 'react-router-dom';
import {useDisclosure} from '@mantine/hooks';
import classes from './HeaderSearch.module.css';
import {Logo} from '../Logo/Logo';
import Login from "../Login/Login";
import useAuth from "../Login/useAuth";

export function HeaderSearch() {
    const { isAuthenticated, token, loading } = useAuth();
    const [opened, {toggle}] = useDisclosure(false);
    const location = useLocation()
    const authenticatedLinks = [
        { link: '/account', label: 'My account' },
        { link: '/travels', label: 'My Travels' },
        { link: '/searches', label: 'My Searches' },
        { link: '/support', label: 'Support' },
    ];

    // Links for unauthenticated users
    const unauthenticatedLinks = [
        { link: '/support', label: 'Support' },
    ];

    // Additional link for administrators
    const adminLink = [
        { link: '/users', label: 'Users' },
    ];

    const linksToDisplay = isAuthenticated
        ? authenticatedLinks : unauthenticatedLinks;

    const items = linksToDisplay.map((link) => (
        <Link
            key={link.label}
            to={link.link}
            className={classes.link}
            data-active={location.pathname === link.link || undefined}
        >
            {link.label}
        </Link>
    ));

    return (
        <header className={classes.header}>
            <Container size="md" className={classes.inner}>
                <Logo/>
                <Group gap={5} visibleFrom="xs">
                    {items}
                </Group>
                <div className={classes.login}>
                    <Login isAuthenticated={isAuthenticated} token={token}/>
                </div>
                <Burger opened={opened} onClick={toggle} hiddenFrom="xs" size="sm"/>
            </Container>
        </header>
    );
}