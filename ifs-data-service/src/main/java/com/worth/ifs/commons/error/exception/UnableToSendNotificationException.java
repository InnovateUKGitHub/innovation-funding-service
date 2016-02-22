package com.worth.ifs.commons.error.exception;

/**
 * Created by rav on 18/02/2016.
 */
public class UnableToSendNotificationException extends RuntimeException {
    public UnableToSendNotificationException() {
    }

    public UnableToSendNotificationException(String message) {
        super(message);
    }

    public UnableToSendNotificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToSendNotificationException(Throwable cause) {
        super(cause);
    }

    public UnableToSendNotificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
