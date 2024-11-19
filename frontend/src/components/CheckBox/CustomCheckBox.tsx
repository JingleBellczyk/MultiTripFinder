import {Checkbox, Flex} from "@mantine/core";


type CustomCheckBoxProps = {
    label: string;
    checked: boolean;
    onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
};

export default function CustomCheckBox({ label, checked, onChange }: CustomCheckBoxProps) {
    return (
        <Flex justify="space-between" style={{ padding: '5px 0' }}>
            <label>{label}</label>
            <Checkbox size="md" checked={checked} onChange={onChange} />
        </Flex>
    );
}
