package org.innovateuk.ifs.commons.exception;

import java.util.List;

public class ServiceUnavailableException extends IFSRuntimeException {

    public ServiceUnavailableException() {
        // no-arg constructor
    }

    public ServiceUnavailableException(List<Object> arguments) {
        super(arguments);
    }

    public ServiceUnavailableException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public ServiceUnavailableException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public ServiceUnavailableException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public ServiceUnavailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}