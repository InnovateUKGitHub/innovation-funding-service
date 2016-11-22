package com.worth.ifs.application.service;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;
import java.util.concurrent.Future;

import com.worth.ifs.application.builder.SectionResourceBuilder;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.resource.SectionType;
import com.worth.ifs.application.resource.SectionResource;
import org.springframework.http.HttpStatus;

/**
 * Tests to check the ApplicationRestService's interaction with the RestTemplate and the processing of its results
 */
public class SectionRestServiceMocksTest extends BaseRestServiceUnitTest<SectionRestServiceImpl> {

    private static final String sectionRestUrl = "/section";

    @Override
    protected SectionRestServiceImpl registerRestServiceUnderTest() {
        SectionRestServiceImpl sectionRestService = new SectionRestServiceImpl();
        return sectionRestService;
    }

    @Test
    public void test_markAsComplete() {
        Long sectionId = 123L;
        Long applicationId = 234L;
        Long markedAsCompleteById = 345L;
        String expectedUrl = sectionRestUrl + "/markAsComplete/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById;
        List<ValidationMessages> messages = new ArrayList<>();
        setupPostWithRestResultExpectations(expectedUrl, validationMessagesListType(), null, messages);

        RestResult<List<ValidationMessages>> result = service.markAsComplete(sectionId, applicationId, markedAsCompleteById);

        assertEquals(messages, result.getSuccessObjectOrThrowException());
    }

    @Test
    public void test_markAsInComplete() {
        Long sectionId = 123L;
        Long applicationId = 234L;
        Long markedAsCompleteById = 345L;
        String expectedUrl = sectionRestUrl + "/markAsInComplete/" + sectionId + "/" + applicationId + "/" + markedAsCompleteById;
        setupPostWithRestResultExpectations(expectedUrl, HttpStatus.OK);

        RestResult<Void> result = service.markAsInComplete(sectionId, applicationId, markedAsCompleteById);

        assertTrue(result.isSuccess());
    }

    @Test
    public void test_getById() {
        long sectionId = 123L;
        String expectedUrl = sectionRestUrl + "/" + sectionId;
        SectionResource sectionResource = SectionResourceBuilder.newSectionResource().build();
        setupGetWithRestResultExpectations(expectedUrl, SectionResource.class, sectionResource);

        RestResult<SectionResource> result = service.getById(sectionId);

        assertEquals(sectionResource, result.getSuccessObjectOrThrowException());
    }

    @Test
    public void test_getByCompetition() {
        long competitionId = 123L;
        String expectedUrl = sectionRestUrl + "/getByCompetition/" + competitionId;
        List<SectionResource> sectionResources = SectionResourceBuilder.newSectionResource().build(2);
        setupGetWithRestResultExpectations(expectedUrl, sectionResourceListType(), sectionResources);

        RestResult<List<SectionResource>> result = service.getByCompetition(competitionId);

        assertEquals(sectionResources, result.getSuccessObjectOrThrowException());
    }

    @Test
    public void test_getCompletedSectionsByOrganisation() {
        long applicationId = 123L;
        String expectedUrl = sectionRestUrl + "/getCompletedSectionsByOrganisation/" + applicationId;
        Map<Long, Set<Long>> expectedResult = new HashMap<>();
        setupGetWithRestResultExpectations(expectedUrl, mapOfLongToLongsSetType(), expectedResult);

        RestResult<Map<Long, Set<Long>>> result = service.getCompletedSectionsByOrganisation(applicationId);

        assertEquals(expectedResult, result.getSuccessObjectOrThrowException());
    }

    @Test
    public void test_getCompletedSectionIds() {
        String expectedUrl = sectionRestUrl + "/getCompletedSections/123/456";
        List<Long> returnedResponse = asList(1L, 2L, 3L);

        setupGetWithRestResultExpectations(expectedUrl, longsListType(), returnedResponse);

        // now run the method under test
        List<Long> response = service.getCompletedSectionIds(123L, 456L).getSuccessObject();
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_getIncompleteSectionIds() {

        String expectedUrl = sectionRestUrl + "/getIncompleteSections/123";
        List<Long> returnedResponse = asList(1L, 2L, 3L);

        setupGetWithRestResultExpectations(expectedUrl, longsListType(), returnedResponse);

        // now run the method under test
        List<Long> response = service.getIncompletedSectionIds(123L).getSuccessObject();
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_getPreviousSection() throws Exception {
        long sectionId = 1L;
        String expectedUrl = sectionRestUrl + "/getPreviousSection/" + sectionId;
        SectionResource resource = SectionResourceBuilder.newSectionResource().build();
        setupGetWithRestResultAsyncExpectations(expectedUrl, SectionResource.class, resource);

        Future<RestResult<SectionResource>> result = service.getPreviousSection(sectionId);

        assertEquals(resource, result.get().getSuccessObjectOrThrowException());
    }

    @Test
    public void test_getNextSection() throws Exception {
        long sectionId = 1L;
        String expectedUrl = sectionRestUrl + "/getNextSection/" + sectionId;
        SectionResource resource = SectionResourceBuilder.newSectionResource().build();
        setupGetWithRestResultAsyncExpectations(expectedUrl, SectionResource.class, resource);

        Future<RestResult<SectionResource>> result = service.getNextSection(sectionId);

        assertEquals(resource, result.get().getSuccessObjectOrThrowException());
    }

    @Test
    public void test_getSectionByQuestionId() {
        long questionId = 1L;
        String expectedUrl = sectionRestUrl + "/getSectionByQuestionId/" + questionId;
        SectionResource sectionResource = SectionResourceBuilder.newSectionResource().build();
        setupGetWithRestResultExpectations(expectedUrl, SectionResource.class, sectionResource);

        RestResult<SectionResource> result = service.getSectionByQuestionId(questionId);

        assertEquals(sectionResource, result.getSuccessObjectOrThrowException());
    }

    @Test
    public void test_getQuestionForSectionAndSubsections() {
        long sectionId = 1L;
        String expectedUrl = sectionRestUrl + "/getQuestionsForSectionAndSubsections/" + sectionId;
        Set<Long> questions = new HashSet<>();
        setupGetWithRestResultExpectations(expectedUrl, longsSetType(), questions);

        RestResult<Set<Long>> result = service.getQuestionsForSectionAndSubsections(sectionId);

        assertEquals(questions, result.getSuccessObjectOrThrowException());
    }

    @Test
    public void testGetSectionsForCompetitionByType() {
        String expectedUrl = sectionRestUrl + "/getSectionsByCompetitionIdAndType/123/FINANCE";
        List<SectionResource> returnedResponse = Arrays.asList(new SectionResource());
        setupGetWithRestResultExpectations(expectedUrl, new ParameterizedTypeReference<List<SectionResource>>() {}, returnedResponse);

        List<SectionResource> response = service.getSectionsByCompetitionIdAndType(123L, SectionType.FINANCE).getSuccessObject();
        
        assertEquals(returnedResponse, response);
    }

    @Test
    public void test_getFinanceSectionForCompetition() {
        long competitionId = 1L;
        String expectedUrl = sectionRestUrl + "/getFinanceSectionByCompetitionId/" + competitionId;
        SectionResource resource = SectionResourceBuilder.newSectionResource().build();
        setupGetWithRestResultExpectations(expectedUrl, SectionResource.class, resource);

        RestResult<SectionResource> result = service.getFinanceSectionForCompetition(competitionId);

        assertEquals(resource, result.getSuccessObjectOrThrowException());
    }
    

}
