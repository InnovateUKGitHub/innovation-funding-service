package com.worth.ifs.rest;

import com.worth.ifs.BaseIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.worth.ifs.commons.error.Errors.internalServerErrorError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class RestResultExceptionHandlingAdviceTest extends BaseIntegrationTest {

    @Autowired
    private RestResultExceptionHandlingAdviceTestController testController;

    @Test
    public void testSuccessMethodReturnsSuccessNormally() {
        RestResult<String> result = testController.successfulMethod();
        assertTrue(result.isSuccess());
        assertEquals("Success", result.getSuccessObject());
    }

    @Test
    public void testFailureMethodReturnsFailureNormally() {
        RestResult<String> result = testController.failedMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError("Failure")));
    }

    @Test
    public void testExceptionThrowingMethodReturnsDefaultFailure() {
        RestResult<String> result = testController.exceptionThrowingMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError("Exception")));
    }
}
