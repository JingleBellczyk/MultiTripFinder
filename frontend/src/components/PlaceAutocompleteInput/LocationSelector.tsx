import React, {useState} from 'react';
import {APIProvider} from '@vis.gl/react-google-maps';
import {PlaceAutocompleteInput} from './PlaceAutocompleteInput';

// @ts-ignore
const GOOGLE_API_KEY: string = process.env.REACT_APP_GOOGLE_API_KEY;
interface LocationSelectorProps {
    value: string; // Current value of the input
    onChange: (value: string) => void; // Callback to handle changes
}

export const LocationSelector: React.FC<LocationSelectorProps> = ({value, onChange}) => {
    const [selectedPlace, setSelectedPlace] = useState<google.maps.places.PlaceResult | null>(null);

    const handlePlaceSelect = (place: google.maps.places.PlaceResult | null) => {
        setSelectedPlace(place);
        if (place && place.formatted_address) {
            onChange(place.formatted_address); // Pass address to parent
        }else{
            onChange("")
        }
    };

    return (
        <APIProvider apiKey={GOOGLE_API_KEY}>
            <PlaceAutocompleteInput onPlaceSelect={handlePlaceSelect}/>
        </APIProvider>
    );
};