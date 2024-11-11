export const MEANS_OF_TRANSPORT: string[] = ['','Bus', 'Train', 'Plane']
export type MeanOfTransport = typeof MEANS_OF_TRANSPORT[number];
export const DISCOUNTS: string[] = ['Adult']
export const MINIMIZED_CRITERION: string[] = ['Price', 'Duration']
export const MIN_PASSENGERS_NUMBER: number = 1

export const MAX_PASSENGERS_NUMBER: number = 20
export const MAX_TOTAL_DAYS_NUMBER: number = 365
export const MIN_TOTAL_DAYS_NUMBER: number = 1

export const AUTHENTICATED_LINKS = [
    { link: '/account', label: 'My account' },
    { link: '/travels', label: 'My Travels' },
    { link: '/searches', label: 'My Searches' },
    { link: '/support', label: 'Support' },
];

export const UNAUTHENTICATED_LINKS = [
    { link: '/home', label: 'Home' },
    { link: '/support', label: 'Support' },
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