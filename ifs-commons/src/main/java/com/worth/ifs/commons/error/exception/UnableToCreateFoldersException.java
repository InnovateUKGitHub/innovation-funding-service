package com.worth.ifs.commons.error.exception;

import java.util.List;

public class UnableToCreateFoldersException extends IFSRuntimeException {
    public UnableToCreateFoldersException() {
    	// no-arg constructor
    }

    public UnableToCreateFoldersException(List<Object> arguments) {
        super(arguments);
    }

    public UnableToCreateFoldersException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public UnableToCreateFoldersException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public UnableToCreateFoldersException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public UnableToCreateFoldersException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
