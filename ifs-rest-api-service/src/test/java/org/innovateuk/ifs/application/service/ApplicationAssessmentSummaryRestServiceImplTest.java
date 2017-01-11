package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.junit.Test;

import static java.lang.String.format;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
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
    public void getApplicationAssessmentSummary() throws Exception {
        ApplicationAssessmentSummaryResource expected = newApplicationAssessmentSummaryResource().build();

        Long applicationId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s", applicationAssessmentSummaryRestUrl, applicationId), ApplicationAssessmentSummaryResource.class, expected);
        assertSame(expected, service.getApplicationAssessmentSummary(applicationId).getSuccessObject());
    }
}