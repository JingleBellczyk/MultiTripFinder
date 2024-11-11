import '@mantine/core/styles.css';

import {Container, Group, Burger} from '@mantine/core';
import {Link, useLocation} from 'react-router-dom';
import {useDisclosure} from '@mantine/hooks';
import classes from './HeaderSearch.module.css';
import {Logo} from '../Logo/Logo';
import Login from "../Login/Login";
import useAuth from "../Login/useAuth";
import {AUTHENTICATED_LINKS, UNAUTHENTICATED_LINKS, ADMIN_LINKS} from "../../constants/constants";

export function HeaderSearch() {
    const { isAuthenticated, token, loading } = useAuth();
    const [opened, {toggle}] = useDisclosure(false);
    const location = useLocation()

    const linksToDisplay = isAuthenticated
        ? AUTHENTICATED_LINKS : UNAUTHENTICATED_LINKS;

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
            <Container size="xl" className={classes.inner}>
                <Logo size={13}></Logo>
                <Group gap={5} visibleFrom="md">
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