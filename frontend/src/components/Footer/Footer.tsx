import {ActionIcon, Anchor, Group, rem, Tooltip} from '@mantine/core';
import {IconBrandGoogleMaps, IconInputSearch, IconPlaneArrival} from '@tabler/icons-react';
import classes from './Footer.module.css';
import {Logo} from "../Logo/Logo";

const links = [
    {link: 'https://policies.google.com/privacy', label: 'Privacy'},
    {link: 'https://github.com/JingleBellczyk/MultiTripFinder', label: 'Github'},
];

export function Footer() {
    const items = links.map((link) => (
        <Anchor
            c="dimmed"
            key={link.label}
            href={link.link}
            lh={1}
            target={link.link.startsWith('http') ? '_blank' : '_self'}
            rel={link.link.startsWith('http') ? 'noopener noreferrer' : ''}
            size="sm"
        >
            {link.label}
        </Anchor>
    ));

    return (
        <div className={classes.footer}>
            <div className={classes.inner}>
                <Logo size={5}></Logo>

                <Group className={classes.links}>{items}</Group>

                <Group gap="xs" justify="flex-end" wrap="nowrap">
                    <a href="https://developers.google.com/maps/documentation/routes" target="_blank"
                       rel="noopener noreferrer">
                        <Tooltip label="Google Routes API" position="top" withArrow>
                            <ActionIcon size="lg" variant="default" radius="xl">
                                <IconBrandGoogleMaps style={{width: rem(18), height: rem(18)}} stroke={1.5}/>
                            </ActionIcon>
                        </Tooltip>
                    </a>
                    <a href="https://developers.amadeus.com/self-service/category/cars-and-transfers/api-doc/transfer-search/api-reference"
                       target="_blank" rel="noopener noreferrer">
                        <Tooltip label="Amadeus Transfer API" position="top" withArrow>
                            <ActionIcon size="lg" variant="default" radius="xl">
                                <IconPlaneArrival style={{width: rem(18), height: rem(18)}} stroke={1.5}/>
                            </ActionIcon>
                        </Tooltip>
                    </a>
                    <a href="https://developers.google.com/maps/documentation/places/web-service/autocomplete"
                       target="_blank" rel="noopener noreferrer">
                        <Tooltip label="Google Places API" position="top" withArrow>
                            <ActionIcon size="lg" variant="default" radius="xl">
                                <IconInputSearch style={{width: rem(18), height: rem(18)}} stroke={1.5}/>
                            </ActionIcon>
                        </Tooltip>
                    </a>
                </Group>
            </div>
        </div>
    );
}