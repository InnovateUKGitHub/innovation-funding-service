package org.innovateuk.ifs.project.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.resource.ProjectResource;
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

public class ProjectPermissionRulesTest extends BasePermissionRulesTest<ProjectPermissionRules> {

    @Override
    protected ProjectPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectPermissionRules();
    }

    @Test
    public void testPartnersOnProjectCanView() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        setupUserAsPartner(project, user);

        assertTrue(rules.partnersOnProjectCanView(project, user));
    }

    @Test
    public void testPartnersOnProjectCanViewButUserNotPartner() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        Role partnerRole = newRole().build();

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(project.getId(), user.getId(), PROJECT_PARTNER)).thenReturn(emptyList());

        assertFalse(rules.partnersOnProjectCanView(project, user));
    }

    @Test
    public void testInternalUsersCanViewProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (allInternalUsers.contains(user)) {
                assertTrue(rules.internalUsersCanViewProjects(project, user));
            } else {
                assertFalse(rules.internalUsersCanViewProjects(project, user));
            }
        });
    }
}
