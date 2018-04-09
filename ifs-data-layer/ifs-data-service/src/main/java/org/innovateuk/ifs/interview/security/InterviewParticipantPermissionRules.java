package org.innovateuk.ifs.interview.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.interview.domain.InterviewParticipant;
import org.innovateuk.ifs.invite.resource.InterviewParticipantResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link InterviewParticipant} resources.
 */
@Component
@PermissionRules
public class InterviewParticipantPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "ACCEPT", description = "only the same user can accept an interview panel invitation")
    public boolean userCanAcceptInterviewInvite(InterviewParticipantResource interviewParticipant, UserResource user) {
        return user != null &&
                interviewParticipant != null &&
                isSameUser(interviewParticipant, user);
    }

    @PermissionRule(value = "READ", description = "only the same user can read their interview panel participation")
    public boolean userCanViewTheirOwnInterviewParticipation(InterviewParticipantResource assessmentInterviewPanelParticipant, UserResource user) {
        return isSameParticipant(assessmentInterviewPanelParticipant, user);
    }

    private static boolean isSameParticipant(InterviewParticipantResource interviewParticipant, UserResource user) {
        return user.getId().equals(interviewParticipant.getUserId());
    }

    private static boolean isSameUser(InterviewParticipantResource interviewParticipant, UserResource user) {
        return isSameParticipant(interviewParticipant, user)
                || interviewParticipant.getUserId() == null
                && interviewParticipant.getInvite() != null
                && user.getEmail().equals(interviewParticipant.getInvite().getEmail());
    }
}
