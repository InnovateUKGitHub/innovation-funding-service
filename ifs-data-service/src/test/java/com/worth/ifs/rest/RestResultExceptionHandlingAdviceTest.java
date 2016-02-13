package com.worth.ifs.rest;

import com.worth.ifs.BaseIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.worth.ifs.commons.error.Errors.internalServerErrorError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class RestResultExceptionHandlingAdviceTest extends BaseIntegrationTest {

    @Autowired
    private RestResultExceptionHandlingAdviceTestRestController applicableController;

    @Test
    public void testSuccessMethodReturnsSuccessNormally() {
        RestResult<String> result = applicableController.successfulMethod();
        assertTrue(result.isSuccess());
        assertEquals("Success", result.getSuccessObject());
    }

    @Test
    public void testFailureMethodReturnsFailureNormally() {
        RestResult<String> result = applicableController.failingMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError("Failure")));
    }

    @Test
    public void testNullReturningMethodReturnsDefaultFailure() {
        RestResult<String> result = applicableController.nullReturningMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError("An unexpected error occurred")));
    }

    @Test
    public void testExceptionThrowingMethodReturnsDefaultFailure() {
        RestResult<String> result = applicableController.exceptionThrowingMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError("Exception")));
    }

    @Test
    public void testPackagePrivateMethodNotAffectedByAdvice() {
        assertNull(applicableController.packagePrivateMethod());
    }

    @Test
    public void testNonRequestMappingAnnotatedMethodNotAffectedByAdvice() {
        assertNull(applicableController.nonRequestMappingAnnotatedMethod());
    }

    @Test
    public void testNonRestResultReturningMethodNotAffectedByAdvice() {
        assertNull(applicableController.nonRestResultReturningMethod());
    }
}
