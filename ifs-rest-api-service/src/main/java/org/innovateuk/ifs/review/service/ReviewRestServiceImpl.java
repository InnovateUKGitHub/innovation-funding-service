package org.innovateuk.ifs.review.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.assessmentReviewResourceListType;

@Service
public class ReviewRestServiceImpl extends BaseRestService implements ReviewRestService {

    private static final String ASSESSMENT_PANEL_REST_URL = "/assessmentpanel";

    @Override
    public RestResult<Void> assignToPanel(long applicationId) {
        return postWithRestResult(format("%s/%s/%s", ASSESSMENT_PANEL_REST_URL, "assign-application", applicationId), Void.class);
    }

    @Override
    public RestResult<Void> unassignFromPanel(long applicationId) {
        return postWithRestResult(format("%s/%s/%s", ASSESSMENT_PANEL_REST_URL, "unassign-application", applicationId), Void.class);
    }

    @Override
    public RestResult<Void> notifyAssessors(long competitionId) {
        return postWithRestResult(format("%s/%s/%s", ASSESSMENT_PANEL_REST_URL, "notify-assessors", competitionId), Void.class);
    }

    @Override
    public RestResult<Boolean> isPendingReviewNotifications(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", ASSESSMENT_PANEL_REST_URL, "notify-assessors", competitionId), Boolean.class);
    }

    @Override
    public RestResult<List<ReviewResource>> getAssessmentReviews(long userId, long competitionId) {
        return getWithRestResult(format("%s/user/%s/competition/%s", ASSESSMENT_PANEL_REST_URL, userId, competitionId), assessmentReviewResourceListType());
    }

    @Override
    public RestResult<ReviewResource> getAssessmentReview(long assessmentReviewId) {
        return getWithRestResult(format("%s/review/%d", ASSESSMENT_PANEL_REST_URL, assessmentReviewId), ReviewResource.class);
    }

    @Override
    public RestResult<Void> acceptAssessmentReview(long assessmentReviewId) {
        return putWithRestResult(format("%s/review/%d/accept", ASSESSMENT_PANEL_REST_URL, assessmentReviewId), Void.class);
    }

    @Override
    public RestResult<Void> rejectAssessmentReview(long assessmentReviewId, ReviewRejectOutcomeResource reviewRejectOutcomeResource) {
        return putWithRestResult(format("%s/review/%d/reject", ASSESSMENT_PANEL_REST_URL, assessmentReviewId), reviewRejectOutcomeResource, Void.class);
    }
}