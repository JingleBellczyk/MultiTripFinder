import { Badge, ActionIcon, Flex, Center, TextInput, Box } from '@mantine/core';
import {IconX, IconPlus, IconCheck} from '@tabler/icons-react';
import { useState, useEffect } from 'react';
import {SavedTag, Tag} from "../../types/SearchDTO";
import TagInput from "../Tags/TagInput";
import styles from './CustomTags.module.css';
import {ICON_SIZE, MAX_TAG_LENGTH, SERVER, STROKE} from "../../constants/constants";
import axios from "axios";

const maxTags = 3;

interface CustomTagsProps {
    myTags: Tag[];
    allTags: SavedTag[];
    onTagRemoveFromGlobalList: (tag: SavedTag) => void;
    onTagEditGlobalList: (tagToEdit: SavedTag, newTagName: string) => Promise<void>;
    onTagRemove: (index: number, tagName: string) => void;
    onEditTag: (newTag: string, oldTag: string, index: number) => Promise<void>;
    onAddTag: (index: number, newName: string) => void;
    setIsModalOpen: (open: boolean) => void
    index: number;
}

export default function CustomTags({ myTags, allTags, onTagRemoveFromGlobalList, onTagEditGlobalList, onTagRemove, onEditTag, onAddTag, index, setIsModalOpen }: CustomTagsProps) {
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
    }, [myTags]);

    const startEditing = (tagName: string) => {
        if (editingTag !== tagName) {
            setEditingTag(tagName);
            setEditValue(tagName);
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
                        onEditTagInList={onTagEditGlobalList}
                        onAddTag={onAddTag}
                        value={tags}
                        index={index}
                        onRemoveTag={onTagRemove}
                        setIsModalOpen={setIsModalOpen}
                    />
                    <ActionIcon size="sm" variant="light" onClick={() => setShowTagInput(false)}>
                        <IconCheck size={ICON_SIZE} stroke={STROKE} />
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
                                        onBlur={async () => {
                                            await onEditTag(editingTag, editValue, index);
                                            setEditingTag(null);
                                        }}
                                        onKeyDown={async (e) => {
                                            if (e.key === 'Enter') {
                                                await onEditTag(editingTag, editValue, index);
                                                setEditingTag(null);
                                            }
                                        }}
                                        autoFocus
                                        maxLength={MAX_TAG_LENGTH}
                                    />
                                ) : (
                                    <Badge
                                        size="md"
                                        variant="light"
                                        onClick={() => startEditing(tag.name)} // Edycja po kliknięciu Badge
                                        rightSection={
                                            <ActionIcon variant="transparent" onClick={(e) => {
                                                e.stopPropagation(); // Unikaj wywoływania edycji podczas usuwania
                                                onTagRemove(index, tag.name);
                                            }}>
                                                <IconX size={ICON_SIZE} stroke={STROKE} />
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
                        <ActionIcon size="sm" variant="light" onClick={() => setShowTagInput(true)}>
                            <IconPlus size={ICON_SIZE} stroke={STROKE} />
                        </ActionIcon>
                    </div>
                )}
            </Center>
        </>
    );
}
