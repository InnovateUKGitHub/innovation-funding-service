package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.service.ReviewRestServiceImpl;
import org.junit.Test;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.assessmentReviewResourceListType;
import static org.innovateuk.ifs.review.builder.ReviewRejectOutcomeResourceBuilder.newReviewRejectOutcomeResource;
import static org.innovateuk.ifs.review.builder.ReviewResourceBuilder.newReviewResource;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class ReviewRestServiceImplTest extends BaseRestServiceUnitTest<ReviewRestServiceImpl> {

    private static final String restUrl = "/assessmentpanel";

    @Override
    protected ReviewRestServiceImpl registerRestServiceUnderTest() {
        return new ReviewRestServiceImpl();
    }

    @Test
    public void assignToPanel() {
        long applicationId = 7L;

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "assign-application", applicationId), OK);

        service.assignToPanel(applicationId).getSuccess();
    }

    @Test
    public void unassignFromPanel() {
        long applicationId = 7L;

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "unassign-application", applicationId), OK);

        service.unassignFromPanel(applicationId).getSuccess();
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

        boolean response = service.isPendingReviewNotifications(competitionId).getSuccess();
        assertEquals(expected, response);
    }

    @Test
    public void getAssessmentReviews() {
        long competitionId = 11L;
        long userId = 2L;
        List<ReviewResource> assessmentReviews = newReviewResource().build(2);

        setupGetWithRestResultExpectations(format("%s/user/%s/competition/%s", restUrl, userId, competitionId), assessmentReviewResourceListType(), assessmentReviews, OK);

        List<ReviewResource> result = service.getAssessmentReviews(userId, competitionId).getSuccess();
        assertEquals(assessmentReviews, result);
    }

    @Test
    public void getAssessmentReview() {
        long assessmentReviewId = 11L;

        ReviewResource assessmentReview = newReviewResource().build();

        setupGetWithRestResultExpectations(format("%s/review/%d", restUrl, assessmentReviewId), ReviewResource.class, assessmentReview, OK);

        ReviewResource result = service.getAssessmentReview(assessmentReviewId).getSuccess();
        assertEquals(assessmentReview, result);
    }

    @Test
    public void acceptAssessmentReview() {
        long assessmentReviewId = 1L;

        setupPutWithRestResultExpectations(format("%s/review/%d/accept", restUrl, assessmentReviewId), null, OK);
        service.acceptAssessmentReview(assessmentReviewId).getSuccess();
    }

    @Test
    public void rejectAssessmentReview() {
        long assessmentReviewId = 1L;

        ReviewRejectOutcomeResource reviewRejectOutcomeResource = newReviewRejectOutcomeResource().build();
        setupPutWithRestResultExpectations(format("%s/review/%d/reject", restUrl, assessmentReviewId),
                reviewRejectOutcomeResource, OK);
        service.rejectAssessmentReview(assessmentReviewId, reviewRejectOutcomeResource).getSuccess();
    }
}