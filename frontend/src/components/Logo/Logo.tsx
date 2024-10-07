import {Flex, Text} from "@mantine/core";
import logoSvg from "../../assets/icons/logo192.png";
import classes from "./Logo.module.css"

export function Logo(){
    return(
    <Flex className={classes.container}>
        <img src={logoSvg} alt="My custom icon" width={28} height={28}/>
        <Text c="white">MultiTripFinder</Text>
    </Flex>
    );
}
