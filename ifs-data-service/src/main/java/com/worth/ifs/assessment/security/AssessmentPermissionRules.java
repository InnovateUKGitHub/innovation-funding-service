package com.worth.ifs.assessment.security;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.security.BasePermissionRules;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.worth.ifs.assessment.resource.AssessmentStates.*;
import static java.util.Arrays.asList;

/**
 * Provides the permissions around CRUD operations for {@link com.worth.ifs.assessment.domain.Assessment} resources.
 */
@Component
@PermissionRules
public class AssessmentPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ_DASHBOARD", description = "Assessors can view all Assessments on the competition dashboard, except those rejected")
    public boolean userCanReadAssessmentOnDashboard(AssessmentResource assessment, UserResource user) {
        List<AssessmentStates> allowedDashboardReadStates = asList(PENDING, ACCEPTED, OPEN, READY_TO_SUBMIT, SUBMITTED);
        return isAssessorForAssessment(assessment, user, allowedDashboardReadStates);
    }

    @PermissionRule(value = "READ", description = "Assessors can directly read Assessments, except those rejected or submitted")
    public boolean userCanReadAssessment(AssessmentResource assessment, UserResource user) {
        List<AssessmentStates> allowedNonDashboardReadStates = asList(PENDING, ACCEPTED, OPEN, READY_TO_SUBMIT);
        return isAssessorForAssessment(assessment, user, allowedNonDashboardReadStates);
    }

    @PermissionRule(value = "ASSIGN", description = "Assessors can only accept or reject assessments that are pending and the accept date hasn't passed")
    public boolean userCanAssignAssessment(AssessmentResource assessment, UserResource user) {
        Competition competition = competitionRepository.findById(assessment.getCompetition());
        List<AssessmentStates> allowedAssignStates = Collections.singletonList(PENDING);
        return competition.getAssessorAcceptsDate().isAfter(LocalDateTime.now()) && isAssessorForAssessment(assessment, user, allowedAssignStates);
    }

    @PermissionRule(value = "UPDATE", description = "Only owners can update Assessments")
    public boolean userCanUpdateAssessment(AssessmentResource assessment, UserResource user) {
        List<AssessmentStates> allowedUpdateStates = asList(PENDING, ACCEPTED, OPEN, READY_TO_SUBMIT);
        return isAssessorForAssessment(assessment, user, allowedUpdateStates);
    }

    private boolean isAssessorForAssessment(AssessmentResource assessment, UserResource user, List<AssessmentStates> allowedStates) {
        Long assessmentUser = processRoleRepository.findOne(assessment.getProcessRole()).getUser().getId();
        return user.getId().equals(assessmentUser) && assessmentHasViewableState(assessment, allowedStates);
    }

    private boolean assessmentHasViewableState(AssessmentResource assessmentResource, List<AssessmentStates> allowedStates) {
        Assessment assessment = assessmentRepository.findOne(assessmentResource.getId());
        return allowedStates.stream().anyMatch(state -> state.equals(assessment.getActivityState()));
    }
}
