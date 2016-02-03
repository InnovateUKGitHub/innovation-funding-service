package com.worth.ifs.commons.rest;

import static com.worth.ifs.commons.error.Errors.internalServerErrorError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;

/**
 * A factory for producing some common RestResult failure cases in a consistent manner.
 */
public class RestFailures {

    public static RestResult<Void> internalServerErrorRestFailure() {
        return internalServerErrorRestFailure("An unexpected error occurred");
    }

    public static RestResult<Void> internalServerErrorRestFailure(String message) {
        return restFailure(internalServerErrorError(message));
    }
}
