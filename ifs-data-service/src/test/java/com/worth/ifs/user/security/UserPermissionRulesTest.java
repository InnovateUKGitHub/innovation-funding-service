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
    public void testSystemRegistrationUserCanCreateUsers() {
        allRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanCreateUsers(newUserResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanCreateUsers(newUserResource().build(), user));
            }
        });
    }

    @Test
    public void testSystemRegistrationUserCanActivateUsers() {
        allRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanActivateUsers(newUserResource().build(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanActivateUsers(newUserResource().build(), user));
            }
        });
    }

    @Override
    protected UserPermissionRules supplyPermissionRulesUnderTest() {
        return new UserPermissionRules();
    }
}