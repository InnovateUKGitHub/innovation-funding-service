package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 *  * REST service for managing applications on an assessment panel.
 */
public interface AssessmentPanelRestService {
    RestResult<Void> assignToPanel(long applicationId);
    RestResult<Void> unassignFromPanel(long applicationId);
    RestResult<Void> notifyAssessors(long competitionId);
    RestResult<Boolean> isPendingReviewNotifications(long competitionId);
    RestResult<List<AssessmentReviewResource>> getAssessmentReviews(long userId, long competitionId);
    RestResult<Void> acceptAssessmentReview(long assessmentReviewId);
    RestResult<Void> rejectAssessmentReview(long assessmentReviewId, AssessmentReviewRejectOutcomeResource assessmentReviewRejectOutcomeResource);
}
