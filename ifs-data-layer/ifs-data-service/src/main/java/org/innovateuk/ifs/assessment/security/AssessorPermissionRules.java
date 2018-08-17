package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;

@Component
@PermissionRules
public class AssessorPermissionRules extends BasePermissionRules {

    @PermissionRule(value = "READ_PROFILE", description = "Assessor can read their own profile")
    public boolean assessorCanReadTheirOwnProfile(AssessorProfileResource assessorProfileResource, UserResource loggedInUser) {

        if (assessorProfileResource.getUser() == null) {
            return false;
        }

        return loggedInUser.getId().equals(assessorProfileResource.getUser().getId());
    }

    @PermissionRule(value = "READ_PROFILE", description = "Comp admin can read any assessor profile")
    public boolean compAdminCanReadAssessorProfile(AssessorProfileResource assessorProfileResource, UserResource loggedInUser) {
        return loggedInUser.hasAnyRoles(COMP_ADMIN, PROJECT_MANAGER);
    }

}
