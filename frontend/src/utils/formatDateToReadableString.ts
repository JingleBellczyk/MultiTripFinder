// utils.ts
export function formatDateToReadableString(isoDate: string): string {
    const date = new Date(isoDate); // Tworzymy obiekt Date z ISO string
    const options: Intl.DateTimeFormatOptions = {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false, // 24-godzinny format
    };

    // Zwracamy sformatowaną datę w formacie: dd.MM.yyyy HH:mm
    return date.toLocaleString('pl-PL', options);
}
