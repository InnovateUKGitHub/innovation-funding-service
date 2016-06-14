package com.worth.ifs.project.service;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.security.ProjectPermissionRules;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static java.util.Collections.emptyList;
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
        Role partnerRole = newRole().build();
        List<ProjectUser> partnerProjectUser = newProjectUser().build(1);

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRoleId(project.getId(), user.getId(), partnerRole.getId())).thenReturn(partnerProjectUser);

        assertTrue(rules.partnersOnProjectCanView(project, user));
    }

    @Test
    public void testPartnersOnProjectCanViewButUserNotPartner() {

        UserResource user = newUserResource().build();

        ProjectResource project = newProjectResource().build();
        Role partnerRole = newRole().build();

        when(roleRepositoryMock.findOneByName(PARTNER.getName())).thenReturn(partnerRole);
        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRoleId(project.getId(), user.getId(), partnerRole.getId())).thenReturn(emptyList());

        assertFalse(rules.partnersOnProjectCanView(project, user));
    }

    @Test
    public void testCompAdminsCanViewProjects() {

        ProjectResource project = newProjectResource().build();

        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(compAdminUser())) {
                assertTrue(rules.compAdminsCanViewProjects(project, user));
            } else {
                assertFalse(rules.compAdminsCanViewProjects(project, user));
            }
        });
    }
}
