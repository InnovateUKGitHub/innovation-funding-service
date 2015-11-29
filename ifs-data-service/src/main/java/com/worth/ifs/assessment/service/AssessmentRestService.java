package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.domain.Assessment;

import java.util.List;
import java.util.Set;

/**
 * Interface for CRUD operations on {@link Assessment} related data.
 */
public interface AssessmentRestService {

    public List<Assessment> getAllByAssessorAndCompetition(Long userId, Long competitionId);

    public Assessment getOneByAssessorAndApplication(Long userId, Long applicationId);

    public Integer getTotalAssignedByAssessorAndCompetition(Long userId, Long competitionId);

    public Integer getTotalSubmittedByAssessorAndCompetition(Long userId, Long competitionId);

    public Boolean respondToAssessmentInvitation(Long assessorId, Long applicationId, Boolean decision, String decisionReason, String observations);

    public Boolean submitAssessments(Long assessorId, Set<Long> assessmentIds);

    public Boolean saveAssessmentSummary(Long assessorId, Long applicationId, String suitableValue, String suitableFeedback, String comments, Double overallScore);

    public void acceptAssessmentInvitation(Long applicationId, Long assessorId, Assessment assessment);

    public void rejectAssessmentInvitation(Long applicationId, Long assessorId, Assessment assessment);

}
