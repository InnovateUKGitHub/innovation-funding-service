package org.innovateuk.ifs.management.admin.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserCompositeId;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isIFSAdmin;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isSupport;

/**
 * Permission checker around the access to user admin
 */
@PermissionRules
@Component
public class UserPermissionRules {

    private UserRestService userRestService;

    protected UserPermissionRules() {
    }

    @Autowired
    public UserPermissionRules(UserRestService userRestService) {
        this.userRestService = userRestService;
    }

    @PermissionRule(value = "ACCESS_USER", description = "Support users can access internal users, Admin users can access internal and external users")
    public boolean accessUser(UserCompositeId userCompositeId, UserResource user) {
        UserResource editUser = userRestService.retrieveUserById(userCompositeId.id()).getSuccess();
        return ((editUser.isExternalUser() || editUser.isInternalUser()) && isIFSAdmin(user)) ||
                (editUser.isExternalUser() && isSupport(user));
    }

    @PermissionRule(value = "EDIT_USER", description = "Only active, internal users can be edited")
    public boolean editUser(UserCompositeId userCompositeId, UserResource user) {
        UserResource editUser = userRestService.retrieveUserById(userCompositeId.id()).getSuccess();
        return editUser != null && UserStatus.ACTIVE.equals(editUser.getStatus()) && accessUser(userCompositeId, user);
    }
}