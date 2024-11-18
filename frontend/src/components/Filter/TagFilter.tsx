import {CheckIcon, Combobox, Group, Pill, PillsInput, useCombobox} from "@mantine/core";
import {useState} from "react";

type FilterProps = {
    list: string[];
    label: string;
    value: string[];
    onValueChange: (val: string, isRemove?: boolean) => void;
};

export default function TagFilter({ list, label, value, onValueChange }: FilterProps) {
    const combobox = useCombobox({
        onDropdownClose: () => combobox.resetSelectedOption(),
        onDropdownOpen: () => combobox.updateSelectedOptionIndex('active'),
    });

    const [element, setElement] = useState<string>('');

    const handleValueSelect = (val: string) => {
        onValueChange(val);
    };

    const values = value.map((item) => (
        <Pill size="md" key={item} withRemoveButton onRemove={() => onValueChange(item, true)}>
            {item}
        </Pill>
    ));

    const options = list
        .filter((item) => item.toLowerCase().includes(element.trim().toLowerCase()))
        .map((item) => (
            <Combobox.Option value={item} key={item} active={value.includes(item)}>
                <Group gap="s">
                    {value.includes(item) ? <CheckIcon size={12} /> : null}
                    <span>{item}</span>
                </Group>
            </Combobox.Option>
        ));

    return (
        <Combobox store={combobox} onOptionSubmit={handleValueSelect}>
            <Combobox.DropdownTarget>
                <PillsInput onClick={() => combobox.openDropdown()} label={label}>
                    <Pill.Group>
                        {values}
                        <Combobox.EventsTarget>
                            <PillsInput.Field
                                onFocus={() => combobox.openDropdown()}
                                onBlur={() => combobox.closeDropdown()}
                                value={element}
                                placeholder="Search..."
                                onChange={(event) => {
                                    combobox.updateSelectedOptionIndex();
                                    setElement(event.currentTarget.value);
                                }}
                                onKeyDown={(event) => {
                                    if (event.key === 'Backspace' && element.length === 0) {
                                        event.preventDefault();
                                        onValueChange(value[value.length - 1]);
                                    }
                                }}
                            />
                        </Combobox.EventsTarget>
                    </Pill.Group>
                </PillsInput>
            </Combobox.DropdownTarget>
            <Combobox.Dropdown>
                <Combobox.Options style={{ maxHeight: 150, overflowY: 'auto' }}>
                    {options.length > 0 ? options : <Combobox.Empty>Nothing found...</Combobox.Empty>}
                </Combobox.Options>
            </Combobox.Dropdown>
        </Combobox>
    );
}