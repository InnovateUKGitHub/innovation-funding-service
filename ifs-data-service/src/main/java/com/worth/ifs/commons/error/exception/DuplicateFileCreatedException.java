package com.worth.ifs.commons.error.exception;

/**
 * Created by rav on 18/02/2016.
 */
public class DuplicateFileCreatedException extends RuntimeException {
    public DuplicateFileCreatedException() {
    }

    public DuplicateFileCreatedException(String message) {
        super(message);
    }

    public DuplicateFileCreatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateFileCreatedException(Throwable cause) {
        super(cause);
    }

    public DuplicateFileCreatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
