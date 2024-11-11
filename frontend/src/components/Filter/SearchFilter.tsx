import { useState } from 'react';
import { Title, Checkbox, Box, Flex, PillsInput, Pill, Combobox, CheckIcon, Group, useCombobox, Button, Stack, Space, Center, Text } from '@mantine/core';
import '@mantine/dates/styles.css';
import { DatePickerInput } from '@mantine/dates';
import {SavedSearchDTO, Tag} from "../../types/SearchDTO";

type ResetButtonProps = {
    onReset: () => void;
}

type CustomCheckBoxProps = {
    label: string;
    checked: boolean;
    onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

type FilterProps = {
    list: string[];
    label: string;
    value: string[];
    onValueChange: (val: string, isRemove?: boolean) => void;
}


type SearchFilterProps = {
    searches: SavedSearchDTO[];
    tags: Tag[];
};

function ShowResults() {
    return (
        <Button onClick={() => {}} size="md" radius="lg" variant="filled" fullWidth>
            Show results
        </Button>
    );
}

function ResetButton({ onReset }: ResetButtonProps) {
    return (
        <Button onClick={onReset} radius="lg" size="md" variant="outline" fullWidth>
            Reset
        </Button>
    );
}

function CustomCheckBox({ label, checked, onChange }: CustomCheckBoxProps) {
    return (
        <Flex justify="space-between" style={{ padding: '5px 0' }}>
            <label>{label}</label>
            <Checkbox size="md" checked={checked} onChange={onChange} />
        </Flex>
    );
}

function Filter({ list, label, value, onValueChange }: FilterProps) {
    const combobox = useCombobox({
        onDropdownClose: () => combobox.resetSelectedOption(),
        onDropdownOpen: () => combobox.updateSelectedOptionIndex('active'),
    });

    const [search, setSearch] = useState<string>('');

    const handleValueSelect = (val: string) => {
        onValueChange(val);
    };

    const values = value.map((item) => (
        <Pill size="md" key={item} withRemoveButton onRemove={() => onValueChange(item, true)}>
            {item}
        </Pill>
    ));

    const options = list
        .filter((item) => item.toLowerCase().includes(search.trim().toLowerCase()))
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
                                value={search}
                                placeholder="Search..."
                                onChange={(event) => {
                                    combobox.updateSelectedOptionIndex();
                                    setSearch(event.currentTarget.value);
                                }}
                                onKeyDown={(event) => {
                                    if (event.key === 'Backspace' && search.length === 0) {
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

export default function SearchFilter({ tags, searches }: SearchFilterProps) {
    const [airplaneChecked, setAirplaneChecked] = useState<boolean>(false);
    const [busChecked, setBusChecked] = useState<boolean>(false);
    const [trainChecked, setTrainChecked] = useState<boolean>(false);
    const [durationChecked, setDurationChecked] = useState<boolean>(false);
    const [priceChecked, setPriceChecked] = useState<boolean>(false);
    const [selectedTags, setSelectedTags] = useState<string[]>([]);
    const [dates, setDates] = useState<[Date | null, Date | null]>([null, null]);

    const resetFilters = () => {
        setAirplaneChecked(false);
        setBusChecked(false);
        setTrainChecked(false);
        setDurationChecked(false);
        setPriceChecked(false);
        setSelectedTags([]);
        setDates([null, null]);
    };

    const tagNames = tags.map(tag => tag.name);

    return (
        <Box
            style={{
                backgroundColor: 'white',
                color: 'black',
                padding: '20px',
                borderRadius: '20px',
            }}
        >
            <Center>
                <Title order={4}>
                    Filter
                </Title>
            </Center>
            <Text fw={500} size="sm">Transport</Text>
            <CustomCheckBox
                label="Airplane"
                checked={airplaneChecked}
                onChange={(event) => setAirplaneChecked(event.currentTarget.checked)}
            />
            <CustomCheckBox
                label="Bus"
                checked={busChecked}
                onChange={(event) => setBusChecked(event.currentTarget.checked)}
            />
            <CustomCheckBox
                label="Train"
                checked={trainChecked}
                onChange={(event) => setTrainChecked(event.currentTarget.checked)}
            />
            <Text fw={500} size="sm">Optimalization Criteria</Text>
            <CustomCheckBox
                label="Duration"
                checked={durationChecked}
                onChange={(event) => setDurationChecked(event.currentTarget.checked)}
            />
            <CustomCheckBox
                label="Price"
                checked={priceChecked}
                onChange={(event) => setPriceChecked(event.currentTarget.checked)}
            />
            <Filter
                list={tagNames}
                label="Choose tags"
                value={selectedTags}
                onValueChange={(val, isRemove) => {
                    if (isRemove) {
                        setSelectedTags((current) => current.filter((item) => item !== val));
                    } else {
                        setSelectedTags((current) => (current.includes(val) ? current : [...current, val]));
                    }
                }}
            />
            <DatePickerInput
                type="range"
                label="Pick dates range"
                placeholder="Pick dates range"
                value={dates}
                onChange={setDates}
            />
            <Space h="lg" />
            <Stack align="center" justify="center">
                <ShowResults />
                <ResetButton onReset={resetFilters} />
            </Stack>
        </Box>
    );
}