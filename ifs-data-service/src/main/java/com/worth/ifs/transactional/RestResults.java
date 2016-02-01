package com.worth.ifs.transactional;

import static com.worth.ifs.transactional.BaseTransactionalService.Failures.UNEXPECTED_ERROR;
import static com.worth.ifs.transactional.RestResult.restFailure;
import static com.worth.ifs.transactional.RestResult.restSuccess;
import static org.springframework.http.HttpStatus.*;

/**
 *
 */
public class RestResults {

    public static RestResult<Void> ok2() {
        return restSuccess(OK);
    }

    public static <T> RestResult<T> created2(T body) {
        return restSuccess(body, CREATED);
    }

    public static RestResult<Void> accepted() {
        return restSuccess(ACCEPTED);
    }

    public static RestResult<Void> noContent2() {
        return restSuccess(NO_CONTENT);
    }





    //
    // 5xx ERRORS
    //

    public static RestResult<Void> internalServerError2() {
        return internalServerError2("An unexpected error occurred");
    }

    public static RestResult<Void> internalServerError2(String message) {
        return restFailure(UNEXPECTED_ERROR, message, INTERNAL_SERVER_ERROR);
    }
}
