package com.worth.ifs.commons.error.exception;

import java.util.List;

public class PayloadTooLargeException extends IFSRuntimeException {
    public PayloadTooLargeException() {
    	// no-arg constructor
    }

    public PayloadTooLargeException(List<Object> arguments) {
        super(arguments);
    }

    public PayloadTooLargeException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public PayloadTooLargeException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public PayloadTooLargeException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public PayloadTooLargeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
