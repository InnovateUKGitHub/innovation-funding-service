package com.worth.ifs.commons.service;


import com.worth.ifs.commons.error.Error;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.badRequestError;
import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.service.ServiceResult.*;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class ServiceResultTest {

    @Test
    public void testAggregateSuccess() {
        final List<ServiceResult<String>> list = asList("1", "2", "3").stream().map(ServiceResult::serviceSuccess).collect(toList());
        final ServiceResult<List<String>> serviceResult = aggregate(list); // Method under test
        assertTrue(serviceResult.isSuccess());
        assertFalse(serviceResult.getSuccessObject().isEmpty());
        assertEquals(asList("1", "2", "3"), serviceResult.getSuccessObject());
    }

    @Test
    public void testAggregateVarargsSuccess() {
        final ServiceResult<List<String>> serviceResult = aggregate(serviceSuccess("1"), serviceSuccess("2"), serviceSuccess("3")); // Method under test
        assertTrue(serviceResult.isSuccess());
        assertFalse(serviceResult.getSuccessObject().isEmpty());
        assertEquals(asList("1", "2", "3"), serviceResult.getSuccessObject());
    }

    @Test
    public void testAggregateFailure() {
        final List<ServiceResult<String>> list = asList("1", "2","3").stream().map(i -> ServiceResult.<String>serviceFailure(new Error(i, INTERNAL_SERVER_ERROR))).collect(toList());
        final ServiceResult<List<String>> serviceResult = aggregate(list); // Method under test
        assertTrue(serviceResult.isFailure());
        assertNotNull(serviceResult.getFailure());
        assertEquals(3, serviceResult.getFailure().getErrors().size());
    }

    @Test
    public void testAggregateFailureAndSuccess() {
        final List<ServiceResult<String>> fails = asList("1", "2", "3").stream().map(i -> ServiceResult.<String>serviceFailure(new Error(i, INTERNAL_SERVER_ERROR))).collect(toList());
        final List<ServiceResult<String>> success = asList("1", "2", "3").stream().map(ServiceResult::serviceSuccess).collect(toList());
        final List<ServiceResult<String>> successAndFails = combineLists(success, fails);
        final ServiceResult<List<String>> serviceResult = aggregate(successAndFails); // Method under test
        // A fail should result in a fail being returned.
        assertTrue(serviceResult.isFailure());
        assertNotNull(serviceResult.getFailure());
        assertEquals(3, serviceResult.getFailure().getErrors().size());
    }

    @Test
    public void testEmptyList() {
        final List<ServiceResult<String>> empty = new ArrayList();
        final ServiceResult<List<String>> serviceResult = aggregate(empty); // Method under test
        assertTrue(serviceResult.isSuccess());
        assertNotNull(serviceResult.getSuccessObject());
        assertTrue(serviceResult.getSuccessObject().isEmpty());
    }

    @Test
    public void testProcessAnyFailuresOrSucceed() {

        List<ServiceResult<Void>> resultsWithErrors = asList(serviceFailure(badRequestError("Error 1")), serviceSuccess(), serviceFailure(asList(internalServerErrorError(), badRequestError("Error 3"))));
        ServiceResult<String> result = processAnyFailuresOrSucceed(resultsWithErrors, serviceSuccess("Should not see this success"));

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(badRequestError("Error 1"), internalServerErrorError(), badRequestError("Error 3")));
    }



}
