package com.worth.ifs.commons.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents an unexpected error that occurred in the data layer while processing a request
 *
 */
@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="An unexpected error occurred")
public class GeneralUnexpectedErrorException extends RuntimeException {
    public GeneralUnexpectedErrorException() {
    }

    public GeneralUnexpectedErrorException(String message) {
        super(message);
    }

    public GeneralUnexpectedErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneralUnexpectedErrorException(Throwable cause) {
        super(cause);
    }

    public GeneralUnexpectedErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
