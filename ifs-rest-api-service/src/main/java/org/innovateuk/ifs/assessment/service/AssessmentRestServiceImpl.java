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
    public RestResult<AssessmentResource> getById(final Long id) {
        return getWithRestResult(format("%s/%s", assessmentRestURL, id), AssessmentResource.class);
    }

    @Override
    public RestResult<List<AssessmentResource>> getByUserAndCompetition(Long userId, Long competitionId) {
        return getWithRestResult(format("%s/user/%s/competition/%s", assessmentRestURL, userId, competitionId), ParameterizedTypeReferences.assessmentResourceListType());
    }

    @Override
    public RestResult<AssessmentTotalScoreResource> getTotalScore(Long id) {
        return getWithRestResult(format("%s/%s/score", assessmentRestURL, id), AssessmentTotalScoreResource.class);
    }

    @Override
    public RestResult<Void> recommend(Long id, AssessmentFundingDecisionResource assessmentFundingDecision) {
        return putWithRestResult(format("%s/%s/recommend", assessmentRestURL, id), assessmentFundingDecision, Void.class);
    }

    @Override
    public RestResult<Void> rejectInvitation(Long id, ApplicationRejectionResource applicationRejection) {
        return putWithRestResult(format("%s/%s/rejectInvitation", assessmentRestURL, id), applicationRejection, Void.class);
    }

    @Override
    public RestResult<Void> notify(Long id) {
        return putWithRestResult(format("%s/%s/notify", assessmentRestURL, id), Void.class);
    }

    @Override
    public RestResult<Void> withdrawAssessment(Long id) {
        return putWithRestResult(format("%s/%s/withdraw", assessmentRestURL, id), Void.class);
    }

    @Override
    public RestResult<Void> acceptInvitation(Long id) {
        return putWithRestResult(format("%s/%s/acceptInvitation", assessmentRestURL, id), Void.class);
    }

    @Override
    public RestResult<Void> submitAssessments(AssessmentSubmissionsResource assessmentSubmissions) {
        return putWithRestResult(format("%s/submitAssessments", assessmentRestURL), assessmentSubmissions, Void.class);
    }
}
