package com.worth.ifs.commons.error.exception;

/**
 * Created by rav on 18/02/2016.
 */
public class UnableToUpdateFileException extends RuntimeException {
    public UnableToUpdateFileException() {
    }

    public UnableToUpdateFileException(String message) {
        super(message);
    }

    public UnableToUpdateFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToUpdateFileException(Throwable cause) {
        super(cause);
    }

    public UnableToUpdateFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
