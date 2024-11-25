import {useState} from 'react';
import {SearchDTO, SearchDTOPost} from '../types/SearchDTO';
import {postSearch} from '../api/services/searchService';
import {Trip} from "../types/TripDTO";

export const useSearchHandlers = (initialDto: SearchDTO) => {
    const [searchDto, setSearchDto] = useState<SearchDTO>(initialDto);

    const handlePassengersNumberChange = (value: number | string) => {
        console.log("handlePassengersNumberChange")
        const numericValue = typeof value === "number" ? value : parseInt(value, 10) || 0;
        setSearchDto(prevDto => ({
            ...prevDto,
            passengersNumber: numericValue,
        }));
    };

    const handleTransportChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
        console.log("handleTransportChange")

        const selectedValue = event.target.value;
        setSearchDto(prevDto => ({
            ...prevDto,
            transport: selectedValue,
        }));
    };

    const handleDateChange = (date: Date | null) => {
        console.log("handleDateChange")

        setSearchDto(prevDto => ({
            ...prevDto,
            startDate: date,
        }));
    };

    const handleMaxTotalTimeChange = (value: number | string) => {
        console.log("handleMaxTotalTimeChange")

        const newMaxTotalTime: number = typeof value === 'number' ? value : parseInt(value, 10) || 0;
        setSearchDto(prevDto => ({
            ...prevDto,
            maxTotalTime: newMaxTotalTime,
        }));
    };

    const handleCriterionChange = (newCriterion: string) => {
        console.log("handleCriterionChange")

        setSearchDto(prevDto => ({
            ...prevDto,
            preferredCriteria: newCriterion,
        }));
    };



    return {
        searchDto,
        setSearchDto,
        handlePassengersNumberChange,
        handleTransportChange,
        handleDateChange,
        handleMaxTotalTimeChange,
        handleCriterionChange
    };
};
