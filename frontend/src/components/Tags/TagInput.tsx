import {CheckIcon, Combobox, Container, Group, Pill, PillsInput, useCombobox} from "@mantine/core";
import React, { useState, useEffect, useRef } from "react";
import {SavedTag, Tag } from "../../types/SearchDTO";
import RemoveModal from "../Modal/RemoveModal";
import EditModal from "../Modal/EditModal";
import {MAX_TAG_LENGTH} from "../../constants/constants";

type TagInputProps = {
    list: SavedTag[];
    label: string;
    value: Tag[];
    onRemoveFromAllTags: (tagToRemove: SavedTag, isRemove?: boolean) => void;
    onEditTagInList: (tagToEdit: SavedTag, newTagName: string) => Promise<void>;
    onRemoveTag: (index: number, tagName: string) => void;
    onAddTag: (index: number, tagName: string) => void;
    setIsModalOpen: (open: boolean) => void;
    index: number;
};

export default function TagInput({ list, label, value, onRemoveFromAllTags, onEditTagInList, onRemoveTag, onAddTag, setIsModalOpen, index }: TagInputProps) {
    const combobox = useCombobox({
        onDropdownClose: () => combobox.resetSelectedOption(),
        onDropdownOpen: () => combobox.updateSelectedOptionIndex('active'),
    });

    const [element, setElement] = useState<string>('');
    const inputRef = useRef<HTMLInputElement | null>(null);

    const handleModalOpen = () => {
        combobox.closeDropdown();
    };

    useEffect(() => {
        if (inputRef.current) {
            inputRef.current.focus();
        }
    }, []);

    const handleValueSelect = async (val: string) => {
        if (value.some((tag) => tag.name === val)) {
            return;
        }
        if (value.length >= 3) {
            alert("You can only add up to 3 tags.");
            return;
        }
        await onAddTag(index, val);
        setElement('');
    };

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setElement(event.currentTarget.value);
        combobox.updateSelectedOptionIndex();
    };

    const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === 'Enter' && element.trim()) {
            handleValueSelect(element.trim());
            event.preventDefault();
        }
    };

    const values =value.map((item) => (
        <Pill size="md" key={item.name} withRemoveButton onRemove={async () => onRemoveTag(index, item.name)}>
            {item.name}
        </Pill>
    ));

    const options = list
        .filter((item) => item.name.includes(element))
        .map((item) => (
            <Combobox.Option value={item.name} key={item.name}>
                <Group
                    style={{
                        justifyContent: 'space-between',
                        width: '100%',
                        gap: 0
                    }}
                >
                    {value.some((tag) => tag.name === item.name) && <CheckIcon size={10} />}
                    <span>{item.name}</span>
                    <Group
                        onClick={(e) => e.stopPropagation()}
                        style={{
                            gap: '2px',
                        }}
                    >
                        <EditModal
                            tagToEdit={item}
                            onEditConfirm={onEditTagInList}
                            setIsModalOpen={setIsModalOpen}
                            onModalOpen={handleModalOpen}
                        />
                        <RemoveModal
                            tagToDelete={item}
                            onDeleteConfirm={onRemoveFromAllTags}
                            setIsModalOpen={setIsModalOpen}
                            onModalOpen={handleModalOpen}
                        />
                    </Group>
                </Group>
            </Combobox.Option>
        ));


    return (
        <Container style={{ width: '100%', paddingLeft: 0, paddingRight: 0 }}>
            <Combobox store={combobox} onOptionSubmit={(val) => handleValueSelect(val)}>
                <Combobox.DropdownTarget>
                    <PillsInput onClick={() => combobox.openDropdown()} label={label}>
                        <Pill.Group>
                            {values}
                            <Combobox.EventsTarget>
                                <PillsInput.Field
                                    ref={inputRef}
                                    onFocus={() => combobox.openDropdown()}
                                    value={element}
                                    placeholder="Enter tag"
                                    maxLength={MAX_TAG_LENGTH}
                                    onChange={handleInputChange}
                                    onKeyDown={handleKeyDown}
                                    disabled={value.length>=3}
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
        </Container>

    );
}
