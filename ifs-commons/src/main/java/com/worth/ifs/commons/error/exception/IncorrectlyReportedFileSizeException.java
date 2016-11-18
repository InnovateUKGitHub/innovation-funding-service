package com.worth.ifs.commons.error.exception;

import java.util.List;

public class IncorrectlyReportedFileSizeException extends IFSRuntimeException {
    public IncorrectlyReportedFileSizeException() {
    	// no-arg constructor
    }

    public IncorrectlyReportedFileSizeException(List<Object> arguments) {
        super(arguments);
    }

    public IncorrectlyReportedFileSizeException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public IncorrectlyReportedFileSizeException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public IncorrectlyReportedFileSizeException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public IncorrectlyReportedFileSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
