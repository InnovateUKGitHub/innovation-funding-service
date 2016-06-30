package com.worth.ifs.controller;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A helper class that wraps the standard BindingResult and allows us to more easily work with the various mechanisms that
 * we use to convey Error messages to the front end
 */
public class ControllerValidationHandler {

    private static Function<Error, FieldError> errorKeyAsFieldNameErrorMapper = e -> new FieldError("", e.getErrorKey(), e.getErrorMessage());

    private BindingResult bindingResult;
    private BindingResultTarget bindingResultTarget;

    private ControllerValidationHandler(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public ControllerValidationHandler addErrorsAsFieldErrors(ErrorHolder errors, String field) {
        return addErrorsAsObjectErrors(errors, fixedFieldNameErrorMapper(field));
    }

    public ControllerValidationHandler addErrorsAsFieldErrors(ErrorHolder errors) {
        return addErrorsAsObjectErrors(errors, errorKeyAsFieldNameErrorMapper);
    }

    private ControllerValidationHandler addErrorsAsObjectErrors(ErrorHolder errors, Function<Error, ? extends ObjectError> errorToObjectErrorFn) {
        return addErrorsAsObjectErrors(errors.getErrors(), errorToObjectErrorFn);
    }

    private ControllerValidationHandler addErrorsAsObjectErrors(List<Error> errors, Function<Error, ? extends ObjectError> errorToObjectErrorFn) {
        errors.forEach(e -> bindingResult.addError(errorToObjectErrorFn.apply(e)));
        return this;
    }

    public ControllerValidationHandler setBindingResultTarget(BindingResultTarget bindingResultTarget) {
        this.bindingResultTarget = bindingResultTarget;
        return this;
    }

    public String failOnErrorsOrSucceed(Supplier<String> failureHandler, Supplier<String> successHandler) {

        if (hasErrors()) {

            if (bindingResultTarget != null) {
                bindingResultTarget.setBindingResult(bindingResult);
                bindingResultTarget.setObjectErrors(bindingResult.getAllErrors());
            }

            return failureHandler.get();
        }

        return successHandler.get();
    }

    public boolean hasErrors() {
        return bindingResult.hasErrors();
    }

    private Function<Error, FieldError> fixedFieldNameErrorMapper(String field) {
        return e -> new FieldError("", field, e.getErrorMessage());
    }

    public static ControllerValidationHandler newBindingResultHandler(BindingResult bindingResult) {
        return new ControllerValidationHandler(bindingResult);
    }
}
