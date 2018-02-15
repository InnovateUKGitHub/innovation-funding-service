package org.innovateuk.ifs.interview.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.resource.InterviewResource;
import org.innovateuk.ifs.interview.resource.InterviewState;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;

import static org.innovateuk.ifs.interview.resource.InterviewState.PENDING;


/**
 * Provides the permissions around CRUD operations for {@link Interview} resources.
 */
@Component
@PermissionRules
public class InterviewPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ_INTERVIEW_DASHBOARD", description = "Assessors can view all Assessment Interviews on the competition " +
            "dashboard")
    public boolean userCanReadAssessmentInterviewOnDashboard(InterviewResource assessmentInterview, UserResource user) {
        Set<InterviewState> allowedStates = EnumSet.of(PENDING);
        return isAssessorForAssessmentInterview(assessmentInterview, user, allowedStates);
    }

    @PermissionRule(value = "UPDATE", description = "An assessor may only update their own invites to assessment Interviews")
    public boolean userCanUpdateAssessmentInterview(InterviewResource assessmentInterview, UserResource loggedInUser) {
        return isAssessorForAssessmentInterview(assessmentInterview, loggedInUser);
    }

    @PermissionRule(value = "READ", description = "An assessor may only read their own invites to assessment Interviews")
    public boolean userCanReadAssessmentInterviews(InterviewResource assessmentInterview, UserResource loggedInUser) {
        return isAssessorForAssessmentInterview(assessmentInterview, loggedInUser);
    }

    private boolean isAssessorForAssessmentInterview(InterviewResource assessmentInterview, UserResource user, Set<InterviewState> allowedStates) {
        return isAssessorForAssessmentInterview(assessmentInterview, user) && assessmentInterviewIsInState(assessmentInterview, allowedStates);
    }

    private boolean isAssessorForAssessmentInterview(InterviewResource assessmentInterview, UserResource user) {
        Long assessmentUser = processRoleRepository.findOne(assessmentInterview.getProcessRole()).getUser().getId();
        return user.getId().equals(assessmentUser);
    }

    private boolean assessmentInterviewIsInState(InterviewResource interviewResource, Set<InterviewState> allowedStates) {
        Interview interview = interviewRepository.findOne(interviewResource.getId());
        return allowedStates.contains(interview.getActivityState());
    }
}