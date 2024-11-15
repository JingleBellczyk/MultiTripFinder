export const MEANS_OF_TRANSPORT: string[] = ['','Bus', 'Train', 'Plane']
export type MeanOfTransport = typeof MEANS_OF_TRANSPORT[number];
export const DISCOUNTS: string[] = ['Adult']
export const MINIMIZED_CRITERION: string[] = ['Price', 'Duration']
export const MIN_PASSENGERS_NUMBER: number = 1
export const MAX_PASSENGERS_NUMBER: number = 20
export const MAX_TOTAL_DAYS_NUMBER: number = 365
export const MIN_TOTAL_DAYS_NUMBER: number = 1
export const MAX_TAG_LENGTH: number = 20

export const PAGE_SIZE=2
export const SERVER = "http://localhost:8080";

export type User = {
    id: number;
    email: string;
    role: string;
    given_name: string;
    picture: string;
};
export const AUTHENTICATED_LINKS = [
    { link: '/', label: 'Home' },
    { link: '/search', label: 'Search for travels' },
    { link: '/travels', label: 'My Travels' },
    { link: '/searches', label: 'My Searches' }
];

export const UNAUTHENTICATED_LINKS = [
    { link: '/', label: 'Home' },
    { link: '/search', label: 'Search for travels' }
];

export const ADMIN_LINKS = [
    { link: '/users', label: 'Users' },
];

export const GRID_ITEMS_SEARCH: string[] = [
    'Choose places and time to spend there',
    'Choose passengers',
    'Preferable transport',
    'Additional info'
];