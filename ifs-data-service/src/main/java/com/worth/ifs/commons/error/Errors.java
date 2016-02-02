package com.worth.ifs.commons.error;

import java.lang.*;
import java.util.List;

import static com.worth.ifs.transactional.BaseTransactionalService.Failures.UNEXPECTED_ERROR;
import static com.worth.ifs.util.CollectionFunctions.simpleJoiner;
import static org.springframework.http.HttpStatus.*;

/**
 *
 */
public class Errors {

    // TODO DW - remove "2" suffixes

    public static Error notFound(String entity, List<Object> arguments) {
        return new Error(NOT_FOUND, entity + " not found", arguments, NOT_FOUND);
    }


    //
    // ERRORS
    //

    public static Error lengthRequired2(long maxFileSizeBytes) {
        return new Error(LENGTH_REQUIRED, "Please supply a valid Content-Length HTTP header.  Maximum " + maxFileSizeBytes, LENGTH_REQUIRED);
    }

    public static Error payloadTooLarge2(long maxFileSizeBytes) {
        return new Error(PAYLOAD_TOO_LARGE, "File upload was too large.  Max filesize in bytes is " + maxFileSizeBytes, PAYLOAD_TOO_LARGE);
    }

    public static Error unsupportedMediaType2(List<String> validMediaTypes) {
        return new Error(UNSUPPORTED_MEDIA_TYPE, "Please supply a valid Content-Type HTTP header.  Valid types are " + simpleJoiner(validMediaTypes, ", "), UNSUPPORTED_MEDIA_TYPE);
    }

    public static Error badRequest2(String message) {
        return new Error(BAD_REQUEST, message, BAD_REQUEST);
    }

    public static Error internalServerError2(String message) {
        return new Error(UNEXPECTED_ERROR, message, INTERNAL_SERVER_ERROR);
    }

}
