package org.innovateuk.ifs.user;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;

public class UserResourceTest {

    @Test
    public void testInternalUserMethod() {

        Set<UserRoleType> expectedInternalRoles = UserRoleType.internalUserRoleTypes();

        stream(UserRoleType.values()).forEach(type -> {

            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(Role.getByName(type.getName()))).build();
            assertEquals(expectedInternalRoles.contains(type), userWithRole.isInternalUser());
        });
    }
}
