package com.worth.ifs.commons.rest;

import com.worth.ifs.commons.error.Error;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Resource object to return validation messages on rest calls.
 */
public class ValidationMessages implements Serializable {

    private String objectName;
    private Long objectId;
    private List<Error> errors = new ArrayList<>();

    public ValidationMessages(MessageSource messageSource, Long objectId, BindingResult bindingResult) {
        bindingResult.getFieldErrors().forEach(e -> {
                    List<Object> args = Arrays.asList();
                    String errorMessage;
                    try {
                        errorMessage = messageSource.getMessage(e, Locale.UK);
                    } catch (NoSuchMessageException ex) {
                        errorMessage = e.getDefaultMessage();
                    }
                    if (errorMessage == null) {
                        errorMessage = "";
                    }
                    Error error = new Error(e.getField(), errorMessage, args, HttpStatus.NOT_ACCEPTABLE);
                    errors.add(error);
                }
        );

        bindingResult.getGlobalErrors().forEach(e -> {
                    List<Object> args = Arrays.asList();
                    Error error = new Error("", e.getDefaultMessage(), args, HttpStatus.NOT_ACCEPTABLE);
                    errors.add(error);
                }
        );
        objectName = bindingResult.getObjectName();
        this.objectId = objectId;
    }

    public ValidationMessages() {

    }

    public ValidationMessages(String objectName, Long objectId, List<Error> errors) {
        this.objectName = objectName;
        this.objectId = objectId;
        this.errors = errors;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ValidationMessages that = (ValidationMessages) o;

        return new EqualsBuilder()
                .append(objectName, that.objectName)
                .append(objectId, that.objectId)
                .append(errors, that.errors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(objectName)
                .append(objectId)
                .append(errors)
                .toHashCode();
    }
}
