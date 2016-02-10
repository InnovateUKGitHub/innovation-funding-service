package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.dto.Score;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service to handle crosscutting business processes related to Assessors and their role within the system.
 */
public interface AssessorService {

    /**
     * Update the Assessor's feedback to a given Response, creating a new AssessorFeedback if one does not yet
     * exist for this Assessor
     */
    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.assessment.dto.Feedback', 'UPDATE')")
    ServiceResult<Feedback> updateAssessorFeedback(@P("id") Feedback.Id feedbackId, Optional<String> feedbackValue, Optional<String> feedbackText);

    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.assessment.dto.Feedback', 'READ')")
    ServiceResult<Feedback> getFeedback(@P("id") Feedback.Id id);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Void> save(Assessment a);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Assessment> saveAndGet(Assessment a);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Assessment> getOne(Long id);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<List<Assessment>> getAllByCompetitionAndAssessor(Long competitionId, Long assessorId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Assessment> getOneByProcessRole(Long processRoleId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Integer> getTotalSubmittedAssessmentsByCompetition(Long competitionId, Long assessorId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Integer> getTotalAssignedAssessmentsByCompetition(Long competitionId, Long assessorId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Score> getScore(Long id);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Void> submitAssessment(Long assessorId, Long applicationId, String suitableValue, String suitableFeedback, String comments);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Void> acceptAssessmentInvitation(Long processRoleId, Assessment assessment);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Void> rejectAssessmentInvitation(Long processRoleId, ProcessOutcome processOutcome);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Void> submitAssessments(Set<Long> assessments);
}