package com.worth.ifs.commons.error.exception;

import java.util.ArrayList;
import java.util.List;

public class IFSRuntimeException extends RuntimeException {
    // Arguments returned by data api.  Useful for composing error messages with details from request.
    private List<Object> arguments;

    public IFSRuntimeException() {
        super();
        this.arguments = new ArrayList<>();
    }

    public IFSRuntimeException(List<Object> arguments) {
        super();
        this.arguments = arguments;
    }

    public IFSRuntimeException(String message, List<Object> arguments) {
        super(message);
        this.arguments = arguments;
    }

    public IFSRuntimeException(String message, Throwable cause, List<Object> arguments) {
        super(message, cause);
        this.arguments = arguments;
    }

    public IFSRuntimeException(Throwable cause, List<Object> arguments) {
        super(cause);
        this.arguments = arguments;
    }

    public IFSRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<Object> arguments) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.arguments = arguments;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }
}
