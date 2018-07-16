package org.innovateuk.ifs.commons.error;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import java.io.Serializable;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.error.Error.globalError;
import static org.innovateuk.ifs.commons.error.ErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.commons.error.ErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

/**
 * Resource object to return validation messages on rest calls.
 */
public class ValidationMessages implements ErrorHolder, Serializable {

    private String objectName;
    private Long objectId;
    private Set<Error> errors = new LinkedHashSet<>();

    public ValidationMessages() {

    }

    public ValidationMessages(Long objectId) {
        this.objectId = objectId;
    }

    public ValidationMessages(BindingResult bindingResult) {
        populateFromBindingResult(null, bindingResult);
    }

    public ValidationMessages(Long objectId, BindingResult bindingResult) {
        populateFromBindingResult(objectId, bindingResult);
    }

    public ValidationMessages(Long objectId, ValidationMessages messages) {
        this.objectId = objectId;
        this.errors = new LinkedHashSet<>(messages.getErrors());
    }

    public <T> ValidationMessages(Set<ConstraintViolation<T>> constraintViolations) {
        List<Error> fieldErrors = simpleMap(constraintViolations,
                violation -> fieldError(violation.getPropertyPath().toString(),
                        violation.getLeafBean(),
                        stripCurlyBrackets(violation.getMessageTemplate())));

        errors.addAll(fieldErrors);
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

            successfullyConvertedErrorList.ifPresent(error -> this.errors.add(error.get()));
        });
    }

    public void addErrors(List<Error> errors) {
        this.errors.addAll(errors);
    }

    public static ValidationMessages noErrors() {
        return new ValidationMessages();
    }

    public static ValidationMessages noErrors(Long objectId) {
        return new ValidationMessages(objectId);
    }

    public static ValidationMessages fromErrors(List<Error> errors) {
        return new ValidationMessages(errors);
    }

    public static ValidationMessages fromBindingResult(BindingResult bindingResult) {
        return new ValidationMessages(bindingResult);
    }

    public static ValidationMessages collectValidationMessages(List<ValidationMessages> messages) {
        ValidationMessages combined = new ValidationMessages();
        combined.addAll(messages);
        return combined;
    }

    private void populateFromBindingResult(Long objectId, BindingResult bindingResult) {

        List<Error> fieldErrors = simpleMap(bindingResult.getFieldErrors(), e -> fieldError(e.getField(), e.getRejectedValue(),
                getErrorKeyFromBindingError(e), getArgumentsFromBindingError(e)));

        List<Error> globalErrors = simpleMap(bindingResult.getGlobalErrors(), e -> globalError(getErrorKeyFromBindingError(e),
                getArgumentsFromBindingError(e)));

        errors.addAll(combineLists(fieldErrors, globalErrors));

        objectName = bindingResult.getObjectName();
        this.objectId = objectId;
    }

    // The Binding Errors in the API contain error keys which can be looked up in the web layer (or used in another client
    // of this API) to produce plain english messages, but it is not the responsibility of the API to produce plain
    // english messages.  Therefore, the "defaultMessage" value in these errors is actually the key that is needed by
    // the web layer to produce the appropriate messages e.g. "validation.standard.email.length.max".
    //
    // The format we receive these in at this point is "{validation.standard.email.length.max}", so we need to ensure
    // that the curly brackets are stripped off before returning to the web layer
    private String getErrorKeyFromBindingError(ObjectError e) {

        String messageKey = e.getDefaultMessage();

        if (messageKey == null) {
            return null;
        }

        return stripCurlyBrackets(messageKey);
    }

    private String stripCurlyBrackets(String key) {
        if (key.startsWith("{") && key.endsWith("}")) {
            return key.substring(1, key.length() - 1);
        }

        return key;
    }

    //
    // The arguments provided by the Binding Errors here include as their first argument a version of all the error message
    // information itself stored as a MessageSourceResolvable.  We don't want to be sending this across to the web layer
    // as it's useless information.  We also can't really filter it out entirely because the resource bundle entries in the
    // web layer expect the useful arguments from a Binding Error to be in a particular position in order to work (for instance,
    //
    // "validation.standard.lastname.length.min=Your last name should have at least {2} characters"
    //
    // expects the actual useful argument to be in array index 2, and this is a resource bundle argument that potentially
    // could be got from either the data layer or the web layer, so it's best that we retain the original order of arguments
    // in the data layer to make these resource bundle entries reusable.  Therefore, we're best off just replacing the
    // MessageSourceResolvable argument with a blank entry.
    //
    private List<Object> getArgumentsFromBindingError(ObjectError e) {
        Object[] originalArguments = e.getArguments();

        if (originalArguments == null || originalArguments.length == 0) {
            return emptyList();
        }
        return simpleMap(asList(originalArguments), arg -> getValidMessageArgument(arg).orElse(""));
    }

    private Optional<Object> getValidMessageArgument(Object arg) {

        if (arg == null) {
            return Optional.ofNullable(arg);
        }

        if(arg instanceof DefaultMessageSourceResolvable) {
            return Optional.empty();
        }

        if (arg instanceof MessageSourceResolvable) {
            return Optional.of((((MessageSourceResolvable) arg).getDefaultMessage()));
        }

        if (arg.getClass().isArray() && ((Object[]) arg).length == 0) {
            return Optional.empty();
        }

        return Optional.of(arg);
    }

    public static void rejectValue(Errors errors, String fieldName, String errorKey, Object... arguments) {
        errors.rejectValue(fieldName, errorKey, arguments, errorKey);
    }

    public static void reject(Errors errors, String errorKey, Object... arguments) {
        errors.reject(errorKey, arguments, errorKey);
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
