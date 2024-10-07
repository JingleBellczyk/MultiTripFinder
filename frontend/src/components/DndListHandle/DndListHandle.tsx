import cx from 'clsx';
import {Button, CloseButton, Input, rem} from '@mantine/core';
import {useListState} from '@mantine/hooks';
import {DragDropContext, Draggable, Droppable} from '@hello-pangea/dnd';
import {IconGripVertical} from '@tabler/icons-react';
import classes from './DndListHandle.module.css';
import {SliderComponent} from '../SliderComponent/SliderComponent';
import React from 'react';

// Initial data can be an empty array
const initialData = [
    {id: '1', name: 'Place 1'},
    {id: '2', name: 'Place 2'},
];
/*
    handable list
 */
export function DndListHandle() {

    // State to handle the list of places
    const [state, handlers] = useListState(initialData);

    // Function to add a new place to the list
    const addPlace = () => {
        const newId = `${state.length + 1}`;
        handlers.append({id: newId, name: `Place ${newId}`});
    };

    // Function to remove a place by index
    const removePlace = (index: number) => {
        handlers.remove(index);
    };

    // Draggable items mapped from state
    const items = state.map((item, index) => (
        <Draggable key={item.id} index={index} draggableId={item.id}>
            {(provided, snapshot) => (
                <div
                    className={cx(classes.item, {[classes.itemDragging]: snapshot.isDragging})}
                    ref={provided.innerRef}
                    {...provided.draggableProps}
                >
                    <div {...provided.dragHandleProps} className={classes.dragHandle}>
                        <IconGripVertical style={{width: rem(18), height: rem(18)}} stroke={1.5}/>
                    </div>

                    <Input size="xs" radius="md" placeholder={item.name}/>
                    <SliderComponent/>

                    {/* CloseButton to remove the item */}
                    <CloseButton onClick={() => removePlace(index)}/>
                </div>
            )}
        </Draggable>
    ));

    return (
        <div>
            <DragDropContext
                onDragEnd={({destination, source}) => {
                    if (!destination) return; // Prevent invalid reordering
                    handlers.reorder({from: source.index, to: destination.index});
                }}
            >
                <Droppable droppableId="dnd-list" direction="vertical">
                    {(provided) => (
                        <div {...provided.droppableProps} ref={provided.innerRef}>
                            {items}
                            {provided.placeholder}
                        </div>
                    )}
                </Droppable>
            </DragDropContext>

            {/* Button to add new place */}
            <Button onClick={addPlace} className={classes.pinkButton}>
                Add Place
            </Button>
        </div>
    );
}
