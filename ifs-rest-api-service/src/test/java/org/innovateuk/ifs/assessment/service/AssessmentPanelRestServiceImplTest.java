package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
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

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "assign-application", applicationId), OK);

        service.assignToPanel(applicationId).getSuccessObjectOrThrowException();
    }

    @Test
    public void unassignFromPanel() {
        long applicationId = 7L;

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "unassign-application", applicationId), OK);

        service.unassignFromPanel(applicationId).getSuccessObjectOrThrowException();
    }

    @Test
    public void notifyAssessors() {
        long competitionId = 11L;

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "notify-assessors", competitionId), OK);

        service.notifyAssessors(competitionId);
    }

    @Test
    public void isPendingReviewNotifications() {
        long competitionId = 11L;
        boolean expected = true;

        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "notify-assessors", competitionId), Boolean.class, expected, OK);

        boolean response = service.isPendingReviewNotifications(competitionId).getSuccessObjectOrThrowException();
        assertEquals(expected, response);
    }
}