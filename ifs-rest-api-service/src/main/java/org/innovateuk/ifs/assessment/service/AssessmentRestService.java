package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link org.innovateuk.ifs.assessment.resource.AssessmentResource} related data.
 */
public interface AssessmentRestService {

    RestResult<AssessmentResource> getById(long id);

    RestResult<AssessmentResource> getAssignableById(long id);

    RestResult<AssessmentResource> getRejectableById(long id);

    RestResult<List<AssessmentResource>> getByUserAndCompetition(long userId, long competitionId);

    RestResult<Long> countByStateAndCompetition(AssessmentState state, long competitionId);

    RestResult<AssessmentTotalScoreResource> getTotalScore(long id);

    RestResult<Void> recommend(long id, AssessmentFundingDecisionOutcomeResource assessmentFundingDecision);

    RestResult<ApplicationAssessmentFeedbackResource> getApplicationFeedback(long applicationId);

    RestResult<Void> rejectInvitation(long id, AssessmentRejectOutcomeResource assessmentRejectOutcomeResource);

    RestResult<Void> acceptInvitation(long id);

    RestResult<Void> withdrawAssessment(long id);

    RestResult<Void> submitAssessments(AssessmentSubmissionsResource assessmentSubmissions);

    RestResult<AssessmentResource> createAssessment(AssessmentCreateResource assessmentCreateResource);
}
