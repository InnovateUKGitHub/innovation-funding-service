package org.innovateuk.ifs.rest;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.web.bind.annotation.*;

import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;

/**
 * A test Controller for tests in {@link RestResultExceptionHandlingAdviceIntegrationTest}
 */
@RestController
public class RestResultExceptionHandlingAdviceIntegrationTestRestController {

    @RequestMapping("/success-test")
    public RestResult<String> successfulRequestMethod() {
        return restSuccess("Success");
    }

    @GetMapping("/success-test")
    public RestResult<String> successfulGetMethod() {
        return restSuccess("Success");
    }

    @PostMapping("/success-test")
    public RestResult<String> successfulPostMethod() {
        return restSuccess("Success");
    }

    @PutMapping("/success-test")
    public RestResult<String> successfulPutMethod() {
        return restSuccess("Success");
    }

    @DeleteMapping("/success-test")
    public RestResult<String> successfulDeleteMethod() {
        return restSuccess("Success");
    }

    @RequestMapping("/failure-test")
    public RestResult<String> failingRequestMethod() {
        return restFailure(internalServerErrorError());
    }

    @GetMapping("/failure-test")
    public RestResult<String> failingGetMethod() {
        return restFailure(internalServerErrorError());
    }

    @PostMapping("/failure-test")
    public RestResult<String> failingPostMethod() {
        return restFailure(internalServerErrorError());
    }

    @PutMapping("/failure-test")
    public RestResult<String> failingPutMethod() {
        return restFailure(internalServerErrorError());
    }

    @DeleteMapping("/failure-test")
    public RestResult<String> failingDeleteMethod() {
        return restFailure(internalServerErrorError());
    }

    @RequestMapping("/null-test")
    public RestResult<String> nullReturningRequestMethod() {
        return null;
    }

    @GetMapping("/null-test")
    public RestResult<String> nullReturningGetMethod() {
        return null;
    }

    @PostMapping("/null-test")
    public RestResult<String> nullReturningPostMethod() {
        return null;
    }

    @PutMapping("/null-test")
    public RestResult<String> nullReturningPutMethod() {
        return null;
    }

    @DeleteMapping("/null-test")
    public RestResult<String> nullReturningDeleteMethod() {
        return null;
    }

    @RequestMapping("/exception-test")
    public RestResult<String> exceptionThrowingRequestMethod() {
        throw new RuntimeException();
    }

    @GetMapping("/exception-test")
    public RestResult<String> exceptionThrowingGetMethod() {
        throw new RuntimeException();
    }

    @PostMapping("/exception-test")
    public RestResult<String> exceptionThrowingPostMethod() {
        throw new RuntimeException();
    }

    @PutMapping("/exception-test")
    public RestResult<String> exceptionThrowingPutMethod() {
        throw new RuntimeException();
    }

    @DeleteMapping("/exception-test")
    public RestResult<String> exceptionThrowingDeleteMethod() {
        throw new RuntimeException();
    }

    @RequestMapping("/package-private-exception-test")
    RestResult<String> packagePrivateRequestMethod() {
        return null;
    }

    @GetMapping("/package-private-exception-test")
    RestResult<String> packagePrivateGetMethod() {
        return null;
    }

    @PostMapping("/package-private-exception-test")
    RestResult<String> packagePrivatePostMethod() {
        return null;
    }

    @PutMapping("/package-private-exception-test")
    RestResult<String> packagePrivatePutMethod() {
        return null;
    }

    @DeleteMapping("/package-private-exception-test")
    RestResult<String> packagePrivateDeleteMethod() {
        return null;
    }

    public RestResult<String> nonRequestMappingAnnotatedMethod() {
        return null;
    }

    @RequestMapping("/non-rest-result-returning-method")
    public String nonRestResultReturningRequestMethod() {
        return null;
    }

    @GetMapping("/non-rest-result-returning-method")
    public String nonRestResultReturningGetMethod() {
        return null;
    }

    @PostMapping("/non-rest-result-returning-method")
    public String nonRestResultReturningPostMethod() {
        return null;
    }

    @PutMapping("/non-rest-result-returning-method")
    public String nonRestResultReturningPutMethod() {
        return null;
    }

    @DeleteMapping("/non-rest-result-returning-method")
    public String nonRestResultReturningDeleteMethod() {
        return null;
    }
}
