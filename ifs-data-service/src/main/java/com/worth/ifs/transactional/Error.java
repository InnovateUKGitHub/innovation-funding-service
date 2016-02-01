package com.worth.ifs.transactional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 *
 */
public class Error {

    private String errorKey;
    private List<Object> arguments;
    private String errorMessage;

    @JsonIgnore
    private HttpStatus statusCode;

    /**
     * For JSON marshalling
     */
    @SuppressWarnings("unused")
    private Error() {

    }

    public Error(ErrorTemplate errorTemplate) {
        this(errorTemplate, emptyList());
    }

    public Error(ErrorTemplate errorTemplate, Object... arguments) {
        this(errorTemplate, asList(arguments));
    }

    public Error(ErrorTemplate errorTemplate, List<Object> arguments) {
        this(errorTemplate.getErrorKey(), errorTemplate.getErrorMessage(), arguments, errorTemplate.getCategory());
    }

    public Error(String messageKey, HttpStatus statusCode) {
        this(messageKey, emptyList(), statusCode);
    }

    public Error(String messageKey, String readableErrorMessage, HttpStatus statusCode) {
        this(messageKey, readableErrorMessage, emptyList(), statusCode);
    }

    public Error(String messageKey, List<Object> arguments, HttpStatus statusCode) {
        this(messageKey, null, arguments, statusCode);
    }

    public Error(String messageKey, String readableErrorMessage, List<Object> arguments, HttpStatus statusCode) {
        this.errorKey = messageKey;
        this.errorMessage = readableErrorMessage;
        this.arguments = arguments;
        this.statusCode = statusCode;
    }

    public Error(Enum<?> messageKey, HttpStatus statusCode) {
        this(messageKey, emptyList(), statusCode);
    }

    public Error(Enum<?> messageKey, String readableErrorMessage, HttpStatus statusCode) {
        this(messageKey, readableErrorMessage, emptyList(), statusCode);
    }

    public Error(Enum<?> messageKey, List<Object> arguments, HttpStatus statusCode) {
        this(messageKey, null, arguments, statusCode);
    }

    public Error(Enum<?> messageKey, String readableErrorMessage, List<Object> arguments, HttpStatus statusCode) {
        this(messageKey.name(), readableErrorMessage, arguments, statusCode);
    }

    public HttpStatus getStatusCode() {
        return statusCode;
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
