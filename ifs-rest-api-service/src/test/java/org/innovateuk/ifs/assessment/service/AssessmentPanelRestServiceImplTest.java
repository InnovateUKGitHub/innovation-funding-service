package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.junit.Test;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewResourceBuilder.newAssessmentReviewResource;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.assessmentReviewResourceListType;
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

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "assignApplication", applicationId), OK);

        service.assignToPanel(applicationId).getSuccessObjectOrThrowException();
    }

    @Test
    public void unassignFromPanel() {
        long applicationId = 7L;

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "unassignApplication", applicationId), OK);

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

    @Test
    public void getAssessmentReviews() {
        long competitionId = 11L;
        long userId = 2L;
        List<AssessmentReviewResource> assessmentReviews = newAssessmentReviewResource().build(2);

        setupGetWithRestResultExpectations(format("%s/user/%s/competition/%s", restUrl, userId, competitionId), assessmentReviewResourceListType(), assessmentReviews, OK);

        List<AssessmentReviewResource> result = service.getAssessmentReviews(userId, competitionId).getSuccessObjectOrThrowException();
        assertEquals(assessmentReviews, result);
    }
}