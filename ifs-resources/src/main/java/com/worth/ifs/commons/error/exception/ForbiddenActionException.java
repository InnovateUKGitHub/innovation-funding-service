package com.worth.ifs.commons.error.exception;

import java.util.List;

import static java.util.Collections.emptyList;

public class ForbiddenActionException extends IFSRuntimeException {

    public ForbiddenActionException() {
    	// no-arg constructor
    }

    public ForbiddenActionException(String message) {
        super(message, emptyList());
    }

    public ForbiddenActionException(List<Object> arguments) {
        super(arguments);
    }

    public ForbiddenActionException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public ForbiddenActionException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public ForbiddenActionException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public ForbiddenActionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
