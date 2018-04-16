package org.innovateuk.ifs.review.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link ReviewInvite} resources.
 */
@Component
@PermissionRules
public class ReviewInvitePermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ_ASSESSMENT_PANEL_INVITES", description = "An assessor may only view their own invites to assessment panels")
    public boolean userCanViewInvites(UserResource inviteUser, UserResource loggedInUser) {
        return inviteUser != null &&
                loggedInUser != null &&
                inviteUser.equals(loggedInUser);
    }
}
