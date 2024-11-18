import { Input, InputBase, Combobox, useCombobox } from '@mantine/core';
import {User} from "../../types/UserDTO";


type ComboBoxProps = {
    options: string[];
    value: string | null;
    setValue: React.Dispatch<React.SetStateAction<string | null>>
};

export default function CustomCombobox({options, value, setValue} : ComboBoxProps ) {
    const combobox = useCombobox({
        onDropdownClose: () => combobox.resetSelectedOption(),
    });

    const allOptions = options.map((item) => (
        <Combobox.Option value={item} key={item}>
            {item}
        </Combobox.Option>
    ));

    return (
        <Combobox
            store={combobox}
            onOptionSubmit={(val) => {
                setValue(val);
                combobox.closeDropdown();
            }}
        >
            <Combobox.Target>
                <InputBase
                    component="button"
                    type="button"
                    pointer
                    rightSection={<Combobox.Chevron />}
                    rightSectionPointerEvents="none"
                    onClick={() => combobox.toggleDropdown()}
                >
                    {value || <Input.Placeholder>Pick value</Input.Placeholder>}
                </InputBase>
            </Combobox.Target>

            <Combobox.Dropdown>
                <Combobox.Options>{allOptions}</Combobox.Options>
            </Combobox.Dropdown>
        </Combobox>
    );
}
