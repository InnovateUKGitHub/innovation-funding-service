package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StatusPermissionRulesTest extends BasePermissionRulesTest<StatusPermissionRules> {

    @Override
    protected StatusPermissionRules supplyPermissionRulesUnderTest() {
        return new StatusPermissionRules();
    }

    @Test
    public void testPartnersCanViewTeamStatus() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanViewTeamStatus(project, user));
    }

    @Test
    public void testNonPartnersCannotViewTeamStatus() {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanViewTeamStatus(project, user));
    }

    @Test
    public void testInternalUsersCanViewTeamStatus() {
        ProjectResource project = newProjectResource().build();
        assertTrue(rules.internalUsersCanViewTeamStatus(project, compAdminUser()));
        assertTrue(rules.internalUsersCanViewTeamStatus(project, projectFinanceUser()));
    }
}
