package com.worth.ifs.assessment.transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.dto.Feedback;
import com.worth.ifs.assessment.dto.Score;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.workflow.domain.ProcessOutcome;

import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

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

    @PreAuthorize("hasPermission(#a, 'UPDATE')")
    ServiceResult<Void> save(Assessment a);

    @PreAuthorize("hasPermission(#a, 'UPDATE')")
    ServiceResult<Assessment> saveAndGet(Assessment a);

    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.assessment.domain.Assessment', 'READ')")
    ServiceResult<Assessment> getOne(Long id);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<Assessment>> getAllByCompetitionAndAssessor(Long competitionId, Long assessorId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
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