import {Flex, Text} from "@mantine/core";
import logoSvg from "../../assets/icons/mtfLogo.png";
import classes from "./Logo.module.css";
import React from 'react';

interface LogoProps {
    size: number;
}
export function Logo({size}: LogoProps) {
    return (
        <Flex className={classes.container}>
            <img
                src={logoSvg}
                alt="My custom icon"
                className={classes.img}
                style={{ width: `${size}%`, height: 'auto' }}
            />
            <Text className={classes.text}>Multi Trip Finder</Text>
        </Flex>
    );
}
