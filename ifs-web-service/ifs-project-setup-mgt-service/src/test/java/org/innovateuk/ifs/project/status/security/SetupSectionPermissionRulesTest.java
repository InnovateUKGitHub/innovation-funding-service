package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.project.builder.ProjectStatusResourceBuilder.newProjectStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


public class SetupSectionPermissionRulesTest extends BasePermissionRulesTest<SetupSectionsPermissionRules> {
    @Override
    protected SetupSectionsPermissionRules supplyPermissionRulesUnderTest() {
        return new SetupSectionsPermissionRules();
    }

    @Test
    public void internalCanAccessFinanceChecksAddQuery() {
        Role role = Role.PROJECT_FINANCE;
        UserResource user = newUserResource().withRolesGlobal(singletonList(role)).build();
        ProjectUserResource projectUser = newProjectUserResource().withRole(Role.FINANCE_CONTACT).withOrganisation(2L).build();
        ProjectStatusResource projectStatus = newProjectStatusResource().withBankDetailsStatus(ProjectActivityStates.COMPLETE).withProjectDetailStatus(ProjectActivityStates.COMPLETE).withFinanceChecksStatus(ProjectActivityStates.COMPLETE).build();
        when(projectServiceMock.getProjectUsersForProject(1L)).thenReturn(singletonList(projectUser));
        when(statusServiceMock.getProjectStatus(1L)).thenReturn(projectStatus);
        assertTrue(rules.internalCanAccessFinanceChecksAddQuery(new ProjectOrganisationCompositeId(1L, 2L), user));
    }

    @Test
    public void internalCanAccessFinanceChecksAddQueryNotFinanceTeam() {
        Role role = Role.COMP_ADMIN;
        UserResource user = newUserResource().withRolesGlobal(singletonList(role)).build();
        ProjectUserResource projectUser = newProjectUserResource().withRole(Role.FINANCE_CONTACT).withOrganisation(2L).build();
        ProjectStatusResource projectStatus = newProjectStatusResource().withBankDetailsStatus(ProjectActivityStates.COMPLETE).withProjectDetailStatus(ProjectActivityStates.COMPLETE).withFinanceChecksStatus(ProjectActivityStates.COMPLETE).build();
        when(projectServiceMock.getProjectUsersForProject(1L)).thenReturn(singletonList(projectUser));
        when(statusServiceMock.getProjectStatus(1L)).thenReturn(projectStatus);
        assertFalse(rules.internalCanAccessFinanceChecksAddQuery(new ProjectOrganisationCompositeId(1L, 2L), user));
    }

    @Test
    public void internalCanAccessFinanceChecksAddQueryNotInternal() {
        Role role = Role.LEADAPPLICANT;
        UserResource user = newUserResource().withRolesGlobal(singletonList(role)).build();
        ProjectUserResource projectUser = newProjectUserResource().withRole(Role.FINANCE_CONTACT).withOrganisation(2L).build();
        ProjectStatusResource projectStatus = newProjectStatusResource().withBankDetailsStatus(ProjectActivityStates.COMPLETE).withProjectDetailStatus(ProjectActivityStates.COMPLETE).withFinanceChecksStatus(ProjectActivityStates.COMPLETE).build();
        when(projectServiceMock.getProjectUsersForProject(1L)).thenReturn(singletonList(projectUser));
        when(statusServiceMock.getProjectStatus(1L)).thenReturn(projectStatus);
        assertFalse(rules.internalCanAccessFinanceChecksAddQuery(new ProjectOrganisationCompositeId(1L, 2L), user));
    }

    @Test
    public void internalCanAccessFinanceChecksAddQueryNoFinanceContact() {
        Role role = Role.COMP_ADMIN;
        UserResource user = newUserResource().withRolesGlobal(singletonList(role)).build();
        ProjectUserResource projectUser = newProjectUserResource().withRole(Role.PARTNER).withOrganisation(2L).build();
        when(projectServiceMock.getProjectUsersForProject(1L)).thenReturn(singletonList(projectUser));
        assertFalse(rules.internalCanAccessFinanceChecksAddQuery(new ProjectOrganisationCompositeId(1L, 2L), user));
    }

    @Test
    public void internalCanAccessFinanceChecksAddQueryNotInOrganisation() {
        Role role = Role.COMP_ADMIN;
        UserResource user = newUserResource().withRolesGlobal(singletonList(role)).build();
        ProjectUserResource projectUser = newProjectUserResource().withRole(Role.FINANCE_CONTACT).withOrganisation(3L).build();
        when(projectServiceMock.getProjectUsersForProject(1L)).thenReturn(singletonList(projectUser));
        assertFalse(rules.internalCanAccessFinanceChecksAddQuery(new ProjectOrganisationCompositeId(1L, 2L), user));
    }
}
