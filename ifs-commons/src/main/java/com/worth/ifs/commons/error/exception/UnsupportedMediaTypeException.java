package com.worth.ifs.commons.error.exception;

import java.util.List;

public class UnsupportedMediaTypeException extends IFSRuntimeException {
    public UnsupportedMediaTypeException() {
    	// no-arg onstructor
    }

    public UnsupportedMediaTypeException(List<Object> arguments) {
        super(arguments);
    }

    public UnsupportedMediaTypeException(String message, List<Object> arguments) {
        super(message, arguments);
    }

    public UnsupportedMediaTypeException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause, arguments);
    }

    public UnsupportedMediaTypeException(Throwable cause, List<Object> arguments) {
        super(cause, arguments);
    }

    public UnsupportedMediaTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace, arguments);
    }
}
