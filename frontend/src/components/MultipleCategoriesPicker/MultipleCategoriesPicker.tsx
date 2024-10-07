import {Button, Group, Popover, Stack, Text} from '@mantine/core';
import {useState} from 'react';
import styles from "./MultipleCategoriesPicker.module.css";
import "../../styles/globals.css"

interface MultipleCategoriesPickerProps {
    categories: string[];
}

export function MultipleCategoriesPicker({categories}: MultipleCategoriesPickerProps) {

    /**
     * hooks for counting specific passengers
     * @author Agata
     */
    const [counts, setCounts] = useState<Record<string, number>>(
        categories.reduce((acc, category) => ({...acc, [category]: 0}), {})
    );

    const [totalSelected, setTotalSelected] = useState<number>(0);

    /**
     * function that increments total number of passengers
     * @param category
     */
    const increment = (category: string): void => {
        setCounts((prev) => {
            const newCount = prev[category] + 1;
            setTotalSelected((total) => total + 1); // Zwiększenie całkowitej liczby
            return {...prev, [category]: newCount};
        });
    };

    /**
     * function that decrements total number of passengers
     * @param category
     */
    const decrement = (category: string): void => {
        setCounts((prev) => {
            const newCount = prev[category] > 0 ? prev[category] - 1 : 0;
            setTotalSelected((total) => (newCount < prev[category] ? total - 1 : total)); // Zmniejszenie całkowitej liczby
            return {...prev, [category]: newCount};
        });
    };

    return (
        <Popover width={300} position="bottom" withArrow shadow="md">
            <Popover.Target>
                <Button className={styles.pinkButton}>
                    Number of passengers {totalSelected > 0 && `(${totalSelected})`}
                </Button>
            </Popover.Target>
            <Popover.Dropdown>
                <Stack>
                    {categories.map((category) => (
                        <Group key={category} style={{justifyContent: 'space-between', width: '100%'}}>
                            <Text size="md">
                                {category}
                            </Text>
                            <Group
                                style={{justifyContent: 'flex-end'}}>
                                <Button variant="default" onClick={() => decrement(category)}>
                                    -
                                </Button>
                                <Text>
                                    {counts[category]}
                                </Text>
                                <Button variant="default" onClick={() => increment(category)}>
                                    +
                                </Button>
                            </Group>
                        </Group>
                    ))}
                </Stack>
            </Popover.Dropdown>
        </Popover>
    );
}
