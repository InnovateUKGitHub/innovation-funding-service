package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StatusPermissionRulesTest extends BasePermissionRulesTest<StatusPermissionRules> {
    private ProjectResource project = newProjectResource().build();
    private UserResource user = newUserResource().build();

    @Override
    protected StatusPermissionRules supplyPermissionRulesUnderTest() {
        return new StatusPermissionRules();
    }

    @Test
    public void testPartnersCanViewTeamStatus() {
        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanViewTeamStatus(project, user));
    }

    @Test
    public void testNonPartnersCannotViewTeamStatus() {
        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanViewTeamStatus(project, user));
    }

    @Test
    public void testInternalUsersCanViewTeamStatus() {
        assertTrue(rules.internalUsersCanViewTeamStatus(project, compAdminUser()));
        assertTrue(rules.internalUsersCanViewTeamStatus(project, projectFinanceUser()));
    }

    @Test
    public void testPartnersCanViewStatus(){
        setupUserAsPartner(project, user);
        assertTrue(rules.partnersCanViewStatus(project, user));
    }

    @Test
    public void testNonPartnersCannotViewStatus(){
        setupUserNotAsPartner(project, user);
        assertFalse(rules.partnersCanViewStatus(project, user));
    }

    @Test
    public void testInternalUsersCanViewStatus(){
        allGlobalRoleUsers.forEach(user -> {
            if (isInternal(user)) {
                assertTrue(rules.internalUsersCanViewStatus(newProjectResource().build(), user));
            } else {
                assertFalse(rules.internalUsersCanViewStatus(newProjectResource().build(), user));
            }
        });
    }

    @Test
    public void testInternalAdminTeamCanViewCompetitionStatus(){

    }

    @Test
    public void testSupportCanViewCompetitionStatus(){

    }

    @Test
    public void testAssignedInnovationLeadCanViewCompetitionStatus(){

    }

    @Test
    public void testInternalAdminTeamCanViewProjectStatus(){

    }

    @Test
    public void testSupportCanViewProjectStatus(){

    }

    @Test
    public void testAssignedInnovationLeadCanViewProjectStatus(){

    }
}
