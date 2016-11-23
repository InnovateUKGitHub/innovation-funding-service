package com.worth.ifs.commons.error.exception;

import java.util.List;

public class UnableToSendNotificationException extends IFSRuntimeException {
    public UnableToSendNotificationException() {
    	// no-arg constructor
    }

    public UnableToSendNotificationException(List<Object> arguments) {
        super(arguments);
    }

    public UnableToSendNotificationException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public UnableToSendNotificationException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public UnableToSendNotificationException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public UnableToSendNotificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
