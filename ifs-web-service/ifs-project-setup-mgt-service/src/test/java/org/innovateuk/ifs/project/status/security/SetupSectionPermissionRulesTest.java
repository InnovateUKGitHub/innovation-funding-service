package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.TriFunction;
import org.junit.Test;
import org.mockito.Mock;

import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.project.builder.ProjectStatusResourceBuilder.newProjectStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.resource.ProjectState.LIVE;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


public class SetupSectionPermissionRulesTest extends BasePermissionRulesTest<SetupSectionsPermissionRules> {

    @Mock
    private ProjectService projectService;

    @Mock
    private StatusService statusService;

    @Override
    protected SetupSectionsPermissionRules supplyPermissionRulesUnderTest() {
        return new SetupSectionsPermissionRules();
    }

    @Test
    public void internalCanAccessFinanceChecksAddQuery() {
        Role role = Role.PROJECT_FINANCE;
        UserResource user = newUserResource().withRoleGlobal(role).build();
        ProjectUserResource projectUser = newProjectUserResource().withRole(ProjectParticipantRole.PROJECT_FINANCE_CONTACT).withOrganisation(2L).build();
        ProjectStatusResource projectStatus = newProjectStatusResource().withProjectState(LIVE).withBankDetailsStatus(ProjectActivityStates.COMPLETE).withProjectDetailStatus(ProjectActivityStates.COMPLETE).withFinanceChecksStatus(ProjectActivityStates.COMPLETE).build();
        when(projectService.getProjectUsersForProject(1L)).thenReturn(singletonList(projectUser));
        when(statusService.getProjectStatus(1L)).thenReturn(projectStatus);
        assertTrue(rules.internalCanAccessFinanceChecksAddQuery(new ProjectOrganisationCompositeId(1L, 2L), user));
    }

    @Test
    public void internalCanAccessFinanceChecksAddQueryNotFinanceTeam() {
        Role role = Role.COMP_ADMIN;
        UserResource user = newUserResource().withRoleGlobal(role).build();
        ProjectUserResource projectUser = newProjectUserResource().withRole(ProjectParticipantRole.PROJECT_FINANCE_CONTACT).withOrganisation(2L).build();
        ProjectStatusResource projectStatus = newProjectStatusResource().withProjectState(LIVE).withBankDetailsStatus(ProjectActivityStates.COMPLETE).withProjectDetailStatus(ProjectActivityStates.COMPLETE).withFinanceChecksStatus(ProjectActivityStates.COMPLETE).build();
        when(projectService.getProjectUsersForProject(1L)).thenReturn(singletonList(projectUser));
        when(statusService.getProjectStatus(1L)).thenReturn(projectStatus);
        assertFalse(rules.internalCanAccessFinanceChecksAddQuery(new ProjectOrganisationCompositeId(1L, 2L), user));
    }

    @Test
    public void internalCanAccessFinanceChecksAddQueryNotInternal() {
        Role role = Role.APPLICANT;
        UserResource user = newUserResource().withRoleGlobal(role).build();
        ProjectUserResource projectUser = newProjectUserResource().withRole(ProjectParticipantRole.PROJECT_FINANCE_CONTACT).withOrganisation(2L).build();
        ProjectStatusResource projectStatus = newProjectStatusResource().withBankDetailsStatus(ProjectActivityStates.COMPLETE).withProjectDetailStatus(ProjectActivityStates.COMPLETE).withFinanceChecksStatus(ProjectActivityStates.COMPLETE).build();
        when(projectService.getProjectUsersForProject(1L)).thenReturn(singletonList(projectUser));
        when(statusService.getProjectStatus(1L)).thenReturn(projectStatus);
        assertFalse(rules.internalCanAccessFinanceChecksAddQuery(new ProjectOrganisationCompositeId(1L, 2L), user));
    }

    @Test
    public void internalCanAccessFinanceChecksAddQueryNoFinanceContact() {
        Role role = Role.COMP_ADMIN;
        UserResource user = newUserResource().withRoleGlobal(role).build();
        ProjectUserResource projectUser = newProjectUserResource().withRole(ProjectParticipantRole.PROJECT_PARTNER).withOrganisation(2L).build();
        when(projectService.getProjectUsersForProject(1L)).thenReturn(singletonList(projectUser));
        assertFalse(rules.internalCanAccessFinanceChecksAddQuery(new ProjectOrganisationCompositeId(1L, 2L), user));
    }

    @Test
    public void internalCanAccessFinanceChecksAddQueryNotInOrganisation() {
        Role role = Role.COMP_ADMIN;
        UserResource user = newUserResource().withRoleGlobal(role).build();
        ProjectUserResource projectUser = newProjectUserResource().withRole(ProjectParticipantRole.PROJECT_FINANCE_CONTACT).withOrganisation(3L).build();
        when(projectService.getProjectUsersForProject(1L)).thenReturn(singletonList(projectUser));
        assertFalse(rules.internalCanAccessFinanceChecksAddQuery(new ProjectOrganisationCompositeId(1L, 2L), user));
    }

    @Test
    public void internalAdminUserCanAccessDocumentsSectionIrrespectiveOfDocumentStatus() {
        assertTrue(doTestDocumentAccess(Role.COMP_ADMIN, ProjectActivityStates.COMPLETE,
                SetupSectionsPermissionRules::internalAdminUserCanAccessDocumentsSection));
        assertTrue(doTestDocumentAccess(Role.COMP_ADMIN, ProjectActivityStates.NOT_STARTED,
                SetupSectionsPermissionRules::internalAdminUserCanAccessDocumentsSection));

    }

    @Test
    public void supportUserCanAccessDocumentsSectionOnlyWhenAllDocumentsAreApproved() {
        assertFalse(doTestDocumentAccess(Role.SUPPORT, ProjectActivityStates.ACTION_REQUIRED,
                SetupSectionsPermissionRules::supportUserCanAccessDocumentsSection));
        assertTrue(doTestDocumentAccess(Role.SUPPORT, ProjectActivityStates.COMPLETE,
                SetupSectionsPermissionRules::supportUserCanAccessDocumentsSection));
    }

    @Test
    public void innovationLeadCanAccessDocumentsSectionOnlyWhenAllDocumentsAreApproved() {
        assertFalse(doTestDocumentAccess(Role.INNOVATION_LEAD, ProjectActivityStates.ACTION_REQUIRED,
                SetupSectionsPermissionRules::innovationLeadCanAccessDocumentsSection));
        assertTrue(doTestDocumentAccess(Role.INNOVATION_LEAD, ProjectActivityStates.COMPLETE,
                SetupSectionsPermissionRules::innovationLeadCanAccessDocumentsSection));
    }

    @Test
    public void stakeholderCanAccessDocumentsSectionOnlyWhenAllDocumentsAreApproved() {
        assertFalse(doTestDocumentAccess(Role.STAKEHOLDER, ProjectActivityStates.ACTION_REQUIRED,
                SetupSectionsPermissionRules::stakeholderCanAccessDocumentsSection));
        assertTrue(doTestDocumentAccess(Role.STAKEHOLDER, ProjectActivityStates.COMPLETE,
                SetupSectionsPermissionRules::stakeholderCanAccessDocumentsSection));
    }

    private boolean doTestDocumentAccess(Role role, ProjectActivityStates documentStatus,
                                         TriFunction<SetupSectionsPermissionRules, ProjectCompositeId, UserResource, Boolean> rule) {
        Long projectId = 1L;

        UserResource user = newUserResource().withRoleGlobal(role).build();
        ProjectStatusResource projectStatus = newProjectStatusResource()
                .withDocumentsStatus(documentStatus)
                .withProjectState(LIVE)
                .build();
        when(statusService.getProjectStatus(projectId)).thenReturn(projectStatus);
        return rule.apply(rules, ProjectCompositeId.id(projectId), user);
    }

    @Test
    public void onlyInternalAdminUserCanApproveDocuments() {

        assertTrue(stream(Role.values()).filter(role -> asList(Role.COMP_ADMIN, Role.PROJECT_FINANCE, Role.IFS_ADMINISTRATOR, Role.SUPER_ADMIN_USER, Role.SYSTEM_MAINTAINER).contains(role))
                .map(this::doTestApproveDocumentsAccess)
                .filter(Boolean.FALSE::equals)
                .collect(Collectors.toList())
                .isEmpty());

        assertTrue(stream(Role.values()).filter(role -> !(asList(Role.COMP_ADMIN, Role.PROJECT_FINANCE, Role.IFS_ADMINISTRATOR, Role.SUPER_ADMIN_USER, Role.SYSTEM_MAINTAINER).contains(role)))
                .map(this::doTestApproveDocumentsAccess)
                .filter(Boolean.TRUE::equals)
                .collect(Collectors.toList())
                .isEmpty());
    }

    private boolean doTestApproveDocumentsAccess(Role role) {

        UserResource user = newUserResource()
                .withRoleGlobal(role)
                .build();

        return rules.internalAdminUserCanApproveDocuments(ProjectCompositeId.id(1L), user);
    }

    @Test
    public void onlySuperAdminUserCanResetGrantOfferLetter() {
        allGlobalRoleUsers.forEach(userResource -> {
            if (userResource.hasRole(Role.SUPER_ADMIN_USER)) {
                assertTrue(rules.superAdminUserCanResetGrantOfferLetter(ProjectCompositeId.id(1L), userResource));
            } else {
                assertFalse(rules.superAdminUserCanResetGrantOfferLetter(ProjectCompositeId.id(1L), userResource));
            }
        });
    }
}