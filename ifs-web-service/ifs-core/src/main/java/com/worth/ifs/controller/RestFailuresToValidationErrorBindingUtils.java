package com.worth.ifs.controller;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestFailure;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.validation.BindingResult;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.emptyList;

/**
 * A utility to easily convert failures from the data layer into validation errors for the web layer
 */
public class RestFailuresToValidationErrorBindingUtils {

    public static <T> RestResult<T> bindAnyErrorsToField(RestResult<T> restResult, String fieldName, BindingResult bindingResult, BindingResultTarget bindingResultTarget) {

        List<String> errorKeys = restResult.handleSuccessOrFailure(
                failure -> lookupValidationErrorsFromServiceFailures(failure),
                success -> emptyList()
        );

        if (!errorKeys.isEmpty()) {
            addErrorsToForm(fieldName, bindingResultTarget, bindingResult, errorKeys);
        }

        return restResult;
    }

    private static void addErrorsToForm(String fieldName, BindingResultTarget bindingResultTarget, BindingResult bindingResult, List<String> errorKeys) {
        registerValidationErrorsWithBindingResult(fieldName, bindingResult, errorKeys);
        bindingResultTarget.setBindingResult(bindingResult);
        bindingResultTarget.setObjectErrors(bindingResult.getAllErrors());
    }

    private static void registerValidationErrorsWithBindingResult(String fieldName, BindingResult bindingResult, List<String> errorKeys) {
        errorKeys.forEach(error -> bindingResult.rejectValue(fieldName, error));
    }

    private static List<String> lookupValidationErrorsFromServiceFailures(RestFailure failure) {
        return simpleMap(failure.getErrors(), Error::getErrorKey);
    }
}
