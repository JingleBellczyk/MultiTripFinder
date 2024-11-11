import React, {useState} from 'react';
import {APIProvider} from '@vis.gl/react-google-maps';
import {PlaceAutocompleteInput} from './PlaceAutocompleteInput';

// @ts-ignore
const GOOGLE_API_KEY: string = process.env.REACT_APP_GOOGLE_API_KEY;
interface LocationSelectorProps {
    value: string; // Current value of the input
    onChange: (formatted_adress: string, country: string, city:string) => void; // Callback to handle changes
    label: string
}

export const LocationSelector: React.FC<LocationSelectorProps> = ({value, onChange, label}) => {
    const [selectedPlace, setSelectedPlace] = useState<google.maps.places.PlaceResult | null>(null);

    const handlePlaceSelect = (place: google.maps.places.PlaceResult | null) => {
        setSelectedPlace(place);
        if (place && place.formatted_address && place.address_components) {
            const city_row = place.address_components.find(component =>
                component.types.some(type => type === "locality" || type === "administrative_area_level_1")
            );

            const country_row = place.address_components.find(component =>
                component.types.some(type => type === "country")
            );
            console.log(place.address_components)

            if(city_row && country_row){
                console.log(city_row?.long_name)
                console.log(country_row?.long_name)

                onChange(place.formatted_address, country_row.long_name, city_row.long_name); // Pass address to parent
            }else{
                console.log(place.address_components)

                onChange("","","")
            }
        }else{
            onChange("","", "")
        }
    };

    return (
        <APIProvider apiKey={GOOGLE_API_KEY} language="en">
            <PlaceAutocompleteInput
                value={value}
                onPlaceSelect={handlePlaceSelect} label={label}/>
        </APIProvider>
    );
};