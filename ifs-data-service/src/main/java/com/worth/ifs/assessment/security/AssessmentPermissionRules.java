package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.assessment.resource.AssessmentSubmissionsResource;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

import static com.worth.ifs.assessment.resource.AssessmentStates.*;

/**
 * Provides the permissions around CRUD operations for {@link com.worth.ifs.assessment.domain.Assessment} resources.
 */
@Component
@PermissionRules
public class AssessmentPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ_DASHBOARD", description = "Assessors can view all Assessments on the competition dashboard, except those rejected")
    public boolean userCanReadAssessmentOnDashboard(AssessmentResource assessment, UserResource user) {
        Set<AssessmentStates> allowedStates = EnumSet.of(PENDING, ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    @PermissionRule(value = "READ", description = "Assessors can directly read Assessments, except those rejected or submitted")
    public boolean userCanReadAssessment(AssessmentResource assessment, UserResource user) {
        Set<AssessmentStates> allowedStates = EnumSet.of(PENDING, ACCEPTED, OPEN, READY_TO_SUBMIT);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    @PermissionRule(value = "READ_SCORE", description = "Assessors can read the score of Assessments except those pending or rejected")
    public boolean userCanReadAssessmentScore(AssessmentResource assessment, UserResource user) {
        Set<AssessmentStates> allowedStates = EnumSet.of(ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    @PermissionRule(value = "UPDATE", description = "Only owners can update Assessments")
    public boolean userCanUpdateAssessment(AssessmentResource assessment, UserResource user) {
        Set<AssessmentStates> allowedStates = EnumSet.of(PENDING, ACCEPTED, OPEN, READY_TO_SUBMIT);
        return isAssessorForAssessment(assessment, user, allowedStates);
    }

    @PermissionRule(value = "SUBMIT", description = "Only owners can submit Assessments")
    public boolean userCanSubmitAssessments(AssessmentSubmissionsResource submissions, UserResource user) {
        return assessmentRepository.findAll(submissions.getAssessmentIds()).stream()
                .allMatch(assessment -> assessment.getParticipant().getUser().getId().equals(user.getId()));
    }

    private boolean isAssessorForAssessment(AssessmentResource assessment, UserResource user, Set<AssessmentStates> allowedStates) {
        Long assessmentUser = processRoleRepository.findOne(assessment.getProcessRole()).getUser().getId();
        return user.getId().equals(assessmentUser) && assessmentHasViewableState(assessment, allowedStates);
    }

    private boolean assessmentHasViewableState(AssessmentResource assessmentResource, Set<AssessmentStates> allowedStates) {
        Assessment assessment = assessmentRepository.findOne(assessmentResource.getId());
        return allowedStates.contains(assessment.getActivityState());
    }
}
