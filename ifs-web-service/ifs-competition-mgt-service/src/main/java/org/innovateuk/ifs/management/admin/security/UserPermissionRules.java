package org.innovateuk.ifs.management.admin.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.UserCompositeId;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

/**
 * Permission checker around the access to user admin
 */
@PermissionRules
@Component
public class UserPermissionRules {

    @Autowired
    private UserRestService userRestService;

    @PermissionRule(value = "ACCESS_USER", description = "Only internal users can be accessed")
    public boolean accessUser(UserCompositeId userCompositeId, UserResource user) {
        UserResource editUser = userRestService.retrieveUserById(userCompositeId.id()).getSuccess();
        return editUser.isInternalUser() && isIFSAdmin(user) || (editUser.isExternalUser() && (isIFSAdmin(user) || isSupport(user)));
    }

//    @PermissionRule(value = "EDIT_INTERNAL_USER", description = "Only active, internal users can be edited")
//    public boolean canEditInternalUser(UserCompositeId userCompositeId, UserResource user) {
//        UserResource editUser = userRestService.retrieveUserById(userCompositeId.id()).getSuccess();
//        return editUser != null && UserStatus.ACTIVE.equals(editUser.getStatus()) && internalUser(userCompositeId, user);
//    }

    @PermissionRule(value = "EDIT_USER", description = "Only active, internal users can be edited")
    public boolean editInternalUser(UserCompositeId userCompositeId, UserResource user) {
        UserResource editUser = userRestService.retrieveUserById(userCompositeId.id()).getSuccess();
        return editUser != null && UserStatus.ACTIVE.equals(editUser.getStatus()) && accessUser(userCompositeId, user);
    }
}