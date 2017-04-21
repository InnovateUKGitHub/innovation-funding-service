package org.innovateuk.ifs.project.monitoringofficer.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.security.ProjectMonitoringOfficerPermissionRules;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectMonitoringOfficerPermissionRulesTest extends BasePermissionRulesTest<ProjectMonitoringOfficerPermissionRules> {

    @Override
    protected ProjectMonitoringOfficerPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectMonitoringOfficerPermissionRules();
    }

    @Test
    public void testInternalUsersCanViewMonitoringOfficersOnProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanViewMonitoringOfficersForAnyProject(project, user));
            } else {
                assertFalse(rules.internalUsersCanViewMonitoringOfficersForAnyProject(project, user));
            }
        });
    }

    @Test
    public void testPartnersCanViewMonitoringOfficersOnTheirOwnProjects() {

        UserResource user = newUserResource().build();
        ProjectResource project = newProjectResource().build();

        setupUserAsPartner(project, user);

        assertTrue(rules.partnersCanViewMonitoringOfficersOnTheirProjects(project, user));
    }

    @Test
    public void testPartnersCanViewMonitoringOfficersOnTheirOwnProjectsButUserNotPartner() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        Role partnerRole = newRole().build();

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(emptyList());

        assertFalse(rules.partnersCanViewMonitoringOfficersOnTheirProjects(project, user));
    }

    @Test
    public void testInternalUsersCanEditMonitoringOfficersOnProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanAssignMonitoringOfficersForAnyProject(project, user));
            } else {
                assertFalse(rules.internalUsersCanAssignMonitoringOfficersForAnyProject(project, user));
            }
        });
    }
}
