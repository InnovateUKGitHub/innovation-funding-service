package com.worth.ifs.commons.error.exception;

/**
 * Created by rav on 18/02/2016.
 */
public class IncorrectlyReportedFileSizeException extends RuntimeException {
    public IncorrectlyReportedFileSizeException() {
    }

    public IncorrectlyReportedFileSizeException(String message) {
        super(message);
    }

    public IncorrectlyReportedFileSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectlyReportedFileSizeException(Throwable cause) {
        super(cause);
    }

    public IncorrectlyReportedFileSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
