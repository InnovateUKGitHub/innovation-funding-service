package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.assessment.domain.Assessment} data.
 */
public interface AssessmentService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<AssessmentResource> findById(Long id);

    @PostFilter("hasPermission(filterObject, 'READ_DASHBOARD')")
    ServiceResult<List<AssessmentResource>> findByUserAndCompetition(Long userId, Long competitionId);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'READ_SCORE')")
    ServiceResult<AssessmentTotalScoreResource> getTotalScore(Long assessmentId);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'UPDATE')")
    ServiceResult<Void> recommend(Long assessmentId, AssessmentFundingDecisionResource assessmentFundingDecision);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'UPDATE')")
    ServiceResult<Void> rejectInvitation(Long assessmentId, ApplicationRejectionResource applicationRejection);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'UPDATE')")
    ServiceResult<Void> acceptInvitation(Long assessmentId);

    @PreAuthorize("hasPermission(#assessmentSubmissions, 'SUBMIT')")
    ServiceResult<Void> submitAssessments(@P("assessmentSubmissions") AssessmentSubmissionsResource assessmentSubmissionsResource);

    @SecuredBySpring(value = "CREATE", description = "Comp Admins/Execs can assign an Assessor to an Application")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'competition_executive')")
    ServiceResult<AssessmentResource> createAssessment(AssessmentCreateResource assessmentCreateResource);
}
