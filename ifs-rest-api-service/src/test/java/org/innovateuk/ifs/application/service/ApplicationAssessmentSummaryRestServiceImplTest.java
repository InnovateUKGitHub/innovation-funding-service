package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.junit.Test;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationAssessorResourceBuilder.newApplicationAssessorResource;
import static org.junit.Assert.assertSame;

public class ApplicationAssessmentSummaryRestServiceImplTest extends BaseRestServiceUnitTest<ApplicationAssessmentSummaryRestServiceImpl> {

    private static String applicationAssessmentSummaryRestUrl = "/applicationAssessmentSummary";

    @Override
    protected ApplicationAssessmentSummaryRestServiceImpl registerRestServiceUnderTest() {
        ApplicationAssessmentSummaryRestServiceImpl applicationAssessmentSummaryRestService = new ApplicationAssessmentSummaryRestServiceImpl();
        applicationAssessmentSummaryRestService.setDataRestServiceUrl(applicationAssessmentSummaryRestUrl);
        return applicationAssessmentSummaryRestService;
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        ApplicationAssessorPageResource expected = new ApplicationAssessorPageResource();

        Long applicationId = 1L;
        int page = 2;
        int size = 3;
        long filterInnovationArea = 4L;

        setupGetWithRestResultExpectations(format("%s/%s/availableAssessors?page=%s&size=%s&filterInnovationArea=%s",
                applicationAssessmentSummaryRestUrl, applicationId, page,
                size, filterInnovationArea), ApplicationAssessorPageResource.class, expected);

        assertSame(expected, service.getAvailableAssessors(applicationId, page, size, filterInnovationArea).getSuccessObject());
    }

    @Test
    public void getAssignedAssessors() throws Exception {
        List<ApplicationAssessorResource> expected = newApplicationAssessorResource().build(2);

        Long applicationId = 1L;

        setupGetWithRestResultExpectations(
                format("%s/%s/assignedAssessors", applicationAssessmentSummaryRestUrl, applicationId),
                ParameterizedTypeReferences.applicationAssessorResourceListType(), expected);

        assertSame(expected, service.getAssignedAssessors(applicationId).getSuccessObject());
    }

    @Test
    public void getApplicationAssessmentSummary() throws Exception {
        ApplicationAssessmentSummaryResource expected = newApplicationAssessmentSummaryResource().build();

        Long applicationId = 1L;

        setupGetWithRestResultExpectations(
                format("%s/%s", applicationAssessmentSummaryRestUrl, applicationId),
                ApplicationAssessmentSummaryResource.class, expected);

        assertSame(expected, service.getApplicationAssessmentSummary(applicationId).getSuccessObject());
    }
}