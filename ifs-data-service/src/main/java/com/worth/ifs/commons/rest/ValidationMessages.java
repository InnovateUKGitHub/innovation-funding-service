package com.worth.ifs.commons.rest;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorConverter;
import com.worth.ifs.commons.error.ErrorHolder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.error.ErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.commons.error.ErrorConverterFactory.fieldErrorsToFieldErrors;
import static com.worth.ifs.util.CollectionFunctions.*;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

/**
 * Resource object to return validation messages on rest calls.
 */
public class ValidationMessages implements ErrorHolder, Serializable {

    private String objectName;
    private Long objectId;
    private Set<Error> errors = new LinkedHashSet<>();

    public ValidationMessages() {

    }

    public ValidationMessages(MessageSource messageSource, Long objectId, BindingResult bindingResult) {

        populateFromBindingResult(objectId, bindingResult, e -> {
            try {
                return messageSource.getMessage(e, Locale.UK);
            } catch (NoSuchMessageException ex) {
                return e.getDefaultMessage();
            }
        });
    }

    private ValidationMessages(BindingResult bindingResult) {
        populateFromBindingResult(null, bindingResult, ObjectError::getDefaultMessage);
    }

    public ValidationMessages(Error... errors) {
        this(asList(errors));
    }

    public ValidationMessages(List<Error> errors) {
        this.errors.addAll(errors);
    }

    public boolean hasErrorWithKey(Object key) {
        return errors.stream().anyMatch(e -> e.getErrorKey().equals(key + ""));
    }

    public boolean hasFieldErrors(String fieldName) {
        return errors.stream().anyMatch(e -> fieldName.equals(e.getFieldName()));
    }

    public List<Error> getFieldErrors(String fieldName) {
        return simpleFilter(errors, e -> fieldName.equals(e.getFieldName()));
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

    public void addAll(ErrorHolder messages) {
        if (messages != null) {
            addAnyErrors(messages.getErrors());
        }
    }

    public void addAll(ErrorHolder messages, ErrorConverter converter, ErrorConverter... otherConverters) {
        if (messages != null) {
            addAnyErrors(messages.getErrors(), converter, otherConverters);
        }
    }

    public void addAll(List<ValidationMessages> messages) {
        messages.forEach(list -> addAnyErrors(list.getErrors()));
    }

    public void addAll(List<ValidationMessages> messages, ErrorConverter converter, ErrorConverter... otherConverters) {
        messages.forEach(list -> addAnyErrors(list.getErrors(), converter, otherConverters));
    }

    private void addAnyErrors(List<Error> errors) {
        addAnyErrors(errors, fieldErrorsToFieldErrors(), asGlobalErrors());
    }

    private void addAnyErrors(List<Error> errors, ErrorConverter converter, ErrorConverter... otherConverters) {
        errors.forEach(e -> {
            List<Optional<Error>> optionalConversionsForThisError = simpleMap(combineLists(converter, otherConverters), fn -> fn.apply(e));
            Optional<Optional<Error>> successfullyConvertedErrorList = simpleFindFirst(optionalConversionsForThisError, Optional::isPresent);

            if (successfullyConvertedErrorList.isPresent()) {
                this.errors.add(successfullyConvertedErrorList.get().get());
            }
        });
    }

    public void addErrors(List<Error> errors) {
        this.errors.addAll(errors);
    }

    public static ValidationMessages noErrors() {
        return new ValidationMessages();
    }

    public static ValidationMessages fromBindingResult(BindingResult bindingResult) {
        return new ValidationMessages(bindingResult);
    }

    public static ValidationMessages collectValidationMessages(List<ValidationMessages> messages) {
        ValidationMessages combined = new ValidationMessages();
        combined.addAll(messages);
        return combined;
    }

    private void populateFromBindingResult(Long objectId, BindingResult bindingResult, Function<ObjectError, String> messageResolver) {

        errors.addAll(simpleMap(bindingResult.getFieldErrors(), e -> {
            String errorMessage = messageResolver.apply(e);
            return fieldError(e.getField(), e.getRejectedValue(), errorMessage);
        }));

        errors.addAll(simpleMap(bindingResult.getGlobalErrors(), e ->
            new Error("", e.getDefaultMessage(), NOT_ACCEPTABLE)
        ));

        objectName = bindingResult.getObjectName();
        this.objectId = objectId;
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
