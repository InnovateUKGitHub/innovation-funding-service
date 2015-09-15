package com.worth.ifs.user.domain;

import com.worth.ifs.application.domain.Application;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ProcessRoleTest {
    ProcessRole processRoleTest;

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

        processRoleTest = new ProcessRole(id, user, application, role, organisation);
    }

    @Test
    public void userApplicationRoleShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(processRoleTest.getUser(), user);
        Assert.assertEquals(processRoleTest.getId(), id);
        Assert.assertEquals(processRoleTest.getApplication(), application);
        Assert.assertEquals(processRoleTest.getRole(), role);
        Assert.assertEquals(processRoleTest.getOrganisation(), organisation);

    }
}