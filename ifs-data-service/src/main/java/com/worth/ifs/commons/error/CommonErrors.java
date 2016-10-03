package com.worth.ifs.commons.error;

import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.util.CollectionFunctions.simpleJoiner;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
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
        return new Error(LENGTH_REQUIRED, singletonList(maxFileSizeBytes), LENGTH_REQUIRED);
    }

    public static Error payloadTooLargeError(long maxFileSizeBytes) {
        return new Error(PAYLOAD_TOO_LARGE, singletonList(maxFileSizeBytes), PAYLOAD_TOO_LARGE);
    }

    public static Error unsupportedMediaTypeByNameError(List<String> validMediaTypes) {
        return new Error(UNSUPPORTED_MEDIA_TYPE, singletonList(simpleJoiner(validMediaTypes, ", ")), UNSUPPORTED_MEDIA_TYPE);
    }

    public static Error unsupportedMediaTypeError(List<MediaType> validMediaTypes) {
        return unsupportedMediaTypeByNameError(simpleMap(validMediaTypes, Object::toString));
    }

    public static Error badRequestError(String errorKey) {
        return new Error(errorKey, BAD_REQUEST);
    }

    public static Error internalServerErrorError() {
        return new Error(GENERAL_UNEXPECTED_ERROR, INTERNAL_SERVER_ERROR);
    }

    public static Error forbiddenError() {
        return new Error(GENERAL_FORBIDDEN, FORBIDDEN);
    }

    public static Error forbiddenError(Enum<?> key) {
        return new Error(key, FORBIDDEN);
    }

    public static Error forbiddenError(Enum<?> key, List<Object> arguments) {
        return new Error(key, arguments, FORBIDDEN);
    }

    public static Error incorrectTypeError(Class<?> clazz, List<Object> arguments) {

        List<Object> allArguments = new ArrayList<>();
        allArguments.add(clazz.getSimpleName());
        allArguments.addAll(arguments);

        return new Error(GENERAL_INCORRECT_TYPE.getErrorKey(), allArguments, GENERAL_INCORRECT_TYPE.getCategory());
    }

    public static Error incorrectTypeError(Class<?> clazz, Object... arguments) {
        return incorrectTypeError(clazz, asList(arguments));
    }
}
