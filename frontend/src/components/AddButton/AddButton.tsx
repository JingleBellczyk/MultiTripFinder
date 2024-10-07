import classes from './AddButton.module.css';
import {ReactNode} from 'react';
import {Button} from '@mantine/core';


export function AddButton({children}: { children: ReactNode }) {
//style props - dla ogółu, nie dla konkretnych komponentów
    return (
        <Button
            className={classes.pinkButton}
            size={"md"}
        >
            {children}
        </Button>
    );
}