package com.worth.ifs.commons.error.exception;

import java.util.List;

public class FileAlreadyLinkedToFormInputResponseException extends IFSRuntimeException {

    public FileAlreadyLinkedToFormInputResponseException() {
    	// no-arg constructor
    }

    public FileAlreadyLinkedToFormInputResponseException(List<Object> arguments) {
        super(arguments);
    }

    public FileAlreadyLinkedToFormInputResponseException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public FileAlreadyLinkedToFormInputResponseException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public FileAlreadyLinkedToFormInputResponseException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public FileAlreadyLinkedToFormInputResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
