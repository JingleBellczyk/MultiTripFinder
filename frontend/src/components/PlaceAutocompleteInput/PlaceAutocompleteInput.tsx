import { useEffect, useRef, useState } from 'react';
import { useMapsLibrary } from '@vis.gl/react-google-maps';
import { Input } from '@mantine/core';

interface PlaceAutocompleteInputProps {
    value: string;
    onPlaceSelect: (place: google.maps.places.PlaceResult | null) => void;
    label: string;
}

export const PlaceAutocompleteInput: React.FC<PlaceAutocompleteInputProps> = ({ value, onPlaceSelect, label }) => {
    const [options, setOptions] = useState<string[]>([]);
    const [inputValue, setInputValue] = useState(value);
    const inputRef = useRef<HTMLInputElement>(null);
    const places = useMapsLibrary('places');

    useEffect(() => {
        if (!places || !inputRef.current) return;

        const autocomplete = new places.Autocomplete(inputRef.current, {
            fields: ['formatted_address', 'address_components'],
            types: ['administrative_area_level_1', 'locality']
        });

        const listener = autocomplete.addListener('place_changed', () => {
            const place = autocomplete.getPlace();
            setInputValue(place?.formatted_address || '');  // Debug log for selected place
            onPlaceSelect(place || null);
        });

        return () => {
            google.maps.event.clearInstanceListeners(autocomplete);
            listener.remove();
        };
    }, [places, onPlaceSelect]);

    useEffect(() => {
        setInputValue(value);
    }, [value]);

    const handleInputChange = (value: string) => {
        setInputValue(value);

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
        service.getPlacePredictions({ input: value }, (predictions, status) => {
            if (status === google.maps.places.PlacesServiceStatus.OK && predictions) {
                setOptions(predictions.map(prediction => prediction.description));
            } else {
                setOptions([]);
            }
        });
    };

    return (
        <div style={{ width: '100%' }}>
            <Input.Wrapper label={label} size="md">
                <Input
                    ref={inputRef}
                    value={inputValue}
                    placeholder="Enter city and country"
                    onChange={(e) => handleInputChange(e.target.value)}
                />
            </Input.Wrapper>
        </div>
    );
};