import {convertHoursToDays} from '../convertHoursToDays'

import {renderToStaticMarkup} from 'react-dom/server';

describe('convertHoursToDays', () => {
    it('renders correctly for hours less than a day', () => {
        const result = renderToStaticMarkup(convertHoursToDays(10));
        expect(result).toBe('0 day(s), 10h');
    });

    it('renders correctly for exactly one day', () => {
        const result = renderToStaticMarkup(convertHoursToDays(24));
        expect(result).toBe('1 day(s), 0h');
    });

    it('renders correctly for multiple days and remaining hours', () => {
        const result = renderToStaticMarkup(convertHoursToDays(50));
        expect(result).toBe('2 day(s), 2h');
    });

    it('renders correctly for zero hours', () => {
        const result = renderToStaticMarkup(convertHoursToDays(0));
        expect(result).toBe('0 day(s), 0h');
    });

    it('renders correctly for large number of hours', () => {
        const result = renderToStaticMarkup(convertHoursToDays(1000));
        expect(result).toBe('41 day(s), 16h');
    });
});
