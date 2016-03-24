package com.worth.ifs.commons.error.exception;

import java.util.List;

public class UnableToDeleteFileException extends IFSRuntimeException {
    public UnableToDeleteFileException() {
    	// no-arg constructor
    }

    public UnableToDeleteFileException(List<Object> arguments) {
        super(arguments);
    }

    public UnableToDeleteFileException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public UnableToDeleteFileException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public UnableToDeleteFileException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public UnableToDeleteFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
