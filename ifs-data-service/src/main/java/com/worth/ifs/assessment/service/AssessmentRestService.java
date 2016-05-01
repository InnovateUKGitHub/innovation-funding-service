package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.Score;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.workflow.domain.ProcessOutcome;

import java.util.List;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link Assessment} related data.
 */
public interface AssessmentRestService {

    RestResult<List<Assessment>> getAllByAssessorAndCompetition(Long userId, Long competitionId);

    RestResult<Assessment> getOneByProcessRole(Long processRoleId);

    RestResult<Integer> getTotalAssignedByAssessorAndCompetition(Long userId, Long competitionId);

    RestResult<Integer> getTotalSubmittedByAssessorAndCompetition(Long userId, Long competitionId);

    RestResult<Void> respondToAssessmentInvitation(Long assessorId, Long applicationId, Boolean decision, String decisionReason, String observations);

    RestResult<Void> submitAssessments(Long assessorId, Set<Long> assessmentIds);

    RestResult<Void> saveAssessmentSummary(Long assessorId, Long applicationId, String suitableValue, String suitableFeedback, String comments);

    RestResult<Void> acceptAssessmentInvitation(Long processRoleId, Assessment assessment);

    RestResult<Void> rejectAssessmentInvitation(Long processRoleId, ProcessOutcome processOutcome);

    RestResult<Score> getScore(Long id);

}
