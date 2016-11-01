package com.worth.ifs.project;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.sections.ProjectSetupSectionPartnerAccessor;
import com.worth.ifs.project.sections.SectionAccess;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.worth.ifs.project.builder.ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource;
import static com.worth.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static com.worth.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.project.sections.SectionAccess.ACCESSIBLE;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.OrganisationTypeEnum.BUSINESS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ProjectSetupSectionsPermissionRulesTest extends BasePermissionRulesTest<ProjectSetupSectionsPermissionRules> {

    @Mock
    private ProjectSetupSectionsPermissionRules.ProjectSetupSectionPartnerAccessorSupplier accessorSupplier;

    @Mock
    private ProjectSetupSectionPartnerAccessor accessor;

    private UserResource user = newUserResource().build();

    @Before
    public void setupAccessorLookup() {
        when(accessorSupplier.apply(isA(ProjectTeamStatusResource.class))).thenReturn(accessor);
    }

    @Test
    public void testCompaniesHouseSectionAccess() {
        assertScenariousForSections(ProjectSetupSectionPartnerAccessor::canAccessCompaniesHouseSection, () -> rules.partnerCanAccessCompaniesHouseSection(123L, user));
    }

    @Test
    public void testProjectDetailsSectionAccess() {
        assertScenariousForSections(ProjectSetupSectionPartnerAccessor::canAccessProjectDetailsSection, () -> rules.partnerCanAccessProjectDetailsSection(123L, user));
    }

    @Test
    public void testMonitoringOfficerSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(ProjectSetupSectionPartnerAccessor::canAccessMonitoringOfficerSection, () -> rules.partnerCanAccessMonitoringOfficerSection(123L, user));
    }

    @Test
    public void testBankDetailsSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(ProjectSetupSectionPartnerAccessor::canAccessBankDetailsSection, () -> rules.partnerCanAccessBankDetailsSection(123L, user));
    }

    @Test
    public void testFinanceChecksSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(ProjectSetupSectionPartnerAccessor::canAccessFinanceChecksSection, () -> rules.partnerCanAccessFinanceChecksSection(123L, user));
    }

    @Test
    public void testSpendProfileSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(ProjectSetupSectionPartnerAccessor::canAccessSpendProfileSection, () -> rules.partnerCanAccessSpendProfileSection(123L, user));
    }

    @Test
    public void testOtherDocumentsSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(ProjectSetupSectionPartnerAccessor::canAccessOtherDocumentsSection, () -> rules.partnerCanAccessOtherDocumentsSection(123L, user));
    }

    @Test
    public void testGrantOfferLetterSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(ProjectSetupSectionPartnerAccessor::canAccessGrantOfferLetterSection, () -> rules.partnerCanAccessGrantOfferLetterSection(123L, user));
    }

    private void assertLeadPartnerSuccessfulAccess(BiFunction<ProjectSetupSectionPartnerAccessor, OrganisationResource, SectionAccess> accessorCheck,
                                                   Supplier<Boolean> ruleCheck) {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withOrganisationId(456L).
                        withOrganisationType(BUSINESS).
                        build()).
                build();

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(user.getId()).
                withOrganisation(456L).
                withRoleName(UserRoleType.PARTNER).
                build(1);

        when(projectServiceMock.getProjectUsersForProject(123L)).thenReturn(projectUsers);

        when(projectServiceMock.getProjectTeamStatus(123L, Optional.of(user.getId()))).thenReturn(teamStatus);

        OrganisationResource expectedOrganisation = new OrganisationResource();
        expectedOrganisation.setId(456L);
        expectedOrganisation.setOrganisationType(
                teamStatus.getPartnerStatusForOrganisation(456L).get().getOrganisationType().getOrganisationTypeId());

        when(accessorCheck.apply(accessor, expectedOrganisation)).thenReturn(ACCESSIBLE);

        assertTrue(ruleCheck.get());

        verify(projectServiceMock).getProjectUsersForProject(123L);
        verify(projectServiceMock).getProjectTeamStatus(123L, Optional.of(user.getId()));
    }

    private void assertNonLeadPartnerSuccessfulAccess(BiFunction<ProjectSetupSectionPartnerAccessor, OrganisationResource, SectionAccess> accessorCheck,
                                                      Supplier<Boolean> ruleCheck) {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withOrganisationId(456L).
                        withOrganisationType(BUSINESS).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withOrganisationId(789L).
                        withOrganisationType(BUSINESS).
                        build(1)).
                build();

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(user.getId()).
                withOrganisation(789L).
                withRoleName(UserRoleType.PARTNER).
                build(1);

        when(projectServiceMock.getProjectUsersForProject(123L)).thenReturn(projectUsers);

        when(projectServiceMock.getProjectTeamStatus(123L, Optional.of(user.getId()))).thenReturn(teamStatus);

        OrganisationResource expectedOrganisation = new OrganisationResource();
        expectedOrganisation.setId(789L);
        expectedOrganisation.setOrganisationType(
                teamStatus.getPartnerStatusForOrganisation(789L).get().getOrganisationType().getOrganisationTypeId());

        when(accessorCheck.apply(accessor, expectedOrganisation)).thenReturn(ACCESSIBLE);

        assertTrue(ruleCheck.get());

        verify(projectServiceMock).getProjectUsersForProject(123L);
        verify(projectServiceMock).getProjectTeamStatus(123L, Optional.of(user.getId()));
        accessorCheck.apply(verify(accessor), expectedOrganisation);
    }

    private void assertNotOnProjectExpectations(Supplier<Boolean> ruleCheck) {
        when(projectServiceMock.getProjectUsersForProject(123L)).thenReturn(
                newProjectUserResource().withUser(999L).withOrganisation(456L).withRoleName(UserRoleType.PARTNER).build(1));

        assertFalse(ruleCheck.get());

        verify(projectServiceMock).getProjectUsersForProject(123L);
        verify(projectServiceMock, never()).getProjectTeamStatus(123L, Optional.of(user.getId()));
    }

    private void assertForbiddenExpectations(Supplier<Boolean> ruleCheck) {

        when(projectServiceMock.getProjectUsersForProject(123L)).thenReturn(
                newProjectUserResource().withUser(user.getId()).withOrganisation(456L).withRoleName(UserRoleType.PARTNER).build(1));

        when(projectServiceMock.getProjectTeamStatus(123L, Optional.of(user.getId()))).thenThrow(new ForbiddenActionException());

        assertFalse(ruleCheck.get());

        verify(projectServiceMock).getProjectUsersForProject(123L);
        verify(projectServiceMock).getProjectTeamStatus(123L, Optional.of(user.getId()));
    }

    private void assertScenariousForSections(BiFunction<ProjectSetupSectionPartnerAccessor, OrganisationResource, SectionAccess> accessorCheck, Supplier<Boolean> ruleCheck) {
        assertLeadPartnerSuccessfulAccess(accessorCheck, ruleCheck);
        resetMocks();

        assertNonLeadPartnerSuccessfulAccess(accessorCheck, ruleCheck);
        resetMocks();

        assertNotOnProjectExpectations(ruleCheck);
        resetMocks();

        assertForbiddenExpectations(ruleCheck);
    }

    private void resetMocks() {
        reset(projectServiceMock, accessor);
    }

    @Override
    protected ProjectSetupSectionsPermissionRules supplyPermissionRulesUnderTest() {
        return new ProjectSetupSectionsPermissionRules();
    }
}
