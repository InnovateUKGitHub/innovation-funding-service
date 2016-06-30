package com.worth.ifs.commons.rest;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorHolder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.validation.BindingResult;

import java.io.Serializable;
import java.util.*;

import static com.worth.ifs.commons.error.Error.fieldError;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

/**
 * Resource object to return validation messages on rest calls.
 */
public class ValidationMessages implements ErrorHolder, Serializable {

    private String objectName;
    private Long objectId;
    private Set<Error> errors = new LinkedHashSet<>();

    public ValidationMessages(MessageSource messageSource, Long objectId, BindingResult bindingResult) {
        bindingResult.getFieldErrors().forEach(e -> {
                    List<Object> args = asList();
                    String errorMessage;
                    try {
                        errorMessage = messageSource.getMessage(e, Locale.UK);
                    } catch (NoSuchMessageException ex) {
                        errorMessage = e.getDefaultMessage();
                    }
                    if (errorMessage == null) {
                        errorMessage = "";
                    }
                    Error error = fieldError(e.getField(), errorMessage, args);
                    errors.add(error);
                }
        );

        bindingResult.getGlobalErrors().forEach(e -> {
                    Error error = new Error("", e.getDefaultMessage(), NOT_ACCEPTABLE);
                    errors.add(error);
                }
        );
        objectName = bindingResult.getObjectName();
        this.objectId = objectId;
    }

    public ValidationMessages() {

    }

    public ValidationMessages(Error... errors) {
        this(asList(errors));
    }

    public ValidationMessages(List<Error> errors) {
        this.errors.addAll(errors);
    }

    public ValidationMessages(String objectName, Long objectId, List<Error> errors) {
        this.objectName = objectName;
        this.objectId = objectId;
        this.errors.addAll(errors);
    }

    public boolean hasErrorWithKey(String key) {
        return errors.stream().anyMatch(e -> e.getErrorKey().equals(key));
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
        return new ArrayList<>(errors);
    }

    public void setErrors(List<Error> errors) {
        this.errors.clear();
        this.errors.addAll(errors);
    }

    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    public void addError(Error error) {
        errors.add(error);
    }

    public void addAll(ValidationMessages messages) {
        if (messages != null) {
            this.errors.addAll(messages.getErrors());
        }
    }

    public void addAll(List<ValidationMessages> messages) {
        messages.forEach(this::addAll);
    }

    public void addErrors(List<Error> errors) {
        this.errors.addAll(errors);
    }

    public static ValidationMessages noErrors() {
        return new ValidationMessages();
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
