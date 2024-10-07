import '@mantine/core/styles.css';

import {useState} from 'react';
import {Container, Group, Burger, Text, MantineProvider, Flex} from '@mantine/core';
import {useDisclosure} from '@mantine/hooks';
import classes from './HeaderSearch.module.css';
import {Logo} from '../Logo/Logo';

// params
type LinkItem = {
    link: string;
    label: string;
};

type Props = {
    links: LinkItem[];
};

export function HeaderSearch({links}: Props) {
    const [opened, {toggle}] = useDisclosure(false);
    const [active, setActive] = useState(links[0].link);

    const items = links.map((link) => (
        <a
            key={link.label}
            href={link.link}
            className={classes.link}
            data-active={active === link.link || undefined}
            onClick={(event) => {
                event.preventDefault();
                setActive(link.link);
            }}
        >
            {link.label}
        </a>
    ));

    return (
        <header className={classes.header}>
            <Container size="md" className={classes.inner}>
                <Logo/>
                <Group gap={5} visibleFrom="xs">
                    {items}
                </Group>
                <Burger opened={opened} onClick={toggle} hiddenFrom="xs" size="sm"/>
            </Container>
        </header>
    );
}