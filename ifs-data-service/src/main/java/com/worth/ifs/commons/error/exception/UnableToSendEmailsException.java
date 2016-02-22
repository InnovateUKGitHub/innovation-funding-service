package com.worth.ifs.commons.error.exception;

/**
 * Created by rav on 18/02/2016.
 */
public class UnableToSendEmailsException extends RuntimeException{
    public UnableToSendEmailsException() {
    }

    public UnableToSendEmailsException(String message) {
        super(message);
    }

    public UnableToSendEmailsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToSendEmailsException(Throwable cause) {
        super(cause);
    }

    public UnableToSendEmailsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
