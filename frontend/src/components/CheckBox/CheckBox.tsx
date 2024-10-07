import {IconMinus, IconPlus} from '@tabler/icons-react';
import {Badge, CloseButton, Container, NativeSelect, Text} from "@mantine/core";
import {DISCOUNTS} from "../../constants/constants";
import {useState} from 'react';

/**
 * not used, but it's a pill with number, + and -
 * @constructor
 */
export function CheckBox() {
    const [selectedDiscount, setSelectedDiscount] = useState(DISCOUNTS[0]);
    const [count, setCount] = useState(2); // Assume 2 people are added as default

    const handleSelectChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setSelectedDiscount(event.currentTarget.value);
    };

    const handleIncrement = () => {
        setCount(count + 1);
    };
    const handleDecrement = () => {
        setCount(count > 0 ? count - 1 : 0);
    };

    // id, number
    return (<Container>
        <div style={{display: 'flex', alignItems: 'center', gap: '16px'}}>
            {/* NativeSelect dropdown for selecting a discount type */}
            <NativeSelect
                size="sm"
                radius="md"
                data={DISCOUNTS}
                value={selectedDiscount}
                onChange={handleSelectChange}
            />

            <Badge
                style={{display: 'flex', alignItems: 'center', gap: '8px'}}
                rightSection={
                    <div style={{display: 'flex', gap: '4px'}}>
                        <CloseButton
                            size="sm"
                            onClick={handleDecrement} // Decrease count
                            icon={<IconMinus size={18} stroke={1.5}/>} // Minus icon for decrement
                        />
                        <CloseButton
                            size="sm"
                            onClick={handleIncrement} // Increase count
                            icon={<IconPlus size={18} stroke={1.5}/>} // Plus icon for increment
                        />
                    </div>
                }
                variant="filled"
                color="plum"
            >
                <Text>
                    {count}
                </Text>
            </Badge>
        </div>
    </Container>);
}
