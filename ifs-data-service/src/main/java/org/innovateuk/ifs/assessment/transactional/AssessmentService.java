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
    ServiceResult<AssessmentResource> findById(long id);

    @PostAuthorize("hasPermission(returnObject, 'READ_ASSIGNMENT')")
    ServiceResult<AssessmentResource> findAssignableById(long id);

    @PostFilter("hasPermission(filterObject, 'READ_DASHBOARD')")
    ServiceResult<List<AssessmentResource>> findByUserAndCompetition(long userId, long competitionId);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'READ_SCORE')")
    ServiceResult<AssessmentTotalScoreResource> getTotalScore(long assessmentId);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'UPDATE')")
    ServiceResult<Void> recommend(long assessmentId, AssessmentFundingDecisionResource assessmentFundingDecision);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'UPDATE')")
    ServiceResult<Void> rejectInvitation(long assessmentId, ApplicationRejectionResource applicationRejection);

    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value = "WITHDRAW_ASSESSOR", description = "Comp Admins can withdraw an application from an assessor")
    ServiceResult<Void> withdrawAssessment(long assessmentId);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'UPDATE')")
    ServiceResult<Void> acceptInvitation(long assessmentId);

    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value = "NOTIFY_ASSESSOR", description = "Comp Admins can notify an assessor of an assigned application")
    ServiceResult<Void> notify(long assessmentId);

    @PreAuthorize("hasPermission(#assessmentSubmissions, 'SUBMIT')")
    ServiceResult<Void> submitAssessments(@P("assessmentSubmissions") AssessmentSubmissionsResource assessmentSubmissionsResource);

    @SecuredBySpring(value = "CREATE", description = "Comp Admins can assign an Assessor to an Application")
    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<AssessmentResource> createAssessment(AssessmentCreateResource assessmentCreateResource);
}
