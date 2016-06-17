package com.worth.ifs.controller;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceFailure;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.validation.BindingResult;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.emptyList;

/**
 * A utility to easily convert failures from the data layer into validation errors for the web layer
 */
public class RestFailuresToValidationErrorBindingUtils {

    public static <T> ServiceResult<T> bindAnyErrorsToField(ServiceResult<T> serviceResult, String fieldName, BindingResult bindingResult, BindingResultTarget bindingResultTarget) {

        List<String> errorKeys = serviceResult.handleSuccessOrFailure(
                failure -> lookupValidationErrorsFromServiceFailures(failure),
                success -> emptyList()
        );

        if (!errorKeys.isEmpty()) {
            addErrorsToForm(fieldName, bindingResultTarget, bindingResult, errorKeys);
        }

        return serviceResult;
    }

    private static void addErrorsToForm(String fieldName, BindingResultTarget bindingResultTarget, BindingResult bindingResult, List<String> errorKeys) {
        registerValidationErrorsWithBindingResult(fieldName, bindingResult, errorKeys);
        bindingResultTarget.setBindingResult(bindingResult);
        bindingResultTarget.setObjectErrors(bindingResult.getAllErrors());
    }

    private static void registerValidationErrorsWithBindingResult(String fieldName, BindingResult bindingResult, List<String> errorKeys) {
        errorKeys.forEach(error -> bindingResult.rejectValue(fieldName, error));
    }

    private static List<String> lookupValidationErrorsFromServiceFailures(ServiceFailure failure) {
        return simpleMap(failure.getErrors(), Error::getErrorKey);
    }
}
