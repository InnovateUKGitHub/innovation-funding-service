package com.worth.ifs.user.security;

import org.junit.Test;
import org.mockito.InjectMocks;

/**
 * Tests around the permissions for UserService and related services
 */
public class UserPermissionRulesTest {

    @InjectMocks
    private UserPermissionRules rules = new UserPermissionRules();

    @Test
    public void testAnyoneCanViewACompetition() {
//        UserResource compAdminUser = newUserResource().withRolesGlobal(compAdminRole).build();
//        assertTrue(rules.systemUserCanCreateUsers(), null);
    }
}