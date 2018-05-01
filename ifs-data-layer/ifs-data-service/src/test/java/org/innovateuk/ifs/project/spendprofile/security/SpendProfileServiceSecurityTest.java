package org.innovateuk.ifs.project.spendprofile.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectCompositeId;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.core.security.ProjectLookupStrategy;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileTableResource;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileService;
import org.innovateuk.ifs.project.spendprofile.transactional.SpendProfileServiceImpl;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.fail;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.mockito.Mockito.*;

public class SpendProfileServiceSecurityTest extends BaseServiceSecurityTest<SpendProfileService> {

    private SpendProfilePermissionRules spendProfilePermissionRules;

    private ProjectLookupStrategy projectLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        spendProfilePermissionRules = getMockPermissionRulesBean(SpendProfilePermissionRules.class);
        projectLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ProjectLookupStrategy.class);
    }

    @Test
    public void testGenerateSpendProfile() {

        asList(Role.values()).forEach(role -> {
            Role roleResource = Role.getByName(role.getName());
            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(roleResource)).build();
            setLoggedInUser(userWithRole);

            if (role == PROJECT_FINANCE || role == COMP_ADMIN) {
                classUnderTest.generateSpendProfile(123L);
            } else {
                try {
                    classUnderTest.generateSpendProfile(123L);
                    fail();
                } catch (AccessDeniedException e) {
                    // expected behaviour
                }
            }
        });
    }

    @Test
    public void testGenerateSpendProfileForPartnerOrganisation() {

        asList(Role.values()).forEach(role -> {
            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(role)).build();
            setLoggedInUser(userWithRole);

            if (PROJECT_FINANCE.equals(role) || COMP_ADMIN.equals(role)) {
                classUnderTest.generateSpendProfileForPartnerOrganisation(1L, 2L, 7L);
            } else {
                try {
                    classUnderTest.generateSpendProfileForPartnerOrganisation(1L, 2L, 7L);
                    fail("Should have thrown an AccessDeniedException for any non-Finance Team members");
                } catch (AccessDeniedException e) {
                    // expected behaviour
                }
            }
        });
    }

    @Test
    public void testGetSpendProfileTable() {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.getSpendProfileTable(projectOrganisationCompositeId),
                () -> {

                    verify(spendProfilePermissionRules).partnersCanViewTheirOwnSpendProfileData(projectOrganisationCompositeId, getLoggedInUser());
                    verify(spendProfilePermissionRules).projectFinanceUserCanViewAnySpendProfileData(projectOrganisationCompositeId, getLoggedInUser());
                    verify(spendProfilePermissionRules).leadPartnerCanViewAnySpendProfileData(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(spendProfilePermissionRules);
                });
    }

    @Test
    public void testGetSpendProfileCSV() {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.getSpendProfileCSV(projectOrganisationCompositeId),
                () -> {
                    verify(spendProfilePermissionRules).partnersCanViewTheirOwnSpendProfileCsv(projectOrganisationCompositeId, getLoggedInUser());
                    verify(spendProfilePermissionRules).internalAdminUsersCanSeeSpendProfileCsv(projectOrganisationCompositeId, getLoggedInUser());
                    verify(spendProfilePermissionRules).supportUsersCanSeeSpendProfileCsv(projectOrganisationCompositeId, getLoggedInUser());
                    verify(spendProfilePermissionRules).leadPartnerCanViewAnySpendProfileCsv(projectOrganisationCompositeId, getLoggedInUser());
                    verify(spendProfilePermissionRules).innovationLeadUsersCanSeeSpendProfileCsv(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(spendProfilePermissionRules);
                });
    }

    @Test
    public void testGetSpendProfile() {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.getSpendProfile(projectOrganisationCompositeId),
                () -> {

                    verify(spendProfilePermissionRules).partnersCanViewTheirOwnSpendProfileData(projectOrganisationCompositeId, getLoggedInUser());
                    verify(spendProfilePermissionRules).projectFinanceUserCanViewAnySpendProfileData(projectOrganisationCompositeId, getLoggedInUser());
                    verify(spendProfilePermissionRules).leadPartnerCanViewAnySpendProfileData(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(spendProfilePermissionRules);
                });
    }

    @Test
    public void testSaveSpendProfile() {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        SpendProfileTableResource table = new SpendProfileTableResource();

        assertAccessDenied(() -> classUnderTest.saveSpendProfile(projectOrganisationCompositeId, table),
                () -> {

                    verify(spendProfilePermissionRules).partnersCanEditTheirOwnSpendProfileData(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(spendProfilePermissionRules);
                });
    }

    @Test
    public void testApproveOrRejectSpendProfile() {

        List<Role> nonCompAdminRoles = getNonProjectFinanceUserRoles();
        nonCompAdminRoles.forEach(role -> {
            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.approveOrRejectSpendProfile(1L, ApprovalType.APPROVED);
                Assert.fail("Should not have been able to create project from application without the global Comp Admin role");
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void testGetSpendProfileStatusByProjectId() {

        List<Role> nonCompAdminRoles = getNonInternalAdminOrSupportUserRoles();
        nonCompAdminRoles.forEach(role -> {
            setLoggedInUser(
                    newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                classUnderTest.getSpendProfileStatusByProjectId(1L);
                Assert.fail("Should not have been able to obtain status for spend profile with role " + role.getName());
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
    }

    @Test
    public void verifyGetSpendProfileStatusByProjectIdRules() {
        final Long projectId = 1L;
        ProjectResource projectResource = newProjectResource().withId(projectId).build();
        when(projectLookupStrategy.getProjectResource(projectId)).thenReturn(projectResource);
        assertAccessDenied(() -> classUnderTest.getSpendProfileStatusByProjectId(projectId), () -> {
            verify(spendProfilePermissionRules).internalAdminTeamCanViewCompetitionStatus(projectResource, getLoggedInUser());
            verify(spendProfilePermissionRules).supportCanViewCompetitionStatus(projectResource, getLoggedInUser());
            verify(spendProfilePermissionRules).assignedInnovationLeadCanViewSPStatus(projectResource, getLoggedInUser());
            verifyNoMoreInteractions(spendProfilePermissionRules);
        });
    }

    @Test
    public void testMarkSpendProfileComplete() {

        Long projectId = 1L;
        Long organisationId = 1L;

        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(projectId, organisationId);

        assertAccessDenied(() -> classUnderTest.markSpendProfileComplete(projectOrganisationCompositeId),
                () -> {

                    verify(spendProfilePermissionRules).partnersCanMarkSpendProfileAsComplete(projectOrganisationCompositeId, getLoggedInUser());
                    verifyNoMoreInteractions(spendProfilePermissionRules);
                });
    }

    @Test
    public void testCompleteSpendProfilesReview() {
        ProjectCompositeId projectId = ProjectCompositeId.id(1L);
        when(projectLookupStrategy.getProjectCompositeId(projectId.id())).thenReturn(projectId);
        assertAccessDenied(() -> classUnderTest.completeSpendProfilesReview(projectId.id()),
                () -> {
                    verify(spendProfilePermissionRules).projectManagerCanCompleteSpendProfile(projectId, getLoggedInUser());
                    verifyNoMoreInteractions(spendProfilePermissionRules);
                });
    }

    @Override
    protected Class<? extends SpendProfileService> getClassUnderTest() {
        return SpendProfileServiceImpl.class;
    }

    private List<Role> getNonInternalAdminOrSupportUserRoles() {
        return Arrays.stream(Role.values()).filter(type -> type != PROJECT_FINANCE && type != COMP_ADMIN && type != SUPPORT)
                .collect(toList());
    }

    private List<Role> getNonProjectFinanceUserRoles() {
        return Arrays.stream(Role.values()).filter(type -> type != PROJECT_FINANCE && type != COMP_ADMIN)
                .collect(toList());
    }
}

