package com.worth.ifs.commons.error.exception;

/**
 * Created by rav on 18/02/2016.
 */
public class FileAlreadyLinkedToFormInputResponseException extends RuntimeException {
    public FileAlreadyLinkedToFormInputResponseException() {
    }

    public FileAlreadyLinkedToFormInputResponseException(String message) {
        super(message);
    }

    public FileAlreadyLinkedToFormInputResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileAlreadyLinkedToFormInputResponseException(Throwable cause) {
        super(cause);
    }

    public FileAlreadyLinkedToFormInputResponseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
