import {Button, Divider, Grid, GridCol, MantineProvider, NativeSelect, NumberInput, Text} from '@mantine/core';
import {HeaderSearch} from "../components/HeaderSearch/HeaderSearch";
import {DndListHandle} from "../components/DndListHandle/DndListHandle";
import {GridComponent} from "../components/GridComponent/GridComponent";
import {DISCOUNTS, MEANS_OF_TRANSPORT, MINIMIZED_CRITERION, LINKS, GRID_ITEMS_SEARCH} from "../constants/constants";
import {DatePicker} from "../components/DatePicker/DatePicker";
import {MultipleCategoriesPicker} from "../components/MultipleCategoriesPicker/MultipleCategoriesPicker";
import RadioComponent from "../components/RadioComponent/RadioComponent";
import {ValidationErrors} from '../utils/placesTimeUtils'
import {useMaxTotalTime} from '../hooks/useMaxTotalTime'
import {validateForm} from '../utils/placesTimeUtils'
import "../styles/globals.css"
import styles from "./SearchPage.module.css"
import {SearchDTO, SearchDTOPost} from "../types/SearchDTO"
import React, {useState, useRef} from 'react';
import {postSearch} from "../api/services/searchService";

const span = 12;
const columnNumber = 1 / 5;

const gridItemsMultiplier: number[] = [
    columnNumber * 2,
    columnNumber,
    columnNumber,
    columnNumber,
];

export function SearchPage() {
    return (
        SearchFunction()
    );
}

function SearchFunction() {
    const [meanOfTransport, setMeanOfTransport] = useState(MEANS_OF_TRANSPORT[0]);
    const [selectedDate, setSelectedDate] = useState<Date | null>(null); // Track the date
    const [selectedCriterion, setSelectedCriterion] = useState('PRICE');
    const {maxTotalTime, handleMaxTotalTimeChange} = useMaxTotalTime(24);

    const [errors, setErrors] = useState<ValidationErrors>({
        dateError: null,
        placesTimeError: null,
        startPlaceError: null,
        endPlaceError: null,
    });

    // Initialize the SearchDTO state as an empty object
    const [searchDto, setSearchDto] = useState<SearchDTO>({
        placesTime: [],
        start: "",
        end: "",
        maxTotalTime: maxTotalTime,
        transport: null,
        startDate: null,
        discounts: {},
        preferredCriteria: selectedCriterion
    });

    const handlePassengersChange = (newValues: Partial<SearchDTO>) => {
        // Ensure discounts is always defined
        const updatedDto = {
            ...searchDto,
            discounts: {
                ...searchDto.discounts,
                ...newValues.discounts, // Merge the updated discounts
            },
        };
        setSearchDto(updatedDto);
    };

    const handleTransportChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        setMeanOfTransport(event.target.value); // Update state with the selected value
    };

    const handleDateChange = (date: Date | null) => {
        setSelectedDate(date);
    };

    const handleSubmit = async () => {
        const selectedTransport = meanOfTransport === MEANS_OF_TRANSPORT[0] ? null : meanOfTransport;
        const numberOfPassnegers = searchDto.discounts["Adult"];
        const placesTimeList = searchDto.placesTime;
        const startPlace = searchDto.start;
        const endPlace = searchDto.end;
        const preferredCriteria = selectedCriterion

        // validation of errors
        const newErrors = validateForm(selectedDate, placesTimeList, startPlace, endPlace);
        setErrors(newErrors);

        // if errors, break
        const hasErrors = Object.values(newErrors).some((error) => error !== null);
        if (hasErrors) {
            return;
        }

        // concatenate startPlace, inner places, endPlacce
        const goalPlacesTime = [
            {place: startPlace, hoursToSpend: 0}, // Dodaj start na początku
            ...placesTimeList,                       // Dodaj miejsca docelowe
            {place: endPlace, hoursToSpend: 0}     // Dodaj end na końcu
        ];

        const dto: SearchDTOPost = {
            places: goalPlacesTime,
            passengersNumber: numberOfPassnegers,
            hoursToSpend: maxTotalTime,
            startDate: selectedDate,
            preferredTransport: selectedTransport,
            preferredCriteria: preferredCriteria
        };
        console.log(JSON.stringify(dto));

        try {
            const response = await postSearch(dto);
            console.log("Response data:", response);
        } catch (error) {
            console.error('Error during POST request:', error);
        }
    };

    // grape color - defines small elements like slider and checkbox
    return (
        <>
            <MantineProvider theme={{primaryColor: 'grape'}}>
                <HeaderSearch links={LINKS}></HeaderSearch>
                <GridComponent gridItems={GRID_ITEMS_SEARCH} gridItemsMultiplier={gridItemsMultiplier}></GridComponent>
                <div style={{width: '80%', margin: '0 auto'}}>
                    <Grid
                        className={styles.gridCol}
                        type="container"
                        breakpoints={{xs: '100px', sm: '200px', md: '300px', lg: '400px', xl: '500px'}}
                    >
                        {/*column with places and time*/}
                        <GridCol key={0} span={span * gridItemsMultiplier[0]}>
                            <DndListHandle
                                dto={searchDto}
                                onUpdate={(updatedDto) => setSearchDto(updatedDto)}
                                placesTimeError={errors.placesTimeError}
                                startPlaceError={errors.startPlaceError}
                                endPlaceError={errors.endPlaceError}
                            />
                        </GridCol>

                        {/*column with passengers*/}
                        <GridCol key={2} span={span * gridItemsMultiplier[1]}>
                            <MultipleCategoriesPicker
                                categories={DISCOUNTS}
                                dto={searchDto}
                                onUpdate={handlePassengersChange}
                            ></MultipleCategoriesPicker>
                            <Button size={"xl"} className={styles.pinkButton} onClick={handleSubmit}>Search!</Button>
                        </GridCol>

                        {/*column with transport*/}
                        <GridCol key={3} span={span * gridItemsMultiplier[2]}>
                            <NativeSelect
                                id="meanOfTransport"
                                size="md"
                                radius="md"
                                data={MEANS_OF_TRANSPORT}
                                defaultValue={MEANS_OF_TRANSPORT.at(0)}
                                onChange={handleTransportChange}
                            />
                        </GridCol>

                        {/*column with date, criterion and maxTotalDays*/}
                        <GridCol key={4} span={span * gridItemsMultiplier[3]}>
                            <Text mb={"sm"} style={{textAlign: 'left'}}>Start date</Text>
                            <DatePicker onDateChange={handleDateChange}/>
                            {errors.dateError &&
                                <Text color="red"
                                      size="sm">{errors.dateError}</Text>} {/* Display error if date is null */}
                            <Divider my="md"/>

                            <Text mb={"sm"} style={{textAlign: 'left'}}>What do you want to minimize?</Text>
                            <RadioComponent
                                labels={MINIMIZED_CRITERION}
                                selectedValue={selectedCriterion}
                                onChange={setSelectedCriterion}
                            />
                            <Divider my="md"/>

                            <Text mb={"sm"} style={{textAlign: 'left'}}>Maximum total days number</Text>
                            <NumberInput
                                size="md"
                                name={"maxTotalTime"}
                                min={1}
                                max={365}
                                onChange={handleMaxTotalTimeChange}
                                defaultValue={1}
                            />
                        </GridCol>
                    </Grid>
                </div>
            </MantineProvider>
        </>
    );
}