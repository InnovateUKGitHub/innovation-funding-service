package com.worth.ifs.commons.error.exception;

/**
 * Created by rav on 18/02/2016.
 */
public class LengthRequiredException extends RuntimeException {
    public LengthRequiredException() {
    }

    public LengthRequiredException(String message) {
        super(message);
    }

    public LengthRequiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public LengthRequiredException(Throwable cause) {
        super(cause);
    }

    public LengthRequiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
