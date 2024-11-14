import {
    Box,
    Button,
    Divider,
    Grid,
    GridCol,
    LoadingOverlay,
    MantineProvider,
    NativeSelect,
    NumberInput,
    Text
} from '@mantine/core';
import {HeaderSearch} from "../../components/HeaderSearch/HeaderSearch";
import {Footer} from "../../components/Footer/Footer";
import { useLocation } from 'react-router-dom';
import {DndListHandle} from "../../components/DndListHandle/DndListHandle";
import {GridComponent} from "../../components/GridComponent/GridComponent";
import {DatePicker} from "../../components/DatePicker/DatePicker";
import RadioComponent from "../../components/RadioComponent/RadioComponent";
import {validateForm, ValidationErrors} from '../../utils/placesTimeUtils'
import {convertSearchDTOPostToSearchDTO, convertToPlaceTimePost} from '../../utils/placeConverters'
import "../../styles/globals.css"
import styles from "./SearchPage.module.css"
import SearchResultPage from "../SearchResultsPage/SearchResultsPage"
import {
    PlaceLocation,
    PlaceLocationPost,
    PlaceTime,
    PlaceTimePost,
    SearchDTO,
    SearchDTOPost,
    SearchDTOSave
} from "../../types/SearchDTO"
import React, {useMemo, useState} from 'react';
import {postSearch, postSearchSave} from "../../api/services/searchService";
import {useSearchHandlers} from "../../hooks/useSearchHandlers"
import {SaveSearchTripModal} from "../../components/SaveSearchTripModal/SaveSearchTripModal";
import {useDisclosure} from '@mantine/hooks';
import {
    GRID_ITEMS_SEARCH,
    MAX_PASSENGERS_NUMBER,
    MAX_TOTAL_DAYS_NUMBER,
    MEANS_OF_TRANSPORT,
    MIN_PASSENGERS_NUMBER,
    MIN_TOTAL_DAYS_NUMBER,
    MINIMIZED_CRITERION
} from "../../constants/constants";
import {
    EXAMPLE_SEARCH_POST_DTO,
    INITIAL_SEARCH_DTO_SAVE
} from "../../constants/searchPostDto"
import {Trip} from "../../types/TripDTO";
import useScrollToBottom from "../../hooks/useScrollToBottom";
import {convertToSearchDTOSave} from "../../utils/convertSearchDTOSave";

const span = 12;
const columnNumber = 1 / 5;

const gridItemsMultiplier: number[] = [
    columnNumber * 2,
    columnNumber,
    columnNumber,
    columnNumber,
];

export function SearchPage() {
    const location = useLocation();
    const searchData = location.state as SearchDTO | undefined;
    console.log(searchData)

    const searchDTO: SearchDTO = useMemo(() => {
        return convertSearchDTOPostToSearchDTO(EXAMPLE_SEARCH_POST_DTO);
    }, [EXAMPLE_SEARCH_POST_DTO]);

    return (
        SearchFunction(searchDTO)
    );
}

