package com.worth.ifs.commons.error.exception;

/**
 * Created by rav on 18/02/2016.
 */
public class UnableToCreateFoldersException extends RuntimeException {
    public UnableToCreateFoldersException() {
    }

    public UnableToCreateFoldersException(String message) {
        super(message);
    }

    public UnableToCreateFoldersException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToCreateFoldersException(Throwable cause) {
        super(cause);
    }

    public UnableToCreateFoldersException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
