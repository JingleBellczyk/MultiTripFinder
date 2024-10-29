export const MEANS_OF_TRANSPORT: string[] = ['','BUS', 'TRAIN', 'PLANE']
export type MeanOfTransport = typeof MEANS_OF_TRANSPORT[number];
export const DISCOUNTS: string[] = ['Adult']
export const MINIMIZED_CRITERION: string[] = ['PRICE', 'TIME']
// export const DISCOUNTS: string[] = ['Adult', 'Child', 'Student']

export const LINKS = [
    {link: '/search', label: 'Search'},
    {link: '/travels', label: 'My Travels'},
    {link: '/searches', label: 'My Searches'},
    {link: '/support', label: 'Log out'},
];

export const GRID_ITEMS_SEARCH: string[] = [
    'Choose places, number of days an hours',
    'Choose passengers',
    'Preferable transport',
    'Additional info'
];