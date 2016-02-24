package com.worth.ifs.commons.error.exception;

import java.util.List;

/**
 * Created by rav on 18/02/2016.
 *
 */
public class UnableToSendEmailsException extends IFSRuntimeException{
    public UnableToSendEmailsException() {
    }

    public UnableToSendEmailsException(List<Object> arguments) {
        super(arguments);
    }

    public UnableToSendEmailsException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public UnableToSendEmailsException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public UnableToSendEmailsException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public UnableToSendEmailsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
