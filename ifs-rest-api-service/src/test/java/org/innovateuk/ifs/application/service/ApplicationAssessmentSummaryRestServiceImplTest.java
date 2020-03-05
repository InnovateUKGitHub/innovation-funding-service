package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAvailableAssessorResource.Sort;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.junit.Test;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationAssessorResourceBuilder.newApplicationAssessorResource;
import static org.junit.Assert.assertSame;

public class ApplicationAssessmentSummaryRestServiceImplTest extends BaseRestServiceUnitTest<ApplicationAssessmentSummaryRestServiceImpl> {

    private static String applicationAssessmentSummaryRestUrl = "/application-assessment-summary";

    @Override
    protected ApplicationAssessmentSummaryRestServiceImpl registerRestServiceUnderTest() {
        ApplicationAssessmentSummaryRestServiceImpl applicationAssessmentSummaryRestService = new ApplicationAssessmentSummaryRestServiceImpl();
        applicationAssessmentSummaryRestService.setServiceUrl(applicationAssessmentSummaryRestUrl);
        return applicationAssessmentSummaryRestService;
    }

    @Test
    public void getAvailableAssessors() {
        ApplicationAvailableAssessorPageResource expected = new ApplicationAvailableAssessorPageResource();

        Long applicationId = 1L;
        int page = 2;
        int size = 3;
        String assessorNameFilter = "Name";
        Sort sort = Sort.ASSESSOR;

        setupGetWithRestResultExpectations(format("%s/%s/available-assessors?page=%s&size=%s&assessorNameFilter=%s&sort=%s",
                applicationAssessmentSummaryRestUrl, applicationId, page,
                size, assessorNameFilter, sort), ApplicationAvailableAssessorPageResource.class, expected);

        assertSame(expected, service.getAvailableAssessors(applicationId, page, size, assessorNameFilter, sort).getSuccess());
    }

    @Test
    public void getAssignedAssessors() {
        List<ApplicationAssessorResource> expected = newApplicationAssessorResource().build(2);

        Long applicationId = 1L;

        setupGetWithRestResultExpectations(
                format("%s/%s/assigned-assessors", applicationAssessmentSummaryRestUrl, applicationId),
                ParameterizedTypeReferences.applicationAssessorResourceListType(), expected);

        assertSame(expected, service.getAssignedAssessors(applicationId).getSuccess());
    }

    @Test
    public void getApplicationAssessmentSummary() {
        ApplicationAssessmentSummaryResource expected = newApplicationAssessmentSummaryResource().build();

        Long applicationId = 1L;

        setupGetWithRestResultExpectations(
                format("%s/%s", applicationAssessmentSummaryRestUrl, applicationId),
                ApplicationAssessmentSummaryResource.class, expected);

        assertSame(expected, service.getApplicationAssessmentSummary(applicationId).getSuccess());
    }
}