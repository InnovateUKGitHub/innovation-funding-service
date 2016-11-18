package com.worth.ifs.commons.error.exception;

import java.util.List;

public class DuplicateFileCreatedException extends IFSRuntimeException {

    public DuplicateFileCreatedException() {
    	// no-arg constructor
    }

    public DuplicateFileCreatedException(List<Object> arguments) {
        super(arguments);
    }

    public DuplicateFileCreatedException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public DuplicateFileCreatedException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public DuplicateFileCreatedException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public DuplicateFileCreatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
