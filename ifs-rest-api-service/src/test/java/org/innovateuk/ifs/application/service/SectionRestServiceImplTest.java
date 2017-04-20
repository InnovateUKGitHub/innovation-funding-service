package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.concurrent.Future;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;
import static org.junit.Assert.*;

/**
 * Tests to check the ApplicationRestService's interaction with the RestTemplate and the processing of its results
 */
public class SectionRestServiceImplTest extends BaseRestServiceUnitTest<SectionRestServiceImpl> {

    private static final String sectionRestUrl = "/section";

    @Override
    protected SectionRestServiceImpl registerRestServiceUnderTest() {
        SectionRestServiceImpl sectionRestService = new SectionRestServiceImpl();
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

        assertEquals(messages, result.getSuccessObjectOrThrowException());
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
    public void getById() {
        long sectionId = 123L;
        String expectedUrl = sectionRestUrl + "/" + sectionId;
        SectionResource sectionResource = newSectionResource().build();
        setupGetWithRestResultExpectations(expectedUrl, SectionResource.class, sectionResource);

        RestResult<SectionResource> result = service.getById(sectionId);

        assertEquals(sectionResource, result.getSuccessObjectOrThrowException());
    }

    @Test
    public void getByCompetition() {
        long competitionId = 123L;
        String expectedUrl = sectionRestUrl + "/getByCompetition/" + competitionId;
        List<SectionResource> sectionResources = newSectionResource().build(2);
        setupGetWithRestResultExpectations(expectedUrl, sectionResourceListType(), sectionResources);

        RestResult<List<SectionResource>> result = service.getByCompetition(competitionId);

        assertEquals(sectionResources, result.getSuccessObjectOrThrowException());
    }

    @Test
    public void getCompletedSectionsByOrganisation() {
        long applicationId = 123L;
        String expectedUrl = sectionRestUrl + "/getCompletedSectionsByOrganisation/" + applicationId;
        Map<Long, Set<Long>> expectedResult = new HashMap<>();
        setupGetWithRestResultExpectations(expectedUrl, mapOfLongToLongsSetType(), expectedResult);

        RestResult<Map<Long, Set<Long>>> result = service.getCompletedSectionsByOrganisation(applicationId);

        assertEquals(expectedResult, result.getSuccessObjectOrThrowException());
    }

    @Test
    public void getCompletedSectionIds() {
        String expectedUrl = sectionRestUrl + "/getCompletedSections/123/456";
        List<Long> returnedResponse = asList(1L, 2L, 3L);

        setupGetWithRestResultExpectations(expectedUrl, longsListType(), returnedResponse);

        // now run the method under test
        List<Long> response = service.getCompletedSectionIds(123L, 456L).getSuccessObject();
        assertEquals(returnedResponse, response);
    }

    @Test
    public void getIncompleteSectionIds() {

        String expectedUrl = sectionRestUrl + "/getIncompleteSections/123";
        List<Long> returnedResponse = asList(1L, 2L, 3L);

        setupGetWithRestResultExpectations(expectedUrl, longsListType(), returnedResponse);

        // now run the method under test
        List<Long> response = service.getIncompletedSectionIds(123L).getSuccessObject();
        assertEquals(returnedResponse, response);
    }

    @Test
    public void getPreviousSection() throws Exception {
        long sectionId = 1L;
        String expectedUrl = sectionRestUrl + "/getPreviousSection/" + sectionId;
        SectionResource resource = newSectionResource().build();
        setupGetWithRestResultAsyncExpectations(expectedUrl, SectionResource.class, resource);

        Future<RestResult<SectionResource>> result = service.getPreviousSection(sectionId);

        assertEquals(resource, result.get().getSuccessObjectOrThrowException());
    }

    @Test
    public void getNextSection() throws Exception {
        long sectionId = 1L;
        String expectedUrl = sectionRestUrl + "/getNextSection/" + sectionId;
        SectionResource resource = newSectionResource().build();
        setupGetWithRestResultAsyncExpectations(expectedUrl, SectionResource.class, resource);

        Future<RestResult<SectionResource>> result = service.getNextSection(sectionId);

        assertEquals(resource, result.get().getSuccessObjectOrThrowException());
    }

    @Test
    public void getSectionByQuestionId() {
        long questionId = 1L;
        String expectedUrl = sectionRestUrl + "/getSectionByQuestionId/" + questionId;
        SectionResource sectionResource = newSectionResource().build();
        setupGetWithRestResultExpectations(expectedUrl, SectionResource.class, sectionResource);

        RestResult<SectionResource> result = service.getSectionByQuestionId(questionId);

        assertEquals(sectionResource, result.getSuccessObjectOrThrowException());
    }

    @Test
    public void getQuestionForSectionAndSubsections() {
        long sectionId = 1L;
        String expectedUrl = sectionRestUrl + "/getQuestionsForSectionAndSubsections/" + sectionId;
        Set<Long> questions = new HashSet<>();
        setupGetWithRestResultExpectations(expectedUrl, longsSetType(), questions);

        RestResult<Set<Long>> result = service.getQuestionsForSectionAndSubsections(sectionId);

        assertEquals(questions, result.getSuccessObjectOrThrowException());
    }

    @Test
    public void getSectionsForCompetitionByType() {
        String expectedUrl = sectionRestUrl + "/getSectionsByCompetitionIdAndType/123/FINANCE";
        List<SectionResource> returnedResponse = Arrays.asList(new SectionResource());
        setupGetWithRestResultExpectations(expectedUrl, sectionResourceListType(), returnedResponse);

        List<SectionResource> response = service.getSectionsByCompetitionIdAndType(123L, SectionType.FINANCE).getSuccessObject();

        assertEquals(returnedResponse, response);
    }

    @Test
    public void financeSectionForCompetition() {
        long competitionId = 1L;
        String expectedUrl = sectionRestUrl + "/getFinanceSectionByCompetitionId/" + competitionId;
        SectionResource resource = newSectionResource().build();
        setupGetWithRestResultExpectations(expectedUrl, SectionResource.class, resource);

        RestResult<SectionResource> result = service.getFinanceSectionForCompetition(competitionId);

        assertEquals(resource, result.getSuccessObjectOrThrowException());
    }

    @Test
    public void getByCompetitionIdVisibleForAssessment() throws Exception {
        List<SectionResource> expected = newSectionResource().build(2);

        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/getByCompetitionIdVisibleForAssessment/%s", sectionRestUrl, competitionId), sectionResourceListType(), expected);
        assertSame(expected, service.getByCompetitionIdVisibleForAssessment(competitionId).getSuccessObject());
    }
}
