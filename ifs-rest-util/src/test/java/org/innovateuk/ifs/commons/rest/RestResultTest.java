package org.innovateuk.ifs.commons.rest;


import org.innovateuk.ifs.commons.error.Error;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.rest.RestResult.aggregate;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;
import static org.junit.Assert.*;

public class RestResultTest {

    @Test
    public void testAggregateSuccess() {
        final List<RestResult<String>> list = asList("1", "2", "3").stream().map(RestResult::restSuccess).collect(toList());
        final RestResult<List<String>> RestResult = aggregate(list); // Method under test
        assertTrue(RestResult.isSuccess());
        assertFalse(RestResult.getSuccess().isEmpty());
        assertEquals(asList("1", "2", "3"), RestResult.getSuccess());
    }

    @Test
    public void testAggregateFailure() {
        final List<RestResult<String>> list = asList("1", "2", "3").stream().map(i -> RestResult.<String>restFailure(new Error(i, HttpStatus.INTERNAL_SERVER_ERROR))).collect(toList());
        final RestResult<List<String>> RestResult = aggregate(list); // Method under test
        assertTrue(RestResult.isFailure());
        assertNotNull(RestResult.getFailure());
        assertEquals(3, RestResult.getFailure().getErrors().size());
    }

    @Test
    public void testAggregateFailureAndSuccess() {
        final List<RestResult<String>> fails = asList("1", "2", "3").stream().map(i -> RestResult.<String>restFailure(new Error(i, HttpStatus.INTERNAL_SERVER_ERROR))).collect(toList());
        final List<RestResult<String>> success = asList("1", "2", "3").stream().map(RestResult::restSuccess).collect(toList());
        final List<RestResult<String>> successAndFails = combineLists(success, fails);
        final RestResult<List<String>> RestResult = aggregate(successAndFails); // Method under test
        // A fail should result in a fail being returned.
        assertTrue(RestResult.isFailure());
        assertNotNull(RestResult.getFailure());
        assertEquals(3, RestResult.getFailure().getErrors().size());
    }

    @Test
    public void testEmptyList() {
        final List<RestResult<String>> empty = new ArrayList();
        final RestResult<List<String>> RestResult = aggregate(empty); // Method under test
        assertTrue(RestResult.isSuccess());
        assertNotNull(RestResult.getSuccess());
        assertTrue(RestResult.getSuccess().isEmpty());
    }

}
