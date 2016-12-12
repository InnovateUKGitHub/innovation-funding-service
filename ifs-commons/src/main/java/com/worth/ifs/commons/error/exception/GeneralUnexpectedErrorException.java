package com.worth.ifs.commons.error.exception;

import java.util.List;

/**
 * Represents an unexpected error that occurred in the data layer while processing a request
 *
 */
public class GeneralUnexpectedErrorException extends IFSRuntimeException {
    public GeneralUnexpectedErrorException() {
    	// no-arg constructor
    }

    public GeneralUnexpectedErrorException(List<Object> arguments) {
        super(arguments);
    }

    public GeneralUnexpectedErrorException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public GeneralUnexpectedErrorException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public GeneralUnexpectedErrorException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public GeneralUnexpectedErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
