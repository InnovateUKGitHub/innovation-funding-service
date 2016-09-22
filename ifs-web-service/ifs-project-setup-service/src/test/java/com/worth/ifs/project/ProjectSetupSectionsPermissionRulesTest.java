package com.worth.ifs.project;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static com.worth.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ProjectSetupSectionsPermissionRulesTest extends BasePermissionRulesTest<ProjectSetupSectionsPermissionRules> {

    private UserResource user = newUserResource().build();

    @Test
    public void testAllowedAccess() {

        OrganisationResource organisation = newOrganisationResource().build();

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(user.getId()).
                withRoleName(UserRoleType.PARTNER).
                withOrganisation(organisation.getId()).
                build(1);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withOrganisationId(organisation.getId()).
                        build(1)).
                build();


        when(projectServiceMock.getProjectTeamStatus(123L)).thenReturn(teamStatus);
        when(projectServiceMock.getProjectUsersForProject(123L)).thenReturn(projectUsers);
        when(organisationServiceMock.getOrganisationById(organisation.getId())).thenReturn(organisation);

        assertTrue(rules.partnerCanAccessProjectDetailsSection(123L, user));
    }

    @Override
    protected ProjectSetupSectionsPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectSetupSectionsPermissionRules();
    }
}
