package com.worth.ifs.commons.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

/**
 * A class for holding information about an error case, including a well-known key, a set of arguments that provide additional
 * contextual information about the error case, and an optional "human readable" error message.
 */
public class Error implements Serializable {

    private String errorKey;
    private String fieldName;
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
        this.arguments = simpleMap(arguments, argument -> argument + "");
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

    /**
     * A convenience method to create a field error
     */
    public static Error fieldError(String fieldName, String messageOrCode) {
        return fieldError(fieldName, messageOrCode, emptyList());
    }

    /**
     * A convenience method to create a field error with arguments
     */
    public static Error fieldError(String fieldName, String messageOrCode, Object... arguments) {
        return fieldError(fieldName, messageOrCode, asList(arguments));
    }

    /**
     * A convenience method to create a field error
     */
    public static Error fieldError(String fieldName, String messageOrCode, List<Object> arguments) {
        Error error = new Error(messageOrCode, arguments, NOT_ACCEPTABLE);
        error.fieldName = fieldName;
        return error;
    }

    /**
     * A convenience method to create a global (non-field) error
     */
    public static Error globalError(String errorKey) {
        return new Error(errorKey, NOT_ACCEPTABLE);
    }

    /**
     * A convenience method to create a global (non-field) error
     */
    public static Error globalError(String errorKey, String messageOrCode) {
        return new Error(errorKey, messageOrCode, NOT_ACCEPTABLE);
    }

    public String getFieldName() {
        return fieldName;
    }

    @JsonIgnore
    public boolean isFieldError() {
        return fieldName != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Error error = (Error) o;

        return new EqualsBuilder()
                .append(errorKey, error.errorKey)
                .append(fieldName, error.fieldName)
                .append(arguments, error.arguments)
                .append(errorMessage, error.errorMessage)
                .append(statusCode, error.statusCode)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(errorKey)
                .append(fieldName)
                .append(arguments)
                .append(errorMessage)
                .append(statusCode)
                .toHashCode();
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("errorKey", errorKey)
                .append("fieldName", fieldName)
                .append("arguments", arguments)
                .append("errorMessage", errorMessage)
                .append("statusCode", statusCode)
                .toString();
    }
}
