package com.worth.ifs.commons.error.exception;

/**
 * Created by rav on 18/02/2016.
 */
public class UnableToCreateFileException extends RuntimeException {
    public UnableToCreateFileException() {
    }

    public UnableToCreateFileException(String message) {
        super(message);
    }

    public UnableToCreateFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToCreateFileException(Throwable cause) {
        super(cause);
    }

    public UnableToCreateFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
