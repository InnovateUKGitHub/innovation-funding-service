package com.worth.ifs.rest;

import com.worth.ifs.commons.rest.RestResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.worth.ifs.commons.error.Errors.internalServerErrorError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;

/**
 *
 */
@RestController
public class RestResultExceptionHandlingAdviceTestController {

    @RequestMapping
    public RestResult<String> successfulMethod() {
        return restSuccess("Success");
    }

    @RequestMapping
    public RestResult<String> failedMethod() {
        return restFailure(internalServerErrorError("Failure"));
    }

    @RequestMapping
    public RestResult<String> exceptionThrowingMethod() {
        throw new RuntimeException("Exception");
    }
}
