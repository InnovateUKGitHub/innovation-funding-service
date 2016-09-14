package com.worth.ifs.project.security;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.invite.domain.ProjectParticipantRole;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Collections.emptyList;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Module: innovation-funding-service
 **/
public class ProjectFinancePermissionRulesTest extends BasePermissionRulesTest<ProjectFinancePermissionRules> {

    @Test
    public void testProjectManagerCanViewAnySpendProfileData() throws Exception {

        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setUpUserAsProjectManager(project, user);

        assertTrue(rules.projectManagerCanViewAnySpendProfileData(projectOrganisationCompositeId, user));
    }
    @Test
    public void testUserNotProjectManagerCannotViewSpendProfile() throws Exception {
        ProjectResource project = newProjectResource().build();
        UserResource user = newUserResource().build();
        ProjectOrganisationCompositeId projectOrganisationCompositeId =
                new ProjectOrganisationCompositeId(project.getId(), newOrganisation().build().getId());

        setUpUserNotAsProjectManager(user);

        assertFalse(rules.projectManagerCanViewAnySpendProfileData(projectOrganisationCompositeId, user));
    }

    private void setUpUserAsProjectManager(ProjectResource projectResource, UserResource user) {

        List<ProjectUser> projectManagerUser = newProjectUser().build(1);

        when(projectUserRepositoryMock.findByProjectIdAndUserIdAndRole(projectResource.getId(), user.getId(), ProjectParticipantRole.PROJECT_MANAGER ))
                .thenReturn(projectManagerUser);
    }

    private void setUpUserNotAsProjectManager(UserResource user) {
        List<RoleResource> projectManagerUser = emptyList();
        user.setRoles(projectManagerUser);
    }

    @Override
    protected ProjectFinancePermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectFinancePermissionRules();
    }
}