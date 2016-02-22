package com.worth.ifs.commons.error.exception;

/**
 * Represents error raised by data layer when attempting to insert a duplicate email address.
 */
public class DuplicateEmailAddressException extends RuntimeException {
    public DuplicateEmailAddressException() {
    }

    public DuplicateEmailAddressException(String message) {
        super(message);
    }

    public DuplicateEmailAddressException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateEmailAddressException(Throwable cause) {
        super(cause);
    }

    public DuplicateEmailAddressException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
