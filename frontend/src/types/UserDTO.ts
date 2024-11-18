export type User = {
    id: number;
    email: string;
    role: string;
    given_name: string;
    picture: string;
};

export type UserDTO = {
    id: number;
    email: string;
    role: string;
}

export type UserProps = {
    user: User | null;
}