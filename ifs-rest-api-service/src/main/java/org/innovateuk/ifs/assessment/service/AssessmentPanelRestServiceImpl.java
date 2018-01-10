package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.assessmentReviewResourceListType;

@Service
public class AssessmentPanelRestServiceImpl extends BaseRestService implements AssessmentPanelRestService {

    private static final String assessmentPanelRestUrl = "/assessmentpanel";

    @Override
    public RestResult<Void> assignToPanel(long applicationId) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelRestUrl, "assignApplication", applicationId), Void.class);
    }

    @Override
    public RestResult<Void> unassignFromPanel(long applicationId) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelRestUrl, "unassignApplication", applicationId), Void.class);
    }

    @Override
    public RestResult<Void> notifyAssessors(long competitionId) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelRestUrl, "notify-assessors", competitionId), Void.class);
    }

    @Override
    public RestResult<Boolean> isPendingReviewNotifications(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", assessmentPanelRestUrl, "notify-assessors", competitionId), Boolean.class);
    }

    @Override
    public RestResult<List<AssessmentReviewResource>> getAssessmentReviews(long userId, long competitionId) {
        return getWithRestResult(format("%s/user/%s/competition/%s", assessmentPanelRestUrl, userId, competitionId), assessmentReviewResourceListType());
    }

    @Override
    public RestResult<AssessmentReviewResource> getAssessmentReview(long assessmentReviewId) {
        return getWithRestResult(format("%s/review/%d", assessmentPanelRestUrl, assessmentReviewId), AssessmentReviewResource.class);
    }

    @Override
    public RestResult<Void> acceptAssessmentReview(long assessmentReviewId) {
        return putWithRestResult(format("%s/review/%d/accept", assessmentPanelRestUrl, assessmentReviewId), Void.class);
    }

    @Override
    public RestResult<Void> rejectAssessmentReview(long assessmentReviewId, AssessmentReviewRejectOutcomeResource assessmentReviewRejectOutcomeResource) {
        return putWithRestResult(format("%s/review/%d/reject", assessmentPanelRestUrl, assessmentReviewId), assessmentReviewRejectOutcomeResource, Void.class);
    }
}