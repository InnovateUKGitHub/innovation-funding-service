package org.innovateuk.ifs.assessment.interview.security;

import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterview;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewResource;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState.PENDING;


/**
 * Provides the permissions around CRUD operations for {@link AssessmentInterview} resources.
 */
@Component
@PermissionRules
public class AssessmentInterviewPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ_INTERVIEW_DASHBOARD", description = "Assessors can view all Assessment Interviews on the competition " +
            "dashboard")
    public boolean userCanReadAssessmentInterviewOnDashboard(AssessmentInterviewResource assessmentInterview, UserResource user) {
        Set<AssessmentInterviewState> allowedStates = EnumSet.of(PENDING);
        return isAssessorForAssessmentInterview(assessmentInterview, user, allowedStates);
    }

    @PermissionRule(value = "UPDATE", description = "An assessor may only update their own invites to assessment Interviews")
    public boolean userCanUpdateAssessmentInterview(AssessmentInterviewResource assessmentInterview, UserResource loggedInUser) {
        return isAssessorForAssessmentInterview(assessmentInterview, loggedInUser);
    }

    @PermissionRule(value = "READ", description = "An assessor may only read their own invites to assessment Interviews")
    public boolean userCanReadAssessmentInterviews(AssessmentInterviewResource assessmentInterview, UserResource loggedInUser) {
        return isAssessorForAssessmentInterview(assessmentInterview, loggedInUser);
    }

    private boolean isAssessorForAssessmentInterview(AssessmentInterviewResource assessmentInterview, UserResource user, Set<AssessmentInterviewState> allowedStates) {
        return isAssessorForAssessmentInterview(assessmentInterview, user) && assessmentInterviewIsInState(assessmentInterview, allowedStates);
    }

    private boolean isAssessorForAssessmentInterview(AssessmentInterviewResource assessmentInterview, UserResource user) {
        Long assessmentUser = processRoleRepository.findOne(assessmentInterview.getProcessRole()).getUser().getId();
        return user.getId().equals(assessmentUser);
    }

    private boolean assessmentInterviewIsInState(AssessmentInterviewResource assessmentInterviewResource, Set<AssessmentInterviewState> allowedStates) {
        AssessmentInterview assessmentInterview = assessmentInterviewRepository.findOne(assessmentInterviewResource.getId());
        return allowedStates.contains(assessmentInterview.getActivityState());
    }
}