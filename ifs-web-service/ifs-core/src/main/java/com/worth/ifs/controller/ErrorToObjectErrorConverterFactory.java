package com.worth.ifs.controller;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Optional;

/**
 * Factory class for creating specific useful implementations of ErrorToObjectErrorConverter
 */
public class ErrorToObjectErrorConverterFactory {

    public static ErrorToObjectErrorConverter toField(String field) {
        return e -> Optional.of(new FieldError("", field, e.getErrorMessage()));
    }

    public static ErrorToObjectErrorConverter standardFieldErrorMappings() {
        return e -> {
            if (e.isFieldError()) {
                return Optional.of(new FieldError("", e.getFieldName(), null, true, new String[] {e.getErrorKey()}, e.getArguments().toArray(), e.getErrorMessage()));
            }
            return Optional.empty();
        };
    }

    public static ErrorToObjectErrorConverter toObject(String objectName) {
        return e -> Optional.of(new ObjectError(objectName, new String[] {e.getErrorKey()}, e.getArguments().toArray(), e.getErrorMessage()));
    }

    public static ErrorToObjectErrorConverter toGlobal() {
        return e -> Optional.of(new ObjectError("", new String[] {e.getErrorKey()}, e.getArguments().toArray(), e.getErrorMessage()));
    }
}
