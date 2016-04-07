package com.worth.ifs.user.security;

import com.worth.ifs.BasePermissionRulesTest;
import org.junit.Test;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests around the permissions for UserService and related services
 */
public class UserPermissionRulesTest extends BasePermissionRulesTest<UserPermissionRules> {

    @Test
    public void testSystemUserCanCreateUsers() {
        allRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemUserCanCreateUsers(newUserResource().build(), user));
            } else {
                assertFalse(rules.systemUserCanCreateUsers(newUserResource().build(), user));
            }
        });
    }

    @Test
    public void testSystemUserCanActivateUsers() {
        allRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemUserCanActivateUsers(newUserResource().build(), user));
            } else {
                assertFalse(rules.systemUserCanActivateUsers(newUserResource().build(), user));
            }
        });
    }

    @Override
    protected UserPermissionRules supplyPermissionRulesUnderTest() {
        return new UserPermissionRules();
    }
}