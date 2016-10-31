package com.worth.ifs.project;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import com.worth.ifs.project.builder.ProjectLeadStatusResourceBuilder;
import com.worth.ifs.project.builder.ProjectPartnerStatusResourceBuilder;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.project.builder.ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource;
import static com.worth.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static com.worth.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.OrganisationTypeEnum.BUSINESS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ProjectSetupSectionsPermissionRulesTest extends BasePermissionRulesTest<ProjectSetupSectionsPermissionRules> {

    private UserResource user = newUserResource().build();

    private ProjectLeadStatusResourceBuilder leadBuilder = newProjectLeadStatusResource().
            withOrganisationId(456L).
            withOrganisationType(BUSINESS);

    ProjectPartnerStatusResourceBuilder nonLeadBuilder = newProjectPartnerStatusResource().
            withOrganisationId(789L).
            withOrganisationType(BUSINESS);

    @Test
    public void testPartnerCanAccessProjectDetailsSection() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(leadBuilder.build()).
                withPartnerStatuses(nonLeadBuilder.build(1)).
                build();

        assertNonLeadPartnerSuccessfulAccess(teamStatus, () -> rules.partnerCanAccessProjectDetailsSection(123L, user));
    }

    @Test
    public void testPartnerCanAccessProjectDetailsSectionButNotOnProject() {

        when(projectServiceMock.getProjectUsersForProject(123L)).thenReturn(
                newProjectUserResource().withUser(999L).withOrganisation(456L).withRoleName(UserRoleType.PARTNER).build(1));

        assertFalse(rules.partnerCanAccessProjectDetailsSection(123L, user));

        verify(projectServiceMock).getProjectUsersForProject(123L);
        verify(projectServiceMock, never()).getProjectTeamStatus(123L, Optional.of(user.getId()));
    }

    @Test
    public void testPartnerCanAccessProjectDetailsSectionButForbiddenException() {

        when(projectServiceMock.getProjectUsersForProject(123L)).thenReturn(
                newProjectUserResource().withUser(user.getId()).withOrganisation(456L).withRoleName(UserRoleType.PARTNER).build(1));

        when(projectServiceMock.getProjectTeamStatus(123L, Optional.of(user.getId()))).thenThrow(new ForbiddenActionException());
        assertFalse(rules.partnerCanAccessProjectDetailsSection(123L, user));

        verify(projectServiceMock).getProjectUsersForProject(123L);
        verify(projectServiceMock).getProjectTeamStatus(123L, Optional.of(user.getId()));
    }

    @Test
    public void testPartnerCanAccessProjectDetailsSectionAndIsLeadPartner() {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(leadBuilder.build()).build();

        assertLeadPartnerSuccessfulAccess(teamStatus, () -> rules.partnerCanAccessProjectDetailsSection(123L, user));
    }

    private void assertLeadPartnerSuccessfulAccess(ProjectTeamStatusResource teamStatus, Supplier<Boolean> ruleCheck) {

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(user.getId()).
                withOrganisation(456L).
                withRoleName(UserRoleType.PARTNER).
                build(1);

        when(projectServiceMock.getProjectUsersForProject(123L)).thenReturn(projectUsers);

        when(projectServiceMock.getProjectTeamStatus(123L, Optional.of(user.getId()))).thenReturn(teamStatus);

        assertTrue(ruleCheck.get());

        verify(projectServiceMock).getProjectUsersForProject(123L);
        verify(projectServiceMock).getProjectTeamStatus(123L, Optional.of(user.getId()));
    }

    private void assertNonLeadPartnerSuccessfulAccess(ProjectTeamStatusResource teamStatus, Supplier<Boolean> ruleCheck) {

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(user.getId()).
                withOrganisation(789L).
                withRoleName(UserRoleType.PARTNER).
                build(1);

        when(projectServiceMock.getProjectUsersForProject(123L)).thenReturn(projectUsers);

        when(projectServiceMock.getProjectTeamStatus(123L, Optional.of(user.getId()))).thenReturn(teamStatus);

        assertTrue(ruleCheck.get());

        verify(projectServiceMock).getProjectUsersForProject(123L);
        verify(projectServiceMock).getProjectTeamStatus(123L, Optional.of(user.getId()));
    }

    @Override
    protected ProjectSetupSectionsPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectSetupSectionsPermissionRules();
    }
}
