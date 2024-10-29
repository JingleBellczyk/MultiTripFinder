import {useEffect, useRef, useState} from 'react';
import {useMapsLibrary} from '@vis.gl/react-google-maps';
import {Select} from '@mantine/core';

interface PlaceAutocompleteInputProps {
    onPlaceSelect: (place: google.maps.places.PlaceResult | null) => void;
}

export const PlaceAutocompleteInput = ({onPlaceSelect}: PlaceAutocompleteInputProps) => {
    const [options, setOptions] = useState<string[]>([]);
    const inputRef = useRef<HTMLInputElement>(null);
    const places = useMapsLibrary('places');

    useEffect(() => {
        if (!places || !inputRef.current) return;

        const autocomplete = new places.Autocomplete(inputRef.current, {
            fields: ['name', 'formatted_address'],
        });

        const listener = autocomplete.addListener('place_changed', () => {
            const place = autocomplete.getPlace();
            console.log("Selected place:", place);  // Debug log for selected place
            onPlaceSelect(place || null);
        });

        return () => {
            google.maps.event.clearInstanceListeners(autocomplete);
            listener.remove(); // Clean up listener properly
        };
    }, [places, onPlaceSelect]);

    const handleInputChange = (value: string) => {
        if (!places) {
            console.error("Places library not loaded yet");
            return; // Return early if places is null
        }

        if (value.length === 0) {
            setOptions([]); // Clear options if input is empty
            onPlaceSelect(null);
            return;
        }

        const service = new places.AutocompleteService();
        service.getPlacePredictions({input: value}, (predictions, status) => {
            if (status === google.maps.places.PlacesServiceStatus.OK && predictions) {
                setOptions(predictions.map(prediction => prediction.description));
            } else {
                setOptions([]);
            }
        });
    };

    return (
        <div
            style={{width: '100%'}}>
            <input
                ref={inputRef}
                placeholder="Enter a location"
                onChange={(e) => handleInputChange(e.target.value)}  // Update options on input change
            />
        </div>
    );
};
