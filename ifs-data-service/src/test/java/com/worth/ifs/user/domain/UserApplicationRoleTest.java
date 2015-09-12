package com.worth.ifs.user.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserApplicationRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UserApplicationRoleTest {
    UserApplicationRole userApplicationRoleTest;

    Long id;
    User user;
    Application application;
    Role role;
    Organisation organisation;

    @Before
    public void setUp() throws Exception {
        id = 0L;
        user = new User();
        application = new Application();
        role = new Role();
        organisation = new Organisation();

        userApplicationRoleTest = new UserApplicationRole(id, user, application, role, organisation);
    }

    @Test
    public void userApplicationRoleShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(userApplicationRoleTest.getUser(), user);
        Assert.assertEquals(userApplicationRoleTest.getId(), id);
        Assert.assertEquals(userApplicationRoleTest.getApplication(), application);
        Assert.assertEquals(userApplicationRoleTest.getRole(), role);
        Assert.assertEquals(userApplicationRoleTest.getOrganisation(), organisation);

    }
}