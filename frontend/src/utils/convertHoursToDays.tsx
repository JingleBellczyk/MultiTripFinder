import React from "react";

export const convertHoursToDays = (hours: number) => {
    const days = Math.floor(hours / 24);
    const remainingHours = hours % 24;
    return <>{days} day(s), {remainingHours}h</>;
};