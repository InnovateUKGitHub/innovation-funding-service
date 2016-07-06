package com.worth.ifs.controller;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorHolder;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.function.Supplier;

/**
 * A helper class that wraps the standard BindingResult and allows us to more easily work with the various mechanisms that
 * we use to convey Error messages to the front end
 */
public class ValidationHandler {

    private BindingResult bindingResult;
    private BindingResultTarget bindingResultTarget;

    private ValidationHandler(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public ValidationHandler addAnyErrors(ErrorHolder errors, ErrorToObjectErrorConverter converter) {
        return addAnyErrors(errors.getErrors(), converter);
    }

    private ValidationHandler addAnyErrors(List<Error> errors, ErrorToObjectErrorConverter errorToObjectErrorFn) {
        errors.forEach(e -> bindingResult.addError(errorToObjectErrorFn.apply(e)));
        return this;
    }

    public ValidationHandler setBindingResultTarget(BindingResultTarget bindingResultTarget) {
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
    public String failNowOrSucceedWith(Supplier<String> failureHandler, Supplier<String> successHandler) {

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

    public static ValidationHandler newBindingResultHandler(BindingResult bindingResult) {
        return new ValidationHandler(bindingResult);
    }
}
