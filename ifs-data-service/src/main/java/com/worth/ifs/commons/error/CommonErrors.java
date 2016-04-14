package com.worth.ifs.commons.error;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.util.CollectionFunctions.simpleJoiner;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.*;

/**
 * A factory for some well-known error cases that appear in multiple classes.  This factory helps to keep some consistency
 * in the way we construct, for instance, Errors that pertain to not being able to find an entity by a set of credentials
 * (e.g. its primary key)
 */
public final class CommonErrors {

	private CommonErrors() {}
	
    public static Error notFoundError(Class<?> entityClazz, List<Object> arguments) {
        List<Object> allArguments = new ArrayList<>();
        allArguments.add(entityClazz.getSimpleName());
        allArguments.addAll(arguments);
        return new Error(GENERAL_NOT_FOUND, entityClazz.getSimpleName() + " not found", allArguments, NOT_FOUND);
    }

    public static Error notFoundError(Class<?> entityClazz, Object... arguments) {
        return notFoundError(entityClazz, asList(arguments));
    }

    public static Error lengthRequiredError(long maxFileSizeBytes) {
        return new Error(LENGTH_REQUIRED, "Please supply a valid Content-Length HTTP header.  Maximum " + maxFileSizeBytes, LENGTH_REQUIRED);
    }

    public static Error payloadTooLargeError(long maxFileSizeBytes) {
        return new Error(PAYLOAD_TOO_LARGE, "File upload was too large.  Max filesize in bytes is " + maxFileSizeBytes, PAYLOAD_TOO_LARGE);
    }

    public static Error unsupportedMediaTypeError(List<String> validMediaTypes) {
        return new Error(UNSUPPORTED_MEDIA_TYPE, "Please supply a valid Content-Type HTTP header.  Valid types are " + simpleJoiner(validMediaTypes, ", "), UNSUPPORTED_MEDIA_TYPE);
    }

    public static Error badRequestError(String message) {
        return new Error(BAD_REQUEST, message, BAD_REQUEST);
    }

    public static Error badRequestErrorWithKey(String key) {
        return new Error(key, BAD_REQUEST);
    }

    public static Error internalServerErrorError() {
        return internalServerErrorError(null);
    }

    public static Error internalServerErrorError(String message) {
        return new Error(GENERAL_UNEXPECTED_ERROR, message, INTERNAL_SERVER_ERROR);
    }

    public static Error forbiddenError(String message) {
        return new Error(GENERAL_FORBIDDEN, message, FORBIDDEN);
    }

    public static Error incorrectTypeError(Class<?> clazz, List<Object> arguments) {

        List<Object> allArguments = new ArrayList<>();
        allArguments.add(clazz.getSimpleName());
        allArguments.addAll(arguments);

        return new Error(GENERAL_INCORRECT_TYPE.getErrorKey(), clazz.getSimpleName() + " was of an incorrect type", allArguments, GENERAL_INCORRECT_TYPE.getCategory());
    }

    public static Error incorrectTypeError(Class<?> clazz, Object... arguments) {
        return incorrectTypeError(clazz, asList(arguments));
    }
}
