package com.worth.ifs.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserApplicationRoleTest {
    UserApplicationRole userApplicationRoleTest;

    long id;
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