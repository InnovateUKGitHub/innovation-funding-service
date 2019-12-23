package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.junit.Test;

import java.util.*;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsSetType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.sectionResourceListType;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

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
    public void getById() {
        long sectionId = 123L;
        String expectedUrl = sectionRestUrl + "/" + sectionId;
        SectionResource sectionResource = newSectionResource().build();
        setupGetWithRestResultExpectations(expectedUrl, SectionResource.class, sectionResource);

        RestResult<SectionResource> result = service.getById(sectionId);

        assertEquals(sectionResource, result.getSuccess());
    }

    @Test
    public void getByCompetition() {
        long competitionId = 123L;
        String expectedUrl = sectionRestUrl + "/get-by-competition/" + competitionId;
        List<SectionResource> sectionResources = newSectionResource().build(2);
        setupGetWithRestResultExpectations(expectedUrl, sectionResourceListType(), sectionResources);

        RestResult<List<SectionResource>> result = service.getByCompetition(competitionId);

        assertEquals(sectionResources, result.getSuccess());
    }

    @Test
    public void getSectionByQuestionId() {
        long questionId = 1L;
        String expectedUrl = sectionRestUrl + "/get-section-by-question-id/" + questionId;
        SectionResource sectionResource = newSectionResource().build();
        setupGetWithRestResultExpectations(expectedUrl, SectionResource.class, sectionResource);

        RestResult<SectionResource> result = service.getSectionByQuestionId(questionId);

        assertEquals(sectionResource, result.getSuccess());
    }

    @Test
    public void getQuestionForSectionAndSubsections() {
        long sectionId = 1L;
        String expectedUrl = sectionRestUrl + "/get-questions-for-section-and-subsections/" + sectionId;
        Set<Long> questions = new HashSet<>();
        setupGetWithRestResultExpectations(expectedUrl, longsSetType(), questions);

        RestResult<Set<Long>> result = service.getQuestionsForSectionAndSubsections(sectionId);

        assertEquals(questions, result.getSuccess());
    }

    @Test
    public void getSectionsForCompetitionByType() {
        String expectedUrl = sectionRestUrl + "/get-sections-by-competition-id-and-type/123/FINANCE";
        List<SectionResource> returnedResponse = singletonList(new SectionResource());
        setupGetWithRestResultExpectations(expectedUrl, sectionResourceListType(), returnedResponse);

        List<SectionResource> response = service.getSectionsByCompetitionIdAndType(123L, SectionType.FINANCE).getSuccess();

        assertEquals(returnedResponse, response);
    }

    @Test
    public void getByCompetitionIdVisibleForAssessment() throws Exception {
        List<SectionResource> expected = newSectionResource().build(2);

        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/get-by-competition-id-visible-for-assessment/%s", sectionRestUrl, competitionId), sectionResourceListType(), expected);
        assertSame(expected, service.getByCompetitionIdVisibleForAssessment(competitionId).getSuccess());
    }

    @Test
    public void getChildSectionsByParentId() {
        long parentId = 12L;
        String expectedUrl = sectionRestUrl + "/get-child-sections/" + parentId;
        SectionResource parentSectionResource = newSectionResource().withId(parentId).build();
        List<SectionResource> childSectionResources = newSectionResource().withParentSection(parentSectionResource.getId()).build(4);
        setupGetWithRestResultExpectations(expectedUrl, sectionResourceListType(), childSectionResources);

        RestResult<List<SectionResource>> result = service.getChildSectionsByParentId(parentId);

        assertEquals(childSectionResources, result.getSuccess());
    }
}
