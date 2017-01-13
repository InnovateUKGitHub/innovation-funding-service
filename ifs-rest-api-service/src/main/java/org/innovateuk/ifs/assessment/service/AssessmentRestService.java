package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

/**
 * Interface for CRUD operations on {@link org.innovateuk.ifs.assessment.resource.AssessmentResource} related data.
 */
public interface AssessmentRestService {

    RestResult<AssessmentResource> getById(Long id);

    RestResult<AssessmentResource> getAssignableById(Long id);

    RestResult<List<AssessmentResource>> getByUserAndCompetition(Long userId, Long CompetitionId);

    RestResult<AssessmentTotalScoreResource> getTotalScore(Long id);

    RestResult<Void> recommend(Long id, AssessmentFundingDecisionResource assessmentFundingDecision);

    RestResult<Void> rejectInvitation(Long id, ApplicationRejectionResource applicationRejection);

    RestResult<Void> acceptInvitation(Long id);

    RestResult<Void> submitAssessments(AssessmentSubmissionsResource assessmentSubmissions);
}
