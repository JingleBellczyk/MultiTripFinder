import { Button, Group, Popover, Stack, Text } from '@mantine/core';
import { useState } from 'react';
import styles from "./MultipleCategoriesPicker.module.css";
import "../../styles/globals.css";
import { SearchDTO } from "../../types/SearchDTO";

/**
 * component for adding passengers, adjusted to have multiple categories, but only one also works
 */
interface MultipleCategoriesPickerProps {
    categories: string[];
    dto: SearchDTO;
    onUpdate: (newValues: Partial<SearchDTO>) => void;
}

export function MultipleCategoriesPicker({ categories, dto, onUpdate }: MultipleCategoriesPickerProps) {
    const [counts, setCounts] = useState<Record<string, number>>(
        categories.reduce((acc, category) => ({ ...acc, [category]: 1 }), {}) // Start with 1 for each category
    );

    const initialTotalSelected = categories.length; // Initially, all categories are selected
    const [totalSelected, setTotalSelected] = useState<number>(initialTotalSelected);

    const increment = (category: string): void => {
        setCounts((prev) => {
            const newCount = prev[category] + 1;
            const updatedDiscounts = { ...dto.discounts, [category]: newCount };

            // Increase the totalSelected by 1
            const newTotalSelected = totalSelected + 1;

            onUpdate({ discounts: updatedDiscounts });

            setTotalSelected(newTotalSelected); // Update totalSelected synchronously
            return { ...prev, [category]: newCount };
        });
    };

    const decrement = (category: string): void => {
        setCounts((prev) => {
            const currentCount = prev[category];

            if (currentCount <= 1) {
                return prev; // No change if current count is already 1
            }

            const newCount = currentCount - 1;

            // Create a copy of the existing discounts and update the relevant category
            const updatedDiscounts = { ...dto.discounts, [category]: newCount };

            // Call onUpdate with the new discounts
            onUpdate({ discounts: updatedDiscounts });
            const newTotalSelected = totalSelected - 1;

            setTotalSelected(newTotalSelected); // Update totalSelected synchronously
            return { ...prev, [category]: newCount };
        });
    };

    return (
        <Popover width={300} position="bottom" withArrow shadow="md">
            <Popover.Target>
                <Button className={styles.pinkButton}>
                    Number of passengers {totalSelected}
                </Button>
            </Popover.Target>
            <Popover.Dropdown>
                <Stack>
                    {categories.map((category) => (
                        <Group key={category} style={{ justifyContent: 'space-between', width: '100%' }}>
                            <Text size="md">{category}</Text>
                            <Group style={{ justifyContent: 'flex-end' }}>
                                <Button variant="default" onClick={() => decrement(category)}>-</Button>
                                <Text>{counts[category]}</Text>
                                <Button variant="default" onClick={() => increment(category)}>+</Button>
                            </Group>
                        </Group>
                    ))}
                </Stack>
            </Popover.Dropdown>
        </Popover>
    );
}
