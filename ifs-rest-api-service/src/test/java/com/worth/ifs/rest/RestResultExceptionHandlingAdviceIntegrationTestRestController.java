package com.worth.ifs.rest;

import com.worth.ifs.commons.rest.RestResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;

/**
 * A test Controller for tests in {@link RestResultExceptionHandlingAdviceIntegrationTest}
 */
@RestController
public class RestResultExceptionHandlingAdviceIntegrationTestRestController {

    @RequestMapping("/success-test")
    public RestResult<String> successfulMethod() {
        return restSuccess("Success");
    }

    @RequestMapping("/failure-test")
    public RestResult<String> failingMethod() {
        return restFailure(internalServerErrorError());
    }

    @RequestMapping("/null-test")
    public RestResult<String> nullReturningMethod() {
        return null;
    }

    @RequestMapping("/exception-test")
    public RestResult<String> exceptionThrowingMethod() {
        throw new RuntimeException("Exception");
    }

    @RequestMapping("/package-private-exception-test")
    RestResult<String> packagePrivateMethod() {
        return null;
    }

    public RestResult<String> nonRequestMappingAnnotatedMethod() {
        return null;
    }

    @RequestMapping("/non-rest-result-returning-method")
    public String nonRestResultReturningMethod() {
        return null;
    }
}
