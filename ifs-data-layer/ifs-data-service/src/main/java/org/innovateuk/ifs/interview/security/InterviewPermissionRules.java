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

import static org.innovateuk.ifs.interview.resource.InterviewState.ASSIGNED;

/**
 * Provides the permissions around CRUD operations for {@link Interview} resources.
 */
@Component
@PermissionRules
public class InterviewPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ_INTERVIEW_DASHBOARD", description = "Assessors can view all Assessment Interviews on the competition " +
            "dashboard")
    public boolean userCanReadInterviewOnDashboard(InterviewResource interview, UserResource user) {
        Set<InterviewState> allowedStates = EnumSet.of(ASSIGNED);
        return isAssessorForInterview(interview, user, allowedStates);
    }

    @PermissionRule(value = "UPDATE", description = "An assessor may only update their own invites to assessment Interviews")
    public boolean userCanUpdateInterview(InterviewResource interview, UserResource loggedInUser) {
        return isAssessorForInterview(interview, loggedInUser);
    }

    @PermissionRule(value = "READ", description = "An assessor may only read their own invites to assessment Interviews")
    public boolean userCanReadInterviews(InterviewResource interview, UserResource loggedInUser) {
        return isAssessorForInterview(interview, loggedInUser);
    }

    private boolean isAssessorForInterview(InterviewResource interview, UserResource user, Set<InterviewState> allowedStates) {
        return isAssessorForInterview(interview, user) && interviewIsInState(interview, allowedStates);
    }

    private boolean isAssessorForInterview(InterviewResource interview, UserResource user) {
        Long assessmentUser = processRoleRepository.findById(interview.getProcessRole()).get().getUser().getId();
        return user.getId().equals(assessmentUser);
    }

    private boolean interviewIsInState(InterviewResource interviewResource, Set<InterviewState> allowedStates) {
        Interview interview = interviewRepository.findById(interviewResource.getId()).get();
        return allowedStates.contains(interview.getProcessState());
    }
}