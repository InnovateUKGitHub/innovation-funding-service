package com.worth.ifs.transactional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 *
 */
public class RestError {

    private Error error;

    @JsonIgnore
    private HttpStatus statusCode;

    public RestError(String messageKey, HttpStatus statusCode) {
        this(messageKey, emptyList(), statusCode);
    }

    public RestError(String messageKey, String readableErrorMessage, HttpStatus statusCode) {
        this(messageKey, readableErrorMessage, emptyList(), statusCode);
    }

    public RestError(String messageKey, List<Object> arguments, HttpStatus statusCode) {
        this(messageKey, null, arguments, statusCode);
    }

    public RestError(String messageKey, String readableErrorMessage, List<Object> arguments, HttpStatus statusCode) {
        this.error = new Error(messageKey, arguments, readableErrorMessage);
        this.statusCode = statusCode;
    }

    public RestError(Enum<?> messageKey, HttpStatus statusCode) {
        this(messageKey, emptyList(), statusCode);
    }

    public RestError(Enum<?> messageKey, String readableErrorMessage, HttpStatus statusCode) {
        this(messageKey, readableErrorMessage, emptyList(), statusCode);
    }

    public RestError(Enum<?> messageKey, List<Object> arguments, HttpStatus statusCode) {
        this(messageKey, null, arguments, statusCode);
    }

    public RestError(Enum<?> messageKey, String readableErrorMessage, List<Object> arguments, HttpStatus statusCode) {
        this(messageKey.name(), readableErrorMessage, arguments, statusCode);
    }

    public RestError(Error error, HttpStatus statusCode) {
        this.error = error;
        this.statusCode = statusCode;
    }

    public Error getError() {
        return error;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
