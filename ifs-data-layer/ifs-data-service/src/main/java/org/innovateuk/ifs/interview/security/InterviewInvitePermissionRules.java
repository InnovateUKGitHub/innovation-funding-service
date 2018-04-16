package org.innovateuk.ifs.interview.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.interview.domain.InterviewInvite;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link InterviewInvite} resources.
 */
@Component
@PermissionRules
public class InterviewInvitePermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ_INTERVIEW_PANEL_INVITES", description = "An assessor may only view their own invites to interview panels")
    public boolean userCanViewInvites(UserResource inviteUser, UserResource loggedInUser) {
        return inviteUser != null &&
                loggedInUser != null &&
                inviteUser.equals(loggedInUser);
    }
}
