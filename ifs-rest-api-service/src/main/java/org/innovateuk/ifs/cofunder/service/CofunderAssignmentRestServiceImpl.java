package org.innovateuk.ifs.cofunder.service;

import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDecisionResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

/**
 * AssessmentFeedbackRestServiceImpl is a utility for CRUD operations on {@link AssessmentResource}.
 * This class connects to the {org.innovateuk.ifs.assessment.controller.AssessmentController}
 * through a REST call.
 */
@Service
public class CofunderAssignmentRestServiceImpl extends BaseRestService implements CofunderAssignmentRestService {

    private String assessmentRestURL = "/cofunder";

    @Override
    public RestResult<CofunderAssignmentResource> getAssignment(long userId, long applicationId) {
        return null;
    }

    @Override
    public RestResult<CofunderAssignmentResource> assign(long userId, long applicationId) {
        return null;
    }

    @Override
    public RestResult<Void> removeAssignment(long userId, long applicationId) {
        return null;
    }

    @Override
    public RestResult<Void> decision(long assignmentId, CofunderDecisionResource decision) {
        return null;
    }

    @Override
    public RestResult<Void> edit(long assignmentId) {
        return null;
    }

    @Override
    public RestResult<ApplicationsForCofundingPageResource> findApplicationsNeedingCofunders(long competitionId, int page) {
        return null;
    }

    @Override
    public RestResult<CofundersAvailableForApplicationPageResource> findAvailableCofudersForApplication(long applicationId, int page) {
        return null;
    }

//    @Override
//    public RestResult<AssessmentResource> getById(final long id) {
//        return getWithRestResult(format("%s/%s", assessmentRestURL, id), AssessmentResource.class);
//    }
//
//    @Override
//    public RestResult<AssessmentResource> getAssignableById(long id) {
//        return getWithRestResult(format("%s/%s/assign", assessmentRestURL, id), AssessmentResource.class);
//    }
//
//    @Override
//    public RestResult<AssessmentResource> getRejectableById(long id) {
//        return getWithRestResult(format("%s/%s/rejectable", assessmentRestURL, id), AssessmentResource.class);
//    }
//
//    @Override
//    public RestResult<List<AssessmentResource>> getByUserAndCompetition(long userId, long competitionId) {
//        return getWithRestResult(format("%s/user/%s/competition/%s", assessmentRestURL, userId, competitionId), ParameterizedTypeReferences.assessmentResourceListType());
//    }
//
//    public RestResult<List<AssessmentResource>> getByUserAndApplication(long userId, long applicationId) {
//        return getWithRestResult(format("%s/user/%s/application/%s", assessmentRestURL, userId, applicationId), ParameterizedTypeReferences.assessmentResourceListType());
//    }
//
//    @Override
//    public RestResult<Long> countByStateAndCompetition(AssessmentState state, long competitionId) {
//        return getWithRestResult(format("%s/state/%s/competition/%s/count", assessmentRestURL, state.getStateName(), competitionId), Long.TYPE);
//    }
//
//    @Override
//    public RestResult<AssessmentTotalScoreResource> getTotalScore(long id) {
//        return getWithRestResult(format("%s/%s/score", assessmentRestURL, id), AssessmentTotalScoreResource.class);
//    }
//
//    @Override
//    public RestResult<Void> recommend(long id, AssessmentFundingDecisionOutcomeResource assessmentFundingDecision) {
//        return putWithRestResult(format("%s/%s/recommend", assessmentRestURL, id), assessmentFundingDecision, Void.class);
//    }
//
//    @Override
//    public RestResult<ApplicationAssessmentFeedbackResource> getApplicationFeedback(long applicationId) {
//        return getWithRestResult(format("%s/application/%s/feedback", assessmentRestURL, applicationId), ApplicationAssessmentFeedbackResource.class);
//    }
//
//    @Override
//    public RestResult<Void> rejectInvitation(long id, AssessmentRejectOutcomeResource assessmentRejectOutcomeResource) {
//        return putWithRestResult(format("%s/%s/reject-invitation", assessmentRestURL, id), assessmentRejectOutcomeResource, Void.class);
//    }
//
//    @Override
//    public RestResult<Void> withdrawAssessment(long id) {
//        return putWithRestResult(format("%s/%s/withdraw", assessmentRestURL, id), Void.class);
//    }
//
//    @Override
//    public RestResult<Void> acceptInvitation(long id) {
//        return putWithRestResult(format("%s/%s/accept-invitation", assessmentRestURL, id), Void.class);
//    }
//
//    @Override
//    public RestResult<Void> submitAssessments(AssessmentSubmissionsResource assessmentSubmissions) {
//        return putWithRestResult(format("%s/submit-assessments", assessmentRestURL), assessmentSubmissions, Void.class);
//    }
//
//    @Override
//    public RestResult<AssessmentResource> createAssessment(AssessmentCreateResource assessmentCreateResource) {
//        return postWithRestResult(format("%s", assessmentRestURL), assessmentCreateResource, AssessmentResource.class);
//    }
//
//    @Override
//    public RestResult<List<AssessmentResource>> createAssessments(List<AssessmentCreateResource> assessmentCreateResource) {
//        return postWithRestResult(format("%s/bulk", assessmentRestURL), assessmentCreateResource, new ParameterizedTypeReference<List<AssessmentResource>>() {});
//    }
}
