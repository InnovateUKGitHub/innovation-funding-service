package com.worth.ifs.commons.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents an error returned by data layer because of incorrect argument type in request
 */
@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Argument was of an incorrect type")
public class IncorrectArgumentTypeException extends IllegalArgumentException {

    public IncorrectArgumentTypeException() {
    }

    public IncorrectArgumentTypeException(String s) {
        super(s);
    }

    public IncorrectArgumentTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectArgumentTypeException(Throwable cause) {
        super(cause);
    }
}
