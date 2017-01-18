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

    RestResult<List<AssessmentResource>> getByUserAndCompetition(long userId, long CompetitionId);

    RestResult<AssessmentTotalScoreResource> getTotalScore(long id);

    RestResult<Void> recommend(long id, AssessmentFundingDecisionResource assessmentFundingDecision);

    RestResult<Void> rejectInvitation(long id, ApplicationRejectionResource applicationRejection);

    RestResult<Void> acceptInvitation(long id);

    RestResult<Void> notify(long id);

    RestResult<Void> withdrawAssessment(long id);

    RestResult<Void> submitAssessments(AssessmentSubmissionsResource assessmentSubmissions);
}
