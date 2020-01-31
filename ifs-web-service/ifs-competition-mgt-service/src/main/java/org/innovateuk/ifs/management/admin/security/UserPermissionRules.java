package org.innovateuk.ifs.management.admin.security;

import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserCompositeId;
import org.innovateuk.ifs.user.resource.UserResource;
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

    @PermissionRule(value = "VIEW_USER_PAGE", description = "Support can view edit user page")
    public boolean ifsAdminCanViewEditUserPage(UserCompositeId userCompositeId, UserResource user) {
        return isIFSAdmin(user);
    }

    @PermissionRule(value = "VIEW_USER_PAGE", description = "Support can view edit user page")
    public boolean supportCanViewEditUserPage(UserCompositeId userCompositeId, UserResource user) {
        return isSupport(user);
    }

    @PermissionRule(value = "VIEW_USER_PAGE", description = "Project finance can view edit user page of assessors")
    public boolean projectFinanceCanViewAssessorsEditUserPage(UserCompositeId userCompositeId, UserResource user) {
        UserResource editUser = userRestService.retrieveUserById(userCompositeId.id()).getSuccess();
        return editUser.hasRole(Role.ASSESSOR) && user.hasRole(Role.PROJECT_FINANCE);
    }

    @PermissionRule(value = "VIEW_USER_PAGE", description = "Comp admins can view edit user page of assessors")
    public boolean compAdminCanViewAssessorsEditUserPage(UserCompositeId userCompositeId, UserResource user) {
        UserResource editUser = userRestService.retrieveUserById(userCompositeId.id()).getSuccess();
        return editUser.hasRole(Role.ASSESSOR) && user.hasRole(Role.COMP_ADMIN);
    }
}