package org.dyploma.exception;

public class AmadeusErrorException extends RuntimeException {
    private final String errorMessage;
    private final int statusCode;

    public AmadeusErrorException(String errorMessage, int statusCode) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
