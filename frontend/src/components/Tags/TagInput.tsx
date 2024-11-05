import { CheckIcon, Combobox, Group, Pill, PillsInput, useCombobox, ActionIcon } from "@mantine/core";
import { useState, useEffect, useRef } from "react";
import { Tag } from "../../types/SearchDTO";
import { IconX } from '@tabler/icons-react';
import ButtonModal from "../Modal/ButtonModal";

type FilterProps = {
    list: Tag[];
    label: string;
    value: Tag[];
    onRemoveFromAllTags: (tagToRemove: Tag, isRemove?: boolean) => void;
    onRemoveTagFromSearch: (index: number, tagName: string) => void;
    onAddTag: (index: number, tagName: string) => void;
    index: number;
};

export default function TagInput({ list, label, value, onRemoveFromAllTags, onRemoveTagFromSearch, onAddTag, index }: FilterProps) {
    const combobox = useCombobox({
        onDropdownClose: () => combobox.resetSelectedOption(),
        onDropdownOpen: () => combobox.updateSelectedOptionIndex('active'),
    });

    const [search, setSearch] = useState<string>('');
    const inputRef = useRef<HTMLInputElement | null>(null);

    useEffect(() => {
        if (inputRef.current) {
            inputRef.current.focus();
        }
    }, []);

    const handleValueSelect = (val: string) => {
        // Check if the tag already exists in the selected values
        if (value.some((tag) => tag.name === val)) {
            return; // If the tag exists, do nothing
        }

        onAddTag(index, val); // Add the tag if it doesn't exist
        setSearch(''); // Clear the search after adding
    };

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSearch(event.currentTarget.value);
        combobox.updateSelectedOptionIndex();
    };

    const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter' && search.trim()) {
            handleValueSelect(search.trim());
            event.preventDefault();
        }
    };

    const values = value.map((item) => (
        <Pill size="md" key={item.name} withRemoveButton onRemove={() => onRemoveTagFromSearch(index, item.name)}>
            {item.name}
        </Pill>
    ));

    const options = list
        .filter((item) => item.name.toLowerCase().includes(search.trim().toLowerCase()))
        .map((item) => (
            <Combobox.Option value={item.name} key={item.name}>
                <Group
                    gap="s"
                    style={{ display: 'flex', justifyContent: 'space-between', width: '100%' }}
                >
                    {value.some((tag) => tag.name === item.name) && <CheckIcon size={12} />}
                    <span>{item.name}</span>
                    <Group onClick={(e) => e.stopPropagation()}>
                        <ButtonModal
                            tagToDelete={item}
                            onDeleteConfirm={onRemoveFromAllTags}
                        />
                    </Group>
                </Group>
            </Combobox.Option>
        ));

    return (
        <Combobox store={combobox} onOptionSubmit={(val) => handleValueSelect(val)}>
            <Combobox.DropdownTarget>
                <PillsInput onClick={() => combobox.openDropdown()} label={label}>
                    <Pill.Group>
                        {values}
                        <Combobox.EventsTarget>
                            <PillsInput.Field
                                ref={inputRef}
                                onFocus={() => combobox.openDropdown()}
                                value={search}
                                placeholder="Enter tag"
                                maxLength={20}
                                onChange={handleInputChange}
                                onKeyDown={handleKeyDown}
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
