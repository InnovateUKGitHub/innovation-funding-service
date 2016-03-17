package com.worth.ifs.commons.error.exception;

import java.util.List;

/**
 * Created by rav on 18/02/2016.
 */
public class UnableToUpdateFileException extends IFSRuntimeException {
    public UnableToUpdateFileException() {
    	// no-arg constructor
    }

    public UnableToUpdateFileException(List<Object> arguments) {
        super(arguments);
    }

    public UnableToUpdateFileException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public UnableToUpdateFileException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public UnableToUpdateFileException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public UnableToUpdateFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
