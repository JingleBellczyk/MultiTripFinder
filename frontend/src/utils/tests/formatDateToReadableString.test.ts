import { formatDateToReadableString, formatTime } from '../formatDateToReadableString';

describe('formatDateToReadableString', () => {
    it('formats ISO date to readable string in Polish locale', () => {
        const isoDate = '2024-01-01T15:30:00Z';
        const formattedDate = formatDateToReadableString(isoDate);
        expect(formattedDate).toBe('01.01.2024, 16:30:00');
    });

    it('formats ISO date with different time zones correctly', () => {
        const isoDate = '2024-01-01T00:00:00Z';
        const formattedDate = formatDateToReadableString(isoDate);
        expect(formattedDate).toBe('01.01.2024, 01:00:00');
    });

    it('handles invalid ISO date gracefully', () => {
        const invalidDate = 'invalid-date';
        const formattedDate = formatDateToReadableString(invalidDate);
        expect(formattedDate).toBe('Invalid Date'); // Wynik zależny od przeglądarki/środowiska
    });
});

describe('formatTime', () => {
    it('formats time with both hours and minutes', () => {
        const minutes = 125;
        const formattedTime = formatTime(minutes);
        expect(formattedTime).toBe('2 hr 5 min');
    });

    it('formats time with only minutes', () => {
        const minutes = 45;
        const formattedTime = formatTime(minutes);
        expect(formattedTime).toBe('45 min');
    });

    it('formats time with exactly one hour', () => {
        const minutes = 60;
        const formattedTime = formatTime(minutes);
        expect(formattedTime).toBe('1 hr 0 min');
    });

    it('handles zero minutes', () => {
        const minutes = 0;
        const formattedTime = formatTime(minutes);
        expect(formattedTime).toBe('0 min');
    });

    it('handles large number of minutes', () => {
        const minutes = 10000;
        const formattedTime = formatTime(minutes);
        expect(formattedTime).toBe('166 hr 40 min');
    });
});
