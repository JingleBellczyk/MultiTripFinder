import React from 'react';
import {Checkbox, Divider, Grid, GridCol, MantineProvider, NativeSelect, Text, Button} from '@mantine/core';
import {HeaderSearch} from "../components/HeaderSearch/HeaderSearch";
import {DndListHandle} from "../components/DndListHandle/DndListHandle";
import {GridComponent} from "../components/GridComponent/GridComponent";
import {DISCOUNTS, MEANS_OF_TRANSPORT} from "../constants/constants";
import {DatePicker} from "../components/DatePicker/DatePicker";
import {MultipleCategoriesPicker} from "../components/MultipleCategoriesPicker/MultipleCategoriesPicker";
import "../styles/globals.css"
import styles from "./SearchPage.module.css"
import {AddButton} from "../components/AddButton/AddButton";

const gridItems: string[] = [
    'Choose places and number of days',
    'Choose passengers',
    'Preferable transport',
    'Additional info'
];

export function SearchPage() {
    return (
        Demo()
    );
}

function Demo() {

    const span = 12;
    const columnNumber = 1 / 5;

    const gridItemsMultiplier: number[] = [
        columnNumber * 2,
        columnNumber,
        columnNumber,
        columnNumber,
    ];
    // grape color - defines small elements like slider and checkbox
    return (
        <>
            <MantineProvider theme={{primaryColor: 'grape'}}>
                <HeaderSearch></HeaderSearch>
                <GridComponent gridItems={gridItems} gridItemsMultiplier={gridItemsMultiplier}></GridComponent>
                <div style={{height: '100vh', width: '80%', margin: '0 auto'}}>
                    <Grid
                        className={styles.gridCol}
                        type="container"
                        breakpoints={{xs: '100px', sm: '200px', md: '300px', lg: '400px', xl: '500px'}}
                    >
                        <GridCol key={0} span={span * gridItemsMultiplier[0]}>
                            <DndListHandle></DndListHandle>
                        </GridCol>
                        <GridCol key={2} span={span * gridItemsMultiplier[1]}>
                            <MultipleCategoriesPicker categories={DISCOUNTS}></MultipleCategoriesPicker>
                            <Button size={"xl"} className={styles.pinkButton}>Search!</Button>
                        </GridCol>
                        <GridCol key={3} span={span * gridItemsMultiplier[2]}>
                            <NativeSelect
                                size="md"
                                radius="md"
                                data={MEANS_OF_TRANSPORT}
                                defaultValue={MEANS_OF_TRANSPORT.at(0)}
                            />
                        </GridCol>
                        <GridCol key={4} span={span * gridItemsMultiplier[3]}>
                            <Text mb={"sm"} style={{textAlign: 'left'}}>Start date</Text>
                            <DatePicker/>
                            <Divider my="md"/>
                            <Checkbox
                                defaultChecked
                                size="md"
                                label="First class"
                                className={styles.checkbox}
                            />
                            <Checkbox
                                className={styles.checkbox}
                                defaultChecked
                                size="md"
                                label="Share this search on my profile"
                            />
                            <Divider my="md"/>
                        </GridCol>
                    </Grid>
                </div>
            </MantineProvider>
        </>
    );
}