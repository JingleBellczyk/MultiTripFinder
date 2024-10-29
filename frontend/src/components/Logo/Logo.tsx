import {Flex, Text} from "@mantine/core";
import logoSvg from "../../assets/icons/mtfLogo.png";
import classes from "./Logo.module.css";

export function Logo() {
    return (
        <Flex className={classes.container}>
            <img src={logoSvg} alt="My custom icon" className={classes.img}/>
            <Text className={classes.text}>Multi Trip Finder</Text>
        </Flex>
    );
}
