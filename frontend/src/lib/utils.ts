export function cn(...classes: (string | undefined | false | null)[]): string {
    return classes.filter(Boolean).join(' '); // Filters out any falsy values and joins the remaining class names with a space
}