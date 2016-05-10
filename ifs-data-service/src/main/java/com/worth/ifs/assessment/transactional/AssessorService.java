package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.Feedback;
import com.worth.ifs.assessment.resource.Score;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
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
    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.assessment.resource.Feedback', 'UPDATE')")
    ServiceResult<Feedback> updateAssessorFeedback(@P("id") Feedback.Id feedbackId, Optional<String> feedbackValue, Optional<String> feedbackText);

    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.assessment.resource.Feedback', 'READ')")
    ServiceResult<Feedback> getFeedback(@P("id") Feedback.Id id);

    @PreAuthorize("hasPermission(#a, 'UPDATE')")
    ServiceResult<Void> save(AssessmentResource a);

    @PreAuthorize("hasPermission(#a, 'UPDATE')")
    ServiceResult<AssessmentResource> saveAndGet(AssessmentResource a);

    @PreAuthorize("hasPermission(#id, 'com.worth.ifs.assessment.domain.Assessment', 'READ')")
    ServiceResult<Assessment> getOne(Long id);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<AssessmentResource>> getAllByCompetitionAndAssessor(Long competitionId, Long assessorId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<AssessmentResource> getOneByProcessRole(Long processRoleId);

    @NotSecured(value = "TODO DW - implement when permissions matrix available", mustBeSecuredByOtherServices = false)
    ServiceResult<Integer> getTotalSubmittedAssessmentsByCompetition(Long competitionId, Long assessorId);

    @NotSecured(value = "TODO DW - implement when permissions matrix available", mustBeSecuredByOtherServices = false)
    ServiceResult<Integer> getTotalAssignedAssessmentsByCompetition(Long competitionId, Long assessorId);

    @NotSecured(value = "TODO DW - implement when permissions matrix available", mustBeSecuredByOtherServices = false)
    ServiceResult<Score> getScore(Long id);

    @NotSecured(value = "TODO DW - implement when permissions matrix available", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> submitAssessment(Long assessorId, Long applicationId, String suitableValue, String suitableFeedback, String comments);

    @NotSecured(value = "TODO DW - implement when permissions matrix available", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> acceptAssessmentInvitation(Long processRoleId, AssessmentResource assessment);

    @NotSecured(value = "TODO DW - implement when permissions matrix available", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> rejectAssessmentInvitation(Long processRoleId, ProcessOutcome processOutcome);

    @NotSecured(value = "TODO DW - implement when permissions matrix available", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> submitAssessments(Set<Long> assessments);
}
