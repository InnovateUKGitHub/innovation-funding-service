package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.*;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests to check the ApplicationRestService's interaction with the RestTemplate and the processing of its results
 */
public class SectionStatusRestServiceImplTest extends BaseRestServiceUnitTest<SectionStatusRestServiceImpl> {

    private static final String sectionRestUrl = "/section-status";

    @Override
    protected SectionStatusRestServiceImpl registerRestServiceUnderTest() {
        SectionStatusRestServiceImpl sectionRestService = new SectionStatusRestServiceImpl();
        return sectionRestService;
    }

    @Test
    public void markAsComplete() {
        long sectionId = 123L;
        long applicationId = 234L;
        long markedAsCompleteById = 345L;
        String expectedUrl = sectionRestUrl + "/mark-as-complete/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById;
        List<ValidationMessages> messages = new ArrayList<>();
        setupPostWithRestResultExpectations(expectedUrl, validationMessagesListType(), null, messages);

        RestResult<List<ValidationMessages>> result = service.markAsComplete(sectionId, applicationId, markedAsCompleteById);

        assertEquals(messages, result.getSuccess());
    }

    @Test
    public void markAsNotRequired() {
        long sectionId = 123L;
        long applicationId = 234L;
        long markedAsCompleteById = 345L;
        String expectedUrl = sectionRestUrl + "/mark-as-not-required/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById;
        setupPostWithRestResultExpectations(expectedUrl, HttpStatus.OK);

        RestResult<Void> result = service.markAsNotRequired(sectionId, applicationId, markedAsCompleteById);

        assertTrue(result.isSuccess());
    }

    @Test
    public void markAsInComplete() {
        long sectionId = 123L;
        long applicationId = 234L;
        long markedAsCompleteById = 345L;
        String expectedUrl = sectionRestUrl + "/mark-as-in-complete/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById;
        setupPostWithRestResultExpectations(expectedUrl, HttpStatus.OK);

        RestResult<Void> result = service.markAsInComplete(sectionId, applicationId, markedAsCompleteById);

        assertTrue(result.isSuccess());
    }

    @Test
    public void getCompletedSectionsByOrganisation() {
        long applicationId = 123L;
        String expectedUrl = sectionRestUrl + "/get-completed-sections-by-organisation/" + applicationId;
        Map<Long, Set<Long>> expectedResult = new HashMap<>();
        setupGetWithRestResultExpectations(expectedUrl, mapOfLongToLongsSetType(), expectedResult);

        RestResult<Map<Long, Set<Long>>> result = service.getCompletedSectionsByOrganisation(applicationId);

        assertEquals(expectedResult, result.getSuccess());
    }

    @Test
    public void getCompletedSectionIds() {
        String expectedUrl = sectionRestUrl + "/get-completed-sections/123/456";
        List<Long> returnedResponse = asList(1L, 2L, 3L);

        setupGetWithRestResultExpectations(expectedUrl, longsListType(), returnedResponse);

        // now run the method under test
        List<Long> response = service.getCompletedSectionIds(123L, 456L).getSuccess();
        assertEquals(returnedResponse, response);
    }

    @Test
    public void getIncompleteSectionIds() {

        String expectedUrl = sectionRestUrl + "/get-incomplete-sections/123";
        List<Long> returnedResponse = asList(1L, 2L, 3L);

        setupGetWithRestResultExpectations(expectedUrl, longsListType(), returnedResponse);

        // now run the method under test
        List<Long> response = service.getIncompletedSectionIds(123L).getSuccess();
        assertEquals(returnedResponse, response);
    }
}
