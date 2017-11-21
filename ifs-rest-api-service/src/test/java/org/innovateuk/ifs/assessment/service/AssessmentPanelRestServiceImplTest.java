package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.junit.Test;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.OK;

public class AssessmentPanelRestServiceImplTest extends BaseRestServiceUnitTest<AssessmentPanelRestServiceImpl> {

    private static final String restUrl = "/assessmentpanel";

    @Override
    protected AssessmentPanelRestServiceImpl registerRestServiceUnderTest() {
        return new AssessmentPanelRestServiceImpl();
    }

    @Test
    public void assignToPanel() {
        long applicationId = 7L;

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "assignApplication", applicationId), OK);

        service.assignToPanel(applicationId).getSuccessObjectOrThrowException();
    }

    @Test
    public void unassignFromPanel() {
        long applicationId = 7L;

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "unassignApplication", applicationId), OK);

        service.unassignFromPanel(applicationId).getSuccessObjectOrThrowException();
    }
}