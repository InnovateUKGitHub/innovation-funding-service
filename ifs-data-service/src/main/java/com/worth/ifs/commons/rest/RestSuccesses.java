package com.worth.ifs.commons.rest;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.springframework.http.HttpStatus.*;

/**
 *
 */
public class RestSuccesses {

    public static RestResult<Void> okRestSuccess() {
        return restSuccess(OK);
    }

    public static <T> RestResult<T> createdRestSuccess(T body) {
        return restSuccess(body, CREATED);
    }

    public static RestResult<Void> acceptedRestSuccess() {
        return restSuccess(ACCEPTED);
    }

    public static RestResult<Void> noContentRestSuccess() {
        return restSuccess(NO_CONTENT);
    }
}
