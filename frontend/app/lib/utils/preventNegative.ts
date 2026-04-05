const NEGATIVE_SIGN_REGEX = /[-]/;

export function preventNegativeKey(event: React.KeyboardEvent<HTMLInputElement>) {
    const isMinusKey =
        event.key === "-" ||
        event.key === "Minus" ||
        event.key === "Subtract" ||
        event.code === "NumpadSubtract";

    if (isMinusKey) {
        event.preventDefault();
    }
}

export function preventNegativePaste(event: React.ClipboardEvent<HTMLInputElement>) {
    const pastedText = event.clipboardData.getData("text");

    if (NEGATIVE_SIGN_REGEX.test(pastedText)) {
        event.preventDefault();
    }
}