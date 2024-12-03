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
export function formatTime(minutes: number) {
    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;

    if (hours > 0) {
        return `${hours} hr ${remainingMinutes} min`;
    } else {
        return `${remainingMinutes} min`;
    }
}
export function formatTimeToDaysAndHours(hoursNumber: number) {
    const days = Math.floor(hoursNumber / 24);

    const hours = hoursNumber % 24;

    if (days > 0) {
        return `${days} day(s) ${hours} hour(s)`;
    } else {
        return `${hours} hour(s)`;
    }
}

export const selectedDateToString = (date: Date): string => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Miesiące są 0-indeksowane
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

export const stringToDate = (dateString: string | null): Date | null => {
    if (!dateString) {
        return null;
    }
    const [year, month, day] = dateString.split('-').map(Number); // Rozdziela rok, miesiąc i dzień
    return new Date(year, month - 1, day, 0, 0, 0); // Tworzy datę z godziną 00:00:00
};