package com.worth.ifs.rest;

import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This tests that Service methods returning RestResults are handled by the RestResultExceptionHandlingAdvice and converts
 * exceptions and nulls into failing RestResults.
 */
public class RestResultExceptionHandlingAdviceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RestResultExceptionHandlingAdviceIntegrationTestRestController applicableController;

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
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    @Test
    public void testNullReturningMethodReturnsDefaultFailure() {
        RestResult<String> result = applicableController.nullReturningMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    @Test
    public void testExceptionThrowingMethodReturnsDefaultFailure() {
        RestResult<String> result = applicableController.exceptionThrowingMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
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
