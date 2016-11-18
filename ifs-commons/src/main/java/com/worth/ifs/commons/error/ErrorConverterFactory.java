package com.worth.ifs.commons.error;

import java.util.Optional;

import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.error.Error.globalError;

/**
 * A factory class that produces various ErrorConverter implementations
 */
public class ErrorConverterFactory {

    public static ErrorConverter toField(String field) {
        return e -> Optional.of(newFieldError(e, field, e.getFieldRejectedValue()));
    }

    public static ErrorConverter fieldErrorsToFieldErrors() {
        return e -> {
            if (e.isFieldError()) {
                return Optional.of(newFieldError(e, e.getFieldName(), e.getFieldRejectedValue()));
            }
            return Optional.empty();
        };
    }

    public static ErrorConverter asGlobalErrors() {
        return e -> Optional.of(globalError(e.getErrorKey()));
    }

    public static ErrorConverter mappingErrorKeyToField(String errorKey, String targetField) {
        return e -> {
            if (errorKey.equals(e.getErrorKey())) {
                return Optional.of(newFieldError(e, targetField, e.getFieldRejectedValue()));
            }
            return Optional.empty();
        };
    }

    public static ErrorConverter mappingErrorKeyToField(Enum<?> errorKey, String targetField) {
        return mappingErrorKeyToField(errorKey.name(), targetField);
    }

    private static Error newFieldError(Error e, String fieldName, Object rejectedValue) {
        return fieldError(fieldName, rejectedValue, e.getErrorKey(), e.getArguments().toArray());
    }
}
