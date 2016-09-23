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
    private Object fieldRejectedValue;
    private List<Object> arguments;

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
        this(errorTemplate.getErrorKey(), arguments, errorTemplate.getCategory());
    }

    public Error(String messageKey, HttpStatus statusCode) {
        this(messageKey, emptyList(), statusCode);
    }

    public Error(String messageKey, List<Object> arguments, HttpStatus statusCode) {
        this.errorKey = messageKey;
        this.arguments = simpleMap(arguments, argument -> argument + "");
        this.statusCode = statusCode;
    }

    public Error(Enum<?> messageKey, HttpStatus statusCode) {
        this(messageKey, emptyList(), statusCode);
    }

    public Error(Enum<?> messageKey, List<Object> arguments, HttpStatus statusCode) {
        this(messageKey.name(), arguments, statusCode);
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

    /**
     * A convenience method to create a field error
     */
    public static Error fieldError(String fieldName, Object fieldRejectedValue, String errorKey) {
        return fieldError(fieldName, fieldRejectedValue, errorKey, emptyList());
    }

    /**
     * A convenience method to create a field error with arguments
     */
    public static Error fieldError(String fieldName, Object fieldRejectedValue, String errorKey, Object... arguments) {
        return fieldError(fieldName, fieldRejectedValue, errorKey, asList(arguments));
    }

    /**
     * A convenience method to create a field error
     */
    public static Error fieldError(String fieldName, Object fieldRejectedValue, String errorKey, List<Object> arguments) {
        Error error = new Error(errorKey, arguments, NOT_ACCEPTABLE);
        error.fieldName = fieldName;
        error.fieldRejectedValue = fieldRejectedValue;
        return error;
    }

    /**
     * A convenience method to create a field error with existing Error object
     */
    public static Error fieldError(String fieldName, Error error) {
        return fieldError(fieldName, error.getFieldRejectedValue(), error.getErrorKey(), error.getArguments());
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
    public static Error globalError(ErrorTemplate errorTemplate) {
        return new Error(errorTemplate, NOT_ACCEPTABLE);
    }

    /**
     * A convenience method to create a global (non-field) error
     */
    public static Error globalError(String errorKey, List<Object> arguments) {
        return new Error(errorKey, arguments, NOT_ACCEPTABLE);
    }

    /**
     * A convenience method to create a global (non-field) error
     */
    public static Error globalError(ErrorTemplate errorTemplate, List<Object> arguments) {
        return new Error(errorTemplate, arguments, NOT_ACCEPTABLE);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldRejectedValue() {
        return fieldRejectedValue;
    }

    public void setFieldRejectedValue(Object fieldRejectedValue) {
        this.fieldRejectedValue = fieldRejectedValue;
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
                .append(fieldRejectedValue, error.fieldRejectedValue)
                .append(arguments, error.arguments)
                .append(statusCode, error.statusCode)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(errorKey)
                .append(fieldName)
                .append(fieldRejectedValue)
                .append(arguments)
                .append(statusCode)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("errorKey", errorKey)
                .append("fieldName", fieldName)
                .append("fieldRejectedValue", fieldRejectedValue)
                .append("arguments", arguments)
                .append("statusCode", statusCode)
                .toString();
    }
}
