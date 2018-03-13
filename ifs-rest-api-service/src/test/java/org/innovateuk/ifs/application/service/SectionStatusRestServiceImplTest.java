package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
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

    private static final String sectionRestUrl = "/sectionStatus";

    @Override
    protected SectionStatusRestServiceImpl registerRestServiceUnderTest() {
        SectionStatusRestServiceImpl sectionRestService = new SectionStatusRestServiceImpl();
        return sectionRestService;
    }

    @Test
    public void markAsComplete() {
        Long sectionId = 123L;
        Long applicationId = 234L;
        Long markedAsCompleteById = 345L;
        String expectedUrl = sectionRestUrl + "/markAsComplete/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById;
        List<ValidationMessages> messages = new ArrayList<>();
        setupPostWithRestResultExpectations(expectedUrl, validationMessagesListType(), null, messages);

        RestResult<List<ValidationMessages>> result = service.markAsComplete(sectionId, applicationId, markedAsCompleteById);

        assertEquals(messages, result.getSuccess());
    }

    @Test
    public void markAsNotRequired() {
        Long sectionId = 123L;
        Long applicationId = 234L;
        Long markedAsCompleteById = 345L;
        String expectedUrl = sectionRestUrl + "/markAsNotRequired/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById;
        setupPostWithRestResultExpectations(expectedUrl, HttpStatus.OK);

        RestResult<Void> result = service.markAsNotRequired(sectionId, applicationId, markedAsCompleteById);

        assertTrue(result.isSuccess());
    }

    @Test
    public void markAsInComplete() {
        Long sectionId = 123L;
        Long applicationId = 234L;
        Long markedAsCompleteById = 345L;
        String expectedUrl = sectionRestUrl + "/markAsInComplete/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById;
        setupPostWithRestResultExpectations(expectedUrl, HttpStatus.OK);

        RestResult<Void> result = service.markAsInComplete(sectionId, applicationId, markedAsCompleteById);

        assertTrue(result.isSuccess());
    }

    @Test
    public void getCompletedSectionsByOrganisation() {
        long applicationId = 123L;
        String expectedUrl = sectionRestUrl + "/getCompletedSectionsByOrganisation/" + applicationId;
        Map<Long, Set<Long>> expectedResult = new HashMap<>();
        setupGetWithRestResultExpectations(expectedUrl, mapOfLongToLongsSetType(), expectedResult);

        RestResult<Map<Long, Set<Long>>> result = service.getCompletedSectionsByOrganisation(applicationId);

        assertEquals(expectedResult, result.getSuccess());
    }

    @Test
    public void getCompletedSectionIds() {
        String expectedUrl = sectionRestUrl + "/getCompletedSections/123/456";
        List<Long> returnedResponse = asList(1L, 2L, 3L);

        setupGetWithRestResultExpectations(expectedUrl, longsListType(), returnedResponse);

        // now run the method under test
        List<Long> response = service.getCompletedSectionIds(123L, 456L).getSuccess();
        assertEquals(returnedResponse, response);
    }

    @Test
    public void getIncompleteSectionIds() {

        String expectedUrl = sectionRestUrl + "/getIncompleteSections/123";
        List<Long> returnedResponse = asList(1L, 2L, 3L);

        setupGetWithRestResultExpectations(expectedUrl, longsListType(), returnedResponse);

        // now run the method under test
        List<Long> response = service.getIncompletedSectionIds(123L).getSuccess();
        assertEquals(returnedResponse, response);
    }
}