function SearchFunction(paramDto: SearchDTO) {
    const [trips, setTrips] = useState<Trip[]>([]);
    useScrollToBottom(trips);
    const [loading, setLoading] = useState<boolean>(false);
    const [searchDTOSave, setSearchDTOSave] = useState<SearchDTOSave>(INITIAL_SEARCH_DTO_SAVE);


    const {
        searchDto,
        setSearchDto,
        handlePassengersNumberChange,
        handleTransportChange,
        handleDateChange,
        handleMaxTotalTimeChange,
        handleCriterionChange,
    } = useSearchHandlers(paramDto);

    const [errors, setErrors] = useState<ValidationErrors>({
        dateError: null,
        placesTimeError: null,
        startPlaceError: null,
        endPlaceError: null,
        maxHoursToSpendError: null
    });

    const performSearch = async (dto: SearchDTOPost): Promise<Trip[]> => {
        try {
            const response = await postSearch(dto);
            return response;
        } catch (error) {
            console.error('Error during POST request:', error);
            return [];
        }
    };
    const handleSave = async (name: string) => {
        const updatedSearch: SearchDTOSave = {...searchDTOSave, name};
        console.log(updatedSearch)
        const response = await postSearchSave(updatedSearch);

        if (response.success) {
            return true;
        }

        return false;
    };


    const handleSubmit = async () => {
        const startPlace: PlaceLocation = searchDto.start;
        const endPlace: PlaceLocation = searchDto.end;
        const placesTimeList: PlaceTime[] = searchDto.placesTime;

        const numberOfPassengers = searchDto.passengersNumber;
        const selectedTransport = searchDto.transport === MEANS_OF_TRANSPORT[0] ? null : (searchDto.transport === null ? null : searchDto.transport.toUpperCase());
        const selectedDate = searchDto.startDate;
        const maxHoursToSpend = searchDto.maxTotalTime * 24;
        const preferredCriteria = searchDto.preferredCriteria

        // validation of errors
        const newErrors = validateForm(selectedDate, placesTimeList, startPlace, endPlace, maxHoursToSpend);
        setErrors(newErrors);

        // if errors, break
        const hasErrors = Object.values(newErrors).some((error) => error !== null);
        if (hasErrors) {
            return;
        }

        const goalPlacesTime: PlaceTimePost[] = convertToPlaceTimePost(placesTimeList)
        const startPlacePost: PlaceLocationPost = {country: startPlace.country, city: startPlace?.city}
        const endPlacePost: PlaceLocationPost = {country: endPlace.country, city: endPlace?.city}

        const dto: SearchDTOPost = {
            placesToVisit: goalPlacesTime,
            startPlace: startPlacePost,
            endPlace: endPlacePost,
            passengerCount: numberOfPassengers,
            maxTripDuration: maxHoursToSpend,
            tripStartDate: selectedDate,
            preferredTransport: selectedTransport,
            optimizationCriteria: preferredCriteria
        };

        console.log(dto)
        setTrips([]); // Clear previous trips
        setLoading(true);

        setTimeout(async () => {

            const searchResults = await performSearch(dto);
            setTrips(searchResults);
            setLoading(false);
        }, 500);


        setSearchDTOSave(convertToSearchDTOSave(dto));
    };

    // grape color - defines small elements like slider and checkbox
    return (
        <div className={styles.blueBackground}>
            <MantineProvider theme={{primaryColor: 'grape'}}>
                <Box pos="relative">

                    <HeaderSearch></HeaderSearch>
                    <GridComponent gridItems={GRID_ITEMS_SEARCH}
                                   gridItemsMultiplier={gridItemsMultiplier}></GridComponent>
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
                                <NumberInput
                                    size="md"
                                    radius="md"
                                    mx="md"
                                    name={"passengersNumber"}
                                    min={MIN_PASSENGERS_NUMBER}
                                    max={MAX_PASSENGERS_NUMBER}
                                    onChange={handlePassengersNumberChange}
                                    value={searchDto.passengersNumber}
                                    defaultValue={MIN_PASSENGERS_NUMBER}
                                />
                                <LoadingOverlay visible={loading} zIndex={1000} overlayProps={{radius: "sm", blur: 2}}/>
                                <Button size={"xl"} className={styles.pinkButton}
                                        onClick={handleSubmit}>Search!</Button>
                                {trips.length > 0 && (
                                <Box className={styles.saveBox}>
                                    Save search
                                        <SaveSearchTripModal
                                            entityType="search"
                                            onSave={handleSave}
                                        />
                                </Box>
                                )}

                            </GridCol>

                            {/*column with transport*/}
                            <GridCol key={3} span={span * gridItemsMultiplier[2]}>
                                <NativeSelect
                                    size="md"
                                    radius="md"
                                    data={MEANS_OF_TRANSPORT}
                                    value={searchDto.transport ?? MEANS_OF_TRANSPORT[0]}
                                    onChange={handleTransportChange}
                                />
                            </GridCol>

                            {/*column with date, criterion and maxTotalDays*/}
                            <GridCol key={4} span={span * gridItemsMultiplier[3]}>
                                <Text className={styles.textStyle}>Start date</Text>
                                <DatePicker
                                    value={searchDto.startDate}
                                    onDateChange={handleDateChange}/>
                                {errors.dateError &&
                                    <Text color="red" size="sm">{errors.dateError}</Text>
                                }
                                <Divider my="md"/>

                                <Text className={styles.textStyle}>What do you want to minimize?</Text>
                                <RadioComponent
                                    labels={MINIMIZED_CRITERION}
                                    selectedValue={searchDto.preferredCriteria}
                                    onChange={handleCriterionChange}
                                />
                                <Divider my="md"/>

                                <Text className={styles.textStyle}>Maximum total days number</Text>
                                <NumberInput
                                    size="md"
                                    name={"maxTotalTime"}
                                    min={MIN_TOTAL_DAYS_NUMBER}
                                    max={MAX_TOTAL_DAYS_NUMBER}
                                    value={searchDto.maxTotalTime}
                                    onChange={handleMaxTotalTimeChange}
                                    defaultValue={MIN_TOTAL_DAYS_NUMBER}
                                />
                                {errors.maxHoursToSpendError &&
                                    <Text color="red" size="sm">{errors.maxHoursToSpendError}</Text>}

                            </GridCol>
                        </Grid>
                        {trips.length > 0 && <SearchResultPage trips={trips}/>}
                    </div>
                </Box>
                <Footer></Footer>
            </MantineProvider>
        </div>
    );
}