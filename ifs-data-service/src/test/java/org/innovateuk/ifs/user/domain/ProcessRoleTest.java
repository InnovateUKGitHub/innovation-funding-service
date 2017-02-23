package org.innovateuk.ifs.user.domain;

import org.innovateuk.ifs.application.domain.Application;
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

        processRoleTest = new ProcessRole(id, user, application.getId(), role, organisation.getId());
    }

    @Test
    public void userApplicationRoleShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(processRoleTest.getUser(), user);
        Assert.assertEquals(processRoleTest.getId(), id);
        Assert.assertEquals(processRoleTest.getApplicationId(), application.getId());
        Assert.assertEquals(processRoleTest.getRole(), role);
        Assert.assertEquals(processRoleTest.getOrganisationId(), organisation.getId());
    }
}
