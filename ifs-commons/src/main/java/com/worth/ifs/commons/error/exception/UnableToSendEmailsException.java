package com.worth.ifs.commons.error.exception;

import java.util.List;

public class UnableToSendEmailsException extends IFSRuntimeException{
    public UnableToSendEmailsException() {
    	// no-arg constructor
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
