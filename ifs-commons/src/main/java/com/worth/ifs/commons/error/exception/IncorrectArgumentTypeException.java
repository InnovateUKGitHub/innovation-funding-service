package com.worth.ifs.commons.error.exception;

import java.util.List;

/**
 * Represents an error returned by data layer because of incorrect argument type in request
 */
public class IncorrectArgumentTypeException extends IFSRuntimeException {
    public IncorrectArgumentTypeException() {
    	// no-arg constructor
    }

    public IncorrectArgumentTypeException(List<Object> arguments) {
        super(arguments);
    }

    public IncorrectArgumentTypeException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public IncorrectArgumentTypeException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public IncorrectArgumentTypeException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public IncorrectArgumentTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
