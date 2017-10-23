package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.invite.resource.AssessmentPanelInviteResource;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Provides the permissions around CRUD operations for {@link org.innovateuk.ifs.invite.domain.AssessmentPanelInvite} resources.
 */
@Component
@PermissionRules
public class AssessmentPanelInvitePermissionRules extends BasePermissionRules {
    @PermissionRule(value = "READ_ASSESSMENT_PANEL_INVITES", description = "An assessor may only view their own invites to assessment panels")
    public boolean userCanViewInvites(List<AssessmentPanelInviteResource> invite, UserResource user) {
        return invite != null &&
                 user != null &&
                 user.getId() == invite.get(0).getUserId();
    }
}
