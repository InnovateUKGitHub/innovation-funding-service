package com.worth.ifs.commons.error.exception;

/**
 * Created by rav on 18/02/2016.
 */
public class UnableToDeleteFileException extends RuntimeException {
    public UnableToDeleteFileException() {
    }

    public UnableToDeleteFileException(String message) {
        super(message);
    }

    public UnableToDeleteFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToDeleteFileException(Throwable cause) {
        super(cause);
    }

    public UnableToDeleteFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
