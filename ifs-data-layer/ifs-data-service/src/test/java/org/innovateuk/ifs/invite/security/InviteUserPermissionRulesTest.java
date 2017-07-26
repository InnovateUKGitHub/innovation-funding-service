package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.user.builder.RoleResourceBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InviteUserPermissionRulesTest extends BasePermissionRulesTest<InviteUserPermissionRules> {

    private UserResource invitedUser;
    private UserResource ifsAdmin;
    private UserResource nonIfsAdmin;

    @Override
    protected InviteUserPermissionRules supplyPermissionRulesUnderTest() {
        return new InviteUserPermissionRules();
    }

    @Before
    public void setup() throws Exception {

        invitedUser = UserResourceBuilder.newUserResource().build();

        RoleResource ifsAdminRole = RoleResourceBuilder.newRoleResource()
                .withName("ifs_administrator")
                .build();

        ifsAdmin = UserResourceBuilder.newUserResource()
                .withRolesGlobal(Collections.singletonList(ifsAdminRole))
                .build();

        RoleResource nonIfsAdminRole = RoleResourceBuilder.newRoleResource()
                .withName("support")
                .build();

        nonIfsAdmin = UserResourceBuilder.newUserResource()
                .withRolesGlobal(Collections.singletonList(nonIfsAdminRole))
                .build();
    }

    @Test
    public void testIfsAdminCanSaveNewUserInvite() {
        assertTrue(rules.ifsAdminCanSaveNewUserInvite(invitedUser, ifsAdmin));
        assertFalse(rules.ifsAdminCanSaveNewUserInvite(invitedUser, nonIfsAdmin));
    }

    @Test
    public void internalUsersCanViewPendingInternalUserInvites() {
        RoleInvitePageResource invite = new RoleInvitePageResource();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanViewPendingInternalUserInvites(invite, user));
            } else {
                assertFalse(rules.internalUsersCanViewPendingInternalUserInvites(invite, user));
            }
        });
    }
}

