package org.innovateuk.ifs.review.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.innovateuk.ifs.review.resource.ReviewResource;

import java.util.List;

/**
 *  * REST service for managing applications on an assessment panel.
 */
public interface ReviewRestService {
    RestResult<Void> assignToPanel(long applicationId);
    RestResult<Void> unassignFromPanel(long applicationId);
    RestResult<Void> notifyAssessors(long competitionId);
    RestResult<Boolean> isPendingReviewNotifications(long competitionId);
    RestResult<List<ReviewResource>> getAssessmentReviews(long userId, long competitionId);
    RestResult<Void> acceptAssessmentReview(long assessmentReviewId);
    RestResult<Void> rejectAssessmentReview(long assessmentReviewId, ReviewRejectOutcomeResource reviewRejectOutcomeResource);
    RestResult<ReviewResource> getAssessmentReview(long assessmentReviewId);
}
