package org.innovateuk.ifs.admin.security;


import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class UserPermissionRulesTest extends BasePermissionRulesTest<UserPermissionRules> {

    @Override
    protected UserPermissionRules supplyPermissionRulesUnderTest() {
        return new UserPermissionRules();
    }

    @Test
    public void activeUserCanBeEdited() {
        RoleResource role = newRoleResource().withType(UserRoleType.PROJECT_FINANCE).build();
        UserResource user = newUserResource().withRolesGlobal(singletonList(role)).build();
        UserResource editUser = newUserResource().withStatus(UserStatus.ACTIVE).build();
        when(userServiceMock.findById(1L)).thenReturn(editUser);
        assertTrue(rules.activeUsersCanBeEdited(1L, user));
    }
}
