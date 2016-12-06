package com.worth.ifs.commons.error.exception;

import java.util.List;

/**
 * Represents error raised by data layer when attempting to insert a duplicate email address.
 */
public class DuplicateEmailAddressException extends IFSRuntimeException {

    public DuplicateEmailAddressException() {
    	// no-arg constructor
    }

    public DuplicateEmailAddressException(List<Object> arguments) {
        super(arguments);
    }

    public DuplicateEmailAddressException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public DuplicateEmailAddressException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public DuplicateEmailAddressException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public DuplicateEmailAddressException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
