package org.innovateuk.ifs.rest;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.junit.Assert.*;

/**
 * This tests that Service methods returning RestResults are handled by the RestResultExceptionHandlingAdvice and converts
 * exceptions and nulls into failing RestResults.
 */
public class RestResultExceptionHandlingAdviceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RestResultExceptionHandlingAdviceIntegrationTestRestController applicableController;

    @Test
    public void testSuccessMethodsReturnSuccessNormally() {
        assertSuccessfulRestResult(() -> applicableController.successfulRequestMethod());
        assertSuccessfulRestResult(() -> applicableController.successfulGetMethod());
        assertSuccessfulRestResult(() -> applicableController.successfulPostMethod());
        assertSuccessfulRestResult(() -> applicableController.successfulPutMethod());
        assertSuccessfulRestResult(() -> applicableController.successfulDeleteMethod());
    }

    @Test
    public void testFailureMethodsReturnFailureNormally() {
        assertFailedRestResult(() -> applicableController.failingRequestMethod());
        assertFailedRestResult(() -> applicableController.failingGetMethod());
        assertFailedRestResult(() -> applicableController.failingPostMethod());
        assertFailedRestResult(() -> applicableController.failingPutMethod());
        assertFailedRestResult(() -> applicableController.failingDeleteMethod());
    }

    @Test
    public void testNullReturningMethodsReturnDefaultFailure() {
        assertFailedRestResult(() -> applicableController.nullReturningRequestMethod());
        assertFailedRestResult(() -> applicableController.nullReturningGetMethod());
        assertFailedRestResult(() -> applicableController.nullReturningPostMethod());
        assertFailedRestResult(() -> applicableController.nullReturningPutMethod());
        assertFailedRestResult(() -> applicableController.nullReturningDeleteMethod());
    }

    @Test
    public void testExceptionThrowingMethodsReturnDefaultFailure() {
        assertFailedRestResult(() -> applicableController.exceptionThrowingRequestMethod());
        assertFailedRestResult(() -> applicableController.exceptionThrowingGetMethod());
        assertFailedRestResult(() -> applicableController.exceptionThrowingPostMethod());
        assertFailedRestResult(() -> applicableController.exceptionThrowingPutMethod());
        assertFailedRestResult(() -> applicableController.exceptionThrowingDeleteMethod());
    }

    @Test
    public void testPackagePrivateMethodsNotAffectedByAdvice() {
        assertNull(applicableController.packagePrivateRequestMethod());
        assertNull(applicableController.packagePrivateGetMethod());
        assertNull(applicableController.packagePrivatePostMethod());
        assertNull(applicableController.packagePrivatePutMethod());
        assertNull(applicableController.packagePrivateDeleteMethod());
    }

    @Test
    public void testNonRequestMappingAnnotatedMethodNotAffectedByAdvice() {
        assertNull(applicableController.nonRequestMappingAnnotatedMethod());
    }

    @Test
    public void testNonRestResultReturningMethodsNotAffectedByAdvice() {
        assertNull(applicableController.nonRestResultReturningRequestMethod());
        assertNull(applicableController.nonRestResultReturningGetMethod());
        assertNull(applicableController.nonRestResultReturningPostMethod());
        assertNull(applicableController.nonRestResultReturningPutMethod());
        assertNull(applicableController.nonRestResultReturningDeleteMethod());
    }

    private void assertSuccessfulRestResult(Supplier<RestResult<String>> restResultSupplier) {
        RestResult<String> result = restResultSupplier.get();
        assertTrue(result.isSuccess());
        assertEquals("Success", result.getSuccessObject());
    }

    private void assertFailedRestResult(Supplier<RestResult<String>> restResultSupplier) {
        RestResult<String> result = restResultSupplier.get();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }
}
