package com.worth.ifs.commons.error;

import java.util.List;

import static com.worth.ifs.application.transactional.ServiceFailureKeys.GENERAL_NOT_FOUND_ENTITY;
import static com.worth.ifs.application.transactional.ServiceFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static com.worth.ifs.util.CollectionFunctions.simpleJoiner;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.*;

/**
 *
 */
public class Errors {

    public static Error notFoundError(Class<?> entityClazz, List<Object> arguments) {
        return new Error(GENERAL_NOT_FOUND_ENTITY, entityClazz + " not found", arguments, NOT_FOUND);
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

    public static Error internalServerErrorError() {
        return internalServerErrorError(null);
    }

    public static Error internalServerErrorError(String message) {
        return new Error(GENERAL_UNEXPECTED_ERROR, message, INTERNAL_SERVER_ERROR);
    }

}
