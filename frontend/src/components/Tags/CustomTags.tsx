import { Badge, ActionIcon, Flex, Center, TextInput, Box } from '@mantine/core';
import { IconX, IconPlus, IconPencil } from '@tabler/icons-react';
import { useState, useEffect } from 'react';
import { Tag } from "../../types/SearchDTO";
import TagInput from "../Tags/TagInput";
import ButtonModal from "../Modal/ButtonModal";
import styles from './CustomTags.module.css';

const iconSize = 16;
const stroke = 1.3;
const maxTagLength = 20;
const maxTags = 3;

interface CustomTagsProps {
    myTags: Tag[];
    allTags: Tag[];
    onTagRemoveFromGlobalList: (tag: Tag) => void;
    onTagRemoveFromSearch: (index: number, tagName: string) => void;
    onAddTag: (index: number, newName: string) => void;
    index: number;
}

export default function CustomTags({ myTags, allTags, onTagRemoveFromGlobalList, onTagRemoveFromSearch, onAddTag, index }: CustomTagsProps) {
    const [tags, setTags] = useState<Tag[]>(myTags);
    const [showTagInput, setShowTagInput] = useState<boolean>(false);
    const [editValue, setEditValue] = useState<string>('');
    const [editingTag, setEditingTag] = useState<string | null>(null);

    useEffect(() => {
        setTags(myTags);
    }, [myTags]);

    useEffect(() => {
        if (tags.length >= maxTags) {
            setShowTagInput(false);
        }
    }, [tags]);

    const startEditing = (tagName: string) => {
        if (editingTag !== tagName) {
            setEditingTag(tagName);
            setEditValue(tagName);
        }
    };

    const saveEdit = () => {
        if (editingTag && editValue) {
            const updatedName = editValue.toUpperCase().trim();

            // Check for duplicates and length limit
            if (updatedName.length <= maxTagLength && updatedName !== editingTag && !tags.some((tag) => tag.name === updatedName)) {
                const updatedTags = tags.map((tag) =>
                    tag.name === editingTag ? { ...tag, name: updatedName } : tag
                );
                onTagRemoveFromSearch(index, editingTag)
                onAddTag(index, updatedName);
                setTags(updatedTags);
                setEditingTag(null);


            }
        }
    };

    return (
        <>
            {showTagInput && (
                <Box className={styles.inputWrapper}>
                    <TagInput
                        label="Press Enter to submit a tag"
                        list={allTags}
                        onRemoveFromAllTags={onTagRemoveFromGlobalList}
                        onAddTag={onAddTag}
                        value={tags}
                        index={index}
                        onRemoveTagFromSearch={onTagRemoveFromSearch}
                    />
                    <ActionIcon variant="light" onClick={() => setShowTagInput(false)}>
                        <IconX size={iconSize} stroke={stroke} />
                    </ActionIcon>
                </Box>
            )}

            <Center>
                <Flex justify="center" align="center" direction="column" wrap="nowrap" className={styles.badgeContainer}>
                    {!showTagInput &&
                        tags.length > 0 &&
                        tags.map((tag) => (
                            <div key={tag.name} className={styles.badgeWrapper}>
                                {editingTag === tag.name ? (
                                    <TextInput
                                        value={editValue}
                                        onChange={(e) => setEditValue(e.target.value)}
                                        onBlur={() => {
                                            saveEdit();
                                            setEditingTag(null);
                                        }}
                                        onKeyDown={(e) => {
                                            if (e.key === 'Enter') {
                                                saveEdit();
                                                setEditingTag(null);
                                            }
                                        }}
                                        autoFocus
                                        maxLength={maxTagLength}
                                    />
                                ) : (
                                    <Badge
                                        size="md"
                                        variant="light"
                                        onClick={() => startEditing(tag.name)} // Edycja po kliknięciu Badge
                                        rightSection={
                                            <ActionIcon variant="transparent" onClick={(e) => {
                                                e.stopPropagation(); // Unikaj wywoływania edycji podczas usuwania
                                                onTagRemoveFromSearch(index, tag.name);
                                            }}>
                                                <IconX size={iconSize} stroke={stroke} />
                                            </ActionIcon>
                                        }
                                    >
                                        {tag.name}
                                    </Badge>
                                )}
                            </div>
                        ))}
                </Flex>

                {tags.length < maxTags && !showTagInput && !editingTag &&(
                    <div className={styles.plusButton}>
                        <ActionIcon variant="light" onClick={() => setShowTagInput(true)}>
                            <IconPlus size={iconSize} stroke={stroke} />
                        </ActionIcon>
                    </div>
                )}
            </Center>
        </>
    );
}
