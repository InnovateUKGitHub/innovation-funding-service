package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

/**
 * AssessmentFeedbackRestServiceImpl is a utility for CRUD operations on {@link org.innovateuk.ifs.assessment.resource.AssessmentResource}.
 * This class connects to the {org.innovateuk.ifs.assessment.controller.AssessmentController}
 * through a REST call.
 */
@Service
public class AssessmentRestServiceImpl extends BaseRestService implements AssessmentRestService {

    private String assessmentRestURL = "/assessment";

    protected void setAssessmentRestURL(final String assessmentRestURL) {
        this.assessmentRestURL = assessmentRestURL;
    }

    @Override
    public RestResult<AssessmentResource> getById(final long id) {
        return getWithRestResult(format("%s/%s", assessmentRestURL, id), AssessmentResource.class);
    }

    @Override
    public RestResult<AssessmentResource> getAssignableById(long id) {
        return getWithRestResult(format("%s/%s/assign", assessmentRestURL, id), AssessmentResource.class);
    }

    @Override
    public RestResult<AssessmentResource> getRejectableById(long id) {
        return getWithRestResult(format("%s/%s/rejectable", assessmentRestURL, id), AssessmentResource.class);
    }

    @Override
    public RestResult<List<AssessmentResource>> getByUserAndCompetition(long userId, long competitionId) {
        return getWithRestResult(format("%s/user/%s/competition/%s", assessmentRestURL, userId, competitionId), ParameterizedTypeReferences.assessmentResourceListType());
    }

    @Override
    public RestResult<Long> countByStateAndCompetition(AssessmentState state, long competitionId) {
        return getWithRestResult(format("%s/state/%s/competition/%s/count", assessmentRestURL, state.getStateName(), competitionId), Long.TYPE);
    }

    @Override
    public RestResult<AssessmentTotalScoreResource> getTotalScore(long id) {
        return getWithRestResult(format("%s/%s/score", assessmentRestURL, id), AssessmentTotalScoreResource.class);
    }

    @Override
    public RestResult<Void> recommend(long id, AssessmentFundingDecisionOutcomeResource assessmentFundingDecision) {
        return putWithRestResult(format("%s/%s/recommend", assessmentRestURL, id), assessmentFundingDecision, Void.class);
    }

    @Override
    public RestResult<ApplicationAssessmentFeedbackResource> getApplicationFeedback(long applicationId) {
        return getWithRestResult(format("%s/application/%s/feedback", assessmentRestURL, applicationId), ApplicationAssessmentFeedbackResource.class);
    }


    @Override
    public RestResult<Void> rejectInvitation(long id, AssessmentRejectOutcomeResource assessmentRejectOutcomeResource) {
        return putWithRestResult(format("%s/%s/rejectInvitation", assessmentRestURL, id), assessmentRejectOutcomeResource, Void.class);
    }

    @Override
    public RestResult<Void> withdrawAssessment(long id) {
        return putWithRestResult(format("%s/%s/withdraw", assessmentRestURL, id), Void.class);
    }

    @Override
    public RestResult<Void> acceptInvitation(long id) {
        return putWithRestResult(format("%s/%s/acceptInvitation", assessmentRestURL, id), Void.class);
    }

    @Override
    public RestResult<Void> submitAssessments(AssessmentSubmissionsResource assessmentSubmissions) {
        return putWithRestResult(format("%s/submitAssessments", assessmentRestURL), assessmentSubmissions, Void.class);
    }

    @Override
    public RestResult<AssessmentResource> createAssessment(AssessmentCreateResource assessmentCreateResource) {
        return postWithRestResult(format("%s", assessmentRestURL), assessmentCreateResource, AssessmentResource.class);
    }
}
