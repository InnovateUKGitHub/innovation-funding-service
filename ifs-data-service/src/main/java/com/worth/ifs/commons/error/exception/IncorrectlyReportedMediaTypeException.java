package com.worth.ifs.commons.error.exception;

/**
 * Created by rav on 18/02/2016.
 */
public class IncorrectlyReportedMediaTypeException extends RuntimeException {
    public IncorrectlyReportedMediaTypeException() {
    }

    public IncorrectlyReportedMediaTypeException(String message) {
        super(message);
    }

    public IncorrectlyReportedMediaTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectlyReportedMediaTypeException(Throwable cause) {
        super(cause);
    }

    public IncorrectlyReportedMediaTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
