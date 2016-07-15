package com.worth.ifs.controller;

import com.worth.ifs.commons.error.Error;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Optional;

/**
 * Factory class for creating specific useful implementations of ErrorToObjectErrorConverter
 */
public class ErrorToObjectErrorConverterFactory {

    public static ErrorToObjectErrorConverter toField(String field) {
        return e -> Optional.of(newFieldError(e, field));
    }

    public static ErrorToObjectErrorConverter fieldErrorsToFieldErrors() {
        return e -> {
            if (e.isFieldError()) {
                return Optional.of(newFieldError(e, e.getFieldName()));
            }
            return Optional.empty();
        };
    }

    public static ErrorToObjectErrorConverter toObject(String objectName) {
        return e -> Optional.of(new ObjectError(objectName, new String[]{e.getErrorKey()}, e.getArguments().toArray(), e.getErrorMessage()));
    }

    public static ErrorToObjectErrorConverter asGlobalErrors() {
        return e -> Optional.of(new ObjectError("", new String[]{e.getErrorKey()}, e.getArguments().toArray(), e.getErrorMessage()));
    }

    public static ErrorToObjectErrorConverter mappingErrorKeyToField(String errorKey, String targetField) {
        return e -> {
            if (errorKey.equals(e.getErrorKey())) {
                return Optional.of(newFieldError(e, targetField));
            }
            return Optional.empty();
        };
    }

    public static ErrorToObjectErrorConverter mappingErrorKeyToField(Enum<?> errorKey, String targetField) {
        return mappingErrorKeyToField(errorKey.name(), targetField);
    }

    private static FieldError newFieldError(Error e, String fieldName) {
        return new FieldError("", fieldName, null, true, new String[] {e.getErrorKey()}, e.getArguments().toArray(), e.getErrorMessage());
    }
}