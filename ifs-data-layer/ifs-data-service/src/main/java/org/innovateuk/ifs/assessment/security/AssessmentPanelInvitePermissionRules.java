package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.stereotype.Component;

/**
 * Provides the permissions around CRUD operations for {@link org.innovateuk.ifs.invite.domain.AssessmentPanelInvite} resources.
 */

@Component
@PermissionRules
public class AssessmentPanelInvitePermissionRules extends BasePermissionRules {
    @PermissionRule(value = "READ_ASSESSMENT_PANEL_INVITES", description = "an assessor may only view their own invites to assessment panels")
    public boolean userCanViewInvites(AssessmentPanelInviteResource invite, UserResource user) {
        return invite != null &&
                user != null &&
                user.getId() == invite.getUserId();
    }
}
