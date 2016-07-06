package com.worth.ifs.controller;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

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

    public ControllerValidationHandler addAnyErrors(ErrorHolder errors, ErrorToObjectErrorConverter converter) {
        return addAnyErrors(errors.getErrors(), converter);
    }

    private ControllerValidationHandler addAnyErrors(List<Error> errors, ErrorToObjectErrorConverter errorToObjectErrorFn) {
        errors.forEach(e -> bindingResult.addError(errorToObjectErrorFn.apply(e)));
        return this;
    }

    public ControllerValidationHandler setBindingResultTarget(BindingResultTarget bindingResultTarget) {
        this.bindingResultTarget = bindingResultTarget;
        return this;
    }

    /**
     * If there are currently errors, fail immediately invoking the given failureHandler.  Otehrwise, continue to process
     * the successHandler.  Assumption is that we would normally be returning view names from the given suppliers.
     *
     * @param failureHandler
     * @param successHandler
     * @return
     */
    public String andFailNowOrSucceed(Supplier<String> failureHandler, Supplier<String> successHandler) {

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

    public static ControllerValidationHandler newBindingResultHandler(BindingResult bindingResult) {
        return new ControllerValidationHandler(bindingResult);
    }
}
