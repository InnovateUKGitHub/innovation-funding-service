package com.worth.ifs.commons.error.exception;


import java.util.List;

/**
 * This object is used when a request to the RestServices returns a null value,
 * so the object is not found on the date-service side. We can then handle this
 * exception in a way to show the error message to the user.
 */
public class InvalidURLException extends IFSRuntimeException {

    public InvalidURLException() {
    	// no-arg constructor
    }

    public InvalidURLException(List<Object> arguments) {
        super(arguments);
    }

    public InvalidURLException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public InvalidURLException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public InvalidURLException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public InvalidURLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}