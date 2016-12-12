package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.resource.AssessmentSubmissionsResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;

/**
 * Provides the permissions around CRUD operations for {@link org.innovateuk.ifs.assessment.domain.Assessment} resources.
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

    @PermissionRule(value = "ASSIGN", description = "Assessors can only accept or reject assessments that are pending")
    public boolean userCanAssignAssessment(AssessmentResource assessment, UserResource user) {
        Set<AssessmentStates> allowedAssignStates = Collections.singleton(PENDING);
        return isAssessorForAssessment(assessment, user, allowedAssignStates);
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
