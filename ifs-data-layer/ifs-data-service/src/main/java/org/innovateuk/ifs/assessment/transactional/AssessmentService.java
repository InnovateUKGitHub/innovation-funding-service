package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;

import java.util.List;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.assessment.domain.Assessment} data.
 */
public interface AssessmentService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<AssessmentResource> findById(long id);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Boolean> existsByTargetId(long applicationId);

    @PostAuthorize("hasPermission(returnObject, 'READ_TO_ASSIGN')")
    ServiceResult<AssessmentResource> findAssignableById(long id);

    @PostAuthorize("hasPermission(returnObject, 'READ_TO_REJECT')")
    ServiceResult<AssessmentResource> findRejectableById(long id);

    @PostFilter("hasPermission(filterObject, 'READ_DASHBOARD')")
    ServiceResult<List<AssessmentResource>> findByUserAndCompetition(long userId, long competitionId);

    @PostFilter("hasPermission(filterObject, 'READ_DASHBOARD')")
    ServiceResult<List<AssessmentResource>> findByUserAndApplication(long userId, long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(
            value = "READ_BY_STATE_AND_COMPETITION",
            description = "Comp admins and execs can see assessments in a particular state per competition")
    ServiceResult<List<AssessmentResource>> findByStateAndCompetition(AssessmentState state, long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(
            value = "COUNT_BY_STATE_AND_COMPETITION",
            description = "Comp admins and execs can see a count of assessments in a particular state per competition")
    ServiceResult<Integer> countByStateAndCompetition(AssessmentState state, long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(
            value = "COUNT_BY_STATE_AND_ASSESSMENT_PERIOD",
            description = "Comp admins and execs can see a count of assessments in a particular state per assessment period")
    ServiceResult<Integer> countByStateAndAssessmentPeriodId(AssessmentState state, long assessmentPeriod);


    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'READ_SCORE')")
    ServiceResult<AssessmentTotalScoreResource> getTotalScore(long assessmentId);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'UPDATE')")
    ServiceResult<Void> recommend(long assessmentId, AssessmentFundingDecisionOutcomeResource assessmentFundingDecision);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<ApplicationAssessmentFeedbackResource> getApplicationFeedback(long applicationId);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'UPDATE')")
    ServiceResult<Void> rejectInvitation(long assessmentId, AssessmentRejectOutcomeResource assessmentRejectOutcomeResource);

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "WITHDRAW_ASSESSOR", description = "Comp Admins can withdraw an application from an assessor")
    ServiceResult<Void> withdrawAssessment(long assessmentId);

    @PreAuthorize("hasAnyAuthority('super_admin_user')")
    @SecuredBySpring(value = "UNSUBMIT_ASSESSMENT", description = "Super admin can unsubmit an assessor's assessment")
    ServiceResult<Void> unsubmitAssessment(long assessmentId);

    @PreAuthorize("hasPermission(#assessmentId, 'org.innovateuk.ifs.assessment.resource.AssessmentResource', 'UPDATE')")
    ServiceResult<Void> acceptInvitation(long assessmentId);

    @PreAuthorize("hasPermission(#assessmentSubmissions, 'SUBMIT')")
    ServiceResult<Void> submitAssessments(@P("assessmentSubmissions") AssessmentSubmissionsResource assessmentSubmissionsResource);

    @SecuredBySpring(value = "CREATE", description = "Comp Admins can assign an Assessor to an Application")
    @PreAuthorize("hasAnyAuthority('comp_admin')")
    ServiceResult<AssessmentResource> createAssessment(AssessmentCreateResource assessmentCreateResource);

    @SecuredBySpring(value = "CREATE", description = "Comp Admins can assign an Assessor to an Application")
    @PreAuthorize("hasAnyAuthority('comp_admin')")
    ServiceResult<List<AssessmentResource>> createAssessments(List<AssessmentCreateResource> assessmentCreateResource);
}