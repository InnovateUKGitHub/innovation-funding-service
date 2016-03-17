package com.worth.ifs.commons.error.exception;


import java.util.List;

/**
 * This object is used when a request to the RestServices returns a null value,
 * so the object is not found on the date-service side. We can then handle this
 * exception in a way to show the error message to the user.
 */
public class ObjectNotFoundException extends IFSRuntimeException {

    public ObjectNotFoundException() {
    	// no-arg constructor
    }

    public ObjectNotFoundException(List<Object> arguments) {
        super(arguments);
    }

    public ObjectNotFoundException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public ObjectNotFoundException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public ObjectNotFoundException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public ObjectNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}