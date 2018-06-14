package org.innovateuk.ifs.user.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.assertEquals;

public class ProcessRoleTest {
    private ProcessRole processRoleTest;

    private User user;
    private Application application;
    private Role role;
    private Organisation organisation;

    @Before
    public void setUp() throws Exception {
        user = new User();
        application = newApplication().build();
        role = Role.ASSESSOR;
        organisation = newOrganisation().build();

        processRoleTest = new ProcessRole(user, application.getId(), role, organisation.getId());
    }

    @Test
    public void userApplicationRoleShouldReturnCorrectAttributeValues() throws Exception {
        assertEquals(processRoleTest.getUser(), user);
        assertEquals(processRoleTest.getApplicationId(), application.getId());
        assertEquals(processRoleTest.getRole(), role);
        assertEquals(processRoleTest.getOrganisationId(), organisation.getId());
    }
}
