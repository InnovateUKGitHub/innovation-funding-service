package com.worth.ifs.transactional;

import static com.worth.ifs.transactional.BaseTransactionalService.Failures.UNEXPECTED_ERROR;
import static com.worth.ifs.transactional.RestResult.restFailure;
import static com.worth.ifs.transactional.RestResult.restSuccess;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public class RestResults {

    public static RestResult<Void> ok() {
        return restSuccess(OK);
    }

    public static RestResult<Void> accepted() {
        return restSuccess(ACCEPTED);
    }

    public static RestResult<Void> internalServerError2() {
        return restFailure(UNEXPECTED_ERROR, "An unexpected error occurred", INTERNAL_SERVER_ERROR);
    }
}
