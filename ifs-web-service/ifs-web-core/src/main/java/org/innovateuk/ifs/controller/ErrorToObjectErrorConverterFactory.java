package org.innovateuk.ifs.controller;

import org.innovateuk.ifs.commons.error.Error;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Optional;
import java.util.function.Function;

/**
 * Factory class for creating specific useful implementations of ErrorToObjectErrorConverter
 */
public class ErrorToObjectErrorConverterFactory {

    public static ErrorToObjectErrorConverter toField(String field) {
        return e -> Optional.of(newFieldError(e, field, e.getFieldRejectedValue()));
    }

    public static ErrorToObjectErrorConverter fieldErrorsToFieldErrors() {
        return e -> {
            if (e.isFieldError()) {
                return Optional.of(newFieldError(e, e.getFieldName(), e.getFieldRejectedValue()));
            }
            return Optional.empty();
        };
    }

    /**
     * Manually map a field {@link Error} to a {@link FieldError}.
     *
     * @param mappingFunction returning the desired {@link FieldError}.
     * @return converter function between {@link Error} and {@link FieldError}.
     */
    public static ErrorToObjectErrorConverter fieldErrorsToFieldErrors(Function<Error, FieldError> mappingFunction) {
        return e -> {
            if (e.isFieldError()) {
                return Optional.of(mappingFunction.apply(e));
            }

            return Optional.empty();
        };
    }

    public static ErrorToObjectErrorConverter toObject(String objectName) {
        return e -> Optional.of(new ObjectError(objectName, new String[]{e.getErrorKey()}, e.getArguments().toArray(), null));
    }

    public static ErrorToObjectErrorConverter asGlobalErrors() {
        return e -> Optional.of(new ObjectError("", new String[]{e.getErrorKey()}, e.getArguments().toArray(), null));
    }

    public static ErrorToObjectErrorConverter mappingErrorKeyToField(String errorKey, String targetField) {
        return e -> {
            if (errorKey.equals(e.getErrorKey())) {
                return Optional.of(newFieldError(e, targetField, e.getFieldRejectedValue()));
            }
            return Optional.empty();
        };
    }

    public static ErrorToObjectErrorConverter mappingErrorKeyToField(Enum<?> errorKey, String targetField) {
        return mappingErrorKeyToField(errorKey.name(), targetField);
    }

    /**
     * Given any error, map it to a target field name on the error holder.
     *
     * @param mappingFunction returning the target field name. If empty,
     *                        no {@link FieldError} will be added to the error holder.
     * @return converter function between {@link Error} and {@link FieldError}.
     */
    public static ErrorToObjectErrorConverter mappingErrorToField(Function<Error, String> mappingFunction) {
        return e -> {
            String targetField = mappingFunction.apply(e);

            if (targetField == null || targetField.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(newFieldError(e, targetField, e.getFieldRejectedValue()));
        };
    }

    /**
     * Given a field error, map it to a target field name on the error holder.
     *
     * @param mappingFunction returning the non-empty target field name. If empty,
     *                        no {@link FieldError} will be added to the error holder.
     * @return converter function between {@link Error} and {@link FieldError}.
     */
    public static ErrorToObjectErrorConverter mappingFieldErrorToField(Function<Error, String> mappingFunction) {
        return e -> {
            if (e.isFieldError()) {
                return mappingErrorToField(mappingFunction).apply(e);
            }

            return Optional.empty();
        };
    }

    public static FieldError newFieldError(Error e, String fieldName, Object rejectedValue) {
        return newFieldError(e, fieldName, rejectedValue, e.getErrorKey());
    }

    public static FieldError newFieldError(Error e, String fieldName, Object rejectedValue, String errorKey) {
        return new FieldError("", fieldName, rejectedValue, true, new String[]{errorKey}, e.getArguments().toArray(), null);
    }
}
