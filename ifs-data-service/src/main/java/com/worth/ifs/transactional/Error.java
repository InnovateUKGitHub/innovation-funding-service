package com.worth.ifs.transactional;

import java.util.List;

/**
 *
 */
public class Error {

    private String errorKey;
    private List<Object> arguments;
    private String errorMessage;

    /**
     * For JSON marshalling
     */
    Error() {
    }

    public Error(String errorKey) {
        this.errorKey = errorKey;
    }

    public Error(String errorKey, List<Object> arguments) {
        this.errorKey = errorKey;
        this.arguments = arguments;
    }

    public Error(String errorKey, String errorMessage) {
        this.errorKey = errorKey;
        this.errorMessage = errorMessage;
    }

    public Error(String errorKey, List<Object> arguments, String errorMessage) {
        this.errorKey = errorKey;
        this.arguments = arguments;
        this.errorMessage = errorMessage;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
