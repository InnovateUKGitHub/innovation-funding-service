package org.innovateuk.ifs.user.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdmin;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isSupport;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RoleProfileStatusPermissionRolesTest extends BasePermissionRulesTest<RoleProfileStatusPermissionRoles> {


    @Override
    protected RoleProfileStatusPermissionRoles supplyPermissionRulesUnderTest() {
        return new RoleProfileStatusPermissionRoles();
    }

    @Test
    public void internalUsersCanRetrieveRoleProfiles() {

        UserResource otherUser = newUserResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (isInternalAdmin(user) || isSupport(user)) {
                System.out.println(user.getRoleDisplayNames());
                assertTrue(rules.adminsAndSupportCanRetrieveUserRoleProfile(otherUser, user));
            } else {
                System.out.println(user.getRoleDisplayNames());
                assertFalse(rules.adminsAndSupportCanRetrieveUserRoleProfile(otherUser, user));
            }
        });
    }

    @Test
    public void usersCanRetrieveTheirOwnRoleProfiles() {
        UserResource user = newUserResource().build();
        assertTrue(rules.usersCanRetrieveTheirOwnUserRoleProfile(user, user));
    }
}
