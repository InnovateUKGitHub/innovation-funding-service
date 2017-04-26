package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.sections.ProjectSetupSectionAccessibilityHelper;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CANNOT_GET_ANY_USERS_FOR_PROJECT;
import static org.innovateuk.ifs.project.builder.ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.project.sections.SectionAccess.NOT_ACCESSIBLE;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.user.resource.UserRoleType.FINANCE_CONTACT;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ProjectSetupSectionsPermissionRulesTest extends BasePermissionRulesTest<ProjectSetupSectionsPermissionRules> {

    @Mock
    private ProjectSetupSectionsPermissionRules.ProjectSetupSectionPartnerAccessorSupplier accessorSupplier;

    @Mock
    private ProjectSetupSectionAccessibilityHelper accessor;

    private UserResource user = newUserResource().build();

    @Before
    public void setupAccessorLookup() {
        when(accessorSupplier.apply(isA(ProjectTeamStatusResource.class))).thenReturn(accessor);
    }

    @Test(expected = ForbiddenActionException.class)
    public void testCompaniesHouseSectionAccess() {
        assertScenariousForSections(ProjectSetupSectionAccessibilityHelper::canAccessCompaniesHouseSection, () -> rules.partnerCanAccessCompaniesHouseSection(123L, user));
    }

    @Test(expected = ForbiddenActionException.class)
    public void testProjectDetailsSectionAccess() {
        assertScenariousForSections(ProjectSetupSectionAccessibilityHelper::canAccessProjectDetailsSection, () -> rules.partnerCanAccessProjectDetailsSection(123L, user));
    }

    @Test
    public void testMonitoringOfficerSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(ProjectSetupSectionAccessibilityHelper::canAccessMonitoringOfficerSection, () -> rules.partnerCanAccessMonitoringOfficerSection(123L, user));
    }

    @Test
    public void testBankDetailsSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(ProjectSetupSectionAccessibilityHelper::canAccessBankDetailsSection, () -> rules.partnerCanAccessBankDetailsSection(123L, user));
    }

    @Test
    public void testSpendProfileSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(ProjectSetupSectionAccessibilityHelper::canAccessSpendProfileSection, () -> rules.partnerCanAccessSpendProfileSection(123L, user));
    }

    @Test
    public void testOtherDocumentsSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(ProjectSetupSectionAccessibilityHelper::canAccessOtherDocumentsSection, () -> rules.partnerCanAccessOtherDocumentsSection(123L, user));
    }

    @Test
    public void testGrantOfferLetterSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(ProjectSetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection, () -> rules.partnerCanAccessGrantOfferLetterSection(123L, user));
    }

    @Test
    public void testSignedGrantOfferLetterSuccessfulAccessByLead() {
        UserResource userRes = new UserResource();
        userRes.setId(1L);
        when(projectServiceMock.isUserLeadPartner(123L, 1L)).thenReturn(true);
        assertTrue(rules.leadPartnerAccess(123L, userRes));
    }

    @Test
    public void testSignedGrantOfferLetterUnSuccessfulAccessByNonLead() {
        UserResource userRes = new UserResource();
        userRes.setId(1L);
        when(projectServiceMock.isUserLeadPartner(123L, 1L)).thenReturn(false);
        assertFalse(rules.leadPartnerAccess(123L, userRes));
    }

    @Test
    public void testMarkSpendProfileIncompleteAccess() {
        ProjectUserResource leadPartnerProjectUserResource = newProjectUserResource().withUser(user.getId()).build();

        when(projectServiceMock.getLeadPartners(123L)).thenReturn(singletonList(leadPartnerProjectUserResource));
        assertTrue(rules.userCanMarkSpendProfileIncomplete(123L, user));
        verify(projectServiceMock).getLeadPartners(123L);
    }

    @Test
    public void testPartnerAccess() {
        long projectId = 123L;
        long organisationId = 234L;

        UserResource user = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PARTNER).build())).build();

        BaseIntegrationTest.setLoggedInUser(user);

        OrganisationResource o = newOrganisationResource().withId(organisationId).build();

        ProjectPartnerStatusResource partnerStatus = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).withOrganisationType(OrganisationTypeEnum.valueOf(BUSINESS.toString())).build();
        List<ProjectUserResource> pu = newProjectUserResource().withProject(projectId).withOrganisation(o.getId()).withUser(user.getId()).build(1);
        pu.get(0).setRoleName(PARTNER.getName());

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().withPartnerStatuses(Collections.singletonList(partnerStatus)).build();

        when(organisationServiceMock.getOrganisationIdFromUser(projectId, user)).thenReturn(organisationId);
        when(projectServiceMock.getProjectTeamStatus(projectId, Optional.of(user.getId()))).thenReturn(teamStatus);
        when(accessor.canAccessFinanceChecksSection(any())).thenReturn(ACCESSIBLE);

        assertTrue(rules.partnerCanAccessFinanceChecksSection(123L, user));

        verify(accessor).canAccessFinanceChecksSection(any());
    }

    @Test
    public void testPartnerNoAccess() {
        long projectId = 123L;
        long organisationId = 234L;

        UserResource user = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(PARTNER).build())).build();

        BaseIntegrationTest.setLoggedInUser(user);

        OrganisationResource o = newOrganisationResource().withId(organisationId).build();

        ProjectPartnerStatusResource partnerStatus = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).withOrganisationType(OrganisationTypeEnum.valueOf(BUSINESS.toString())).build();
        List<ProjectUserResource> pu = newProjectUserResource().withProject(projectId).withOrganisation(o.getId()).withUser(user.getId()).build(1);
        pu.get(0).setRoleName(PARTNER.getName());

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().withPartnerStatuses(Collections.singletonList(partnerStatus)).build();
        when(projectServiceMock.getProjectUsersForProject(projectId)).thenReturn(pu);
        when(projectServiceMock.getProjectTeamStatus(projectId, Optional.of(user.getId()))).thenReturn(teamStatus);
        when(accessor.canAccessFinanceChecksSection(any())).thenReturn(NOT_ACCESSIBLE);
        when(organisationServiceMock.getOrganisationIdFromUser(projectId, user)).thenReturn(organisationId);

        assertFalse(rules.partnerCanAccessFinanceChecksSection(123L, user));

    }

    @Test
    public void testFinanceContactAccess() {
        long projectId = 123L;
        long organisationId = 234L;

        UserResource user = newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(FINANCE_CONTACT).build())).build();

        BaseIntegrationTest.setLoggedInUser(user);

        OrganisationResource o = newOrganisationResource().withId(organisationId).build();

        ProjectPartnerStatusResource partnerStatus = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).withOrganisationType(OrganisationTypeEnum.valueOf(BUSINESS.toString())).build();
        List<ProjectUserResource> pu = newProjectUserResource().withProject(projectId).withOrganisation(o.getId()).withUser(user.getId()).build(2);
        pu.get(0).setRoleName(PARTNER.getName());
        pu.get(1).setRoleName(FINANCE_CONTACT.getName());

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().withPartnerStatuses(Collections.singletonList(partnerStatus)).build();

        when(organisationServiceMock.getOrganisationIdFromUser(projectId, user)).thenReturn(organisationId);
        when(projectServiceMock.getProjectTeamStatus(projectId, Optional.of(user.getId()))).thenReturn(teamStatus);
        when(accessor.canAccessFinanceChecksSection(any())).thenReturn(ACCESSIBLE);

        assertTrue(rules.partnerCanAccessFinanceChecksSection(123L, user));

        verify(projectServiceMock).getProjectTeamStatus(projectId, Optional.of(user.getId()));
    }

    private void assertLeadPartnerSuccessfulAccess(BiFunction<ProjectSetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> accessorCheck,
                                                   Supplier<Boolean> ruleCheck) {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withOrganisationId(456L).
                        withOrganisationType(BUSINESS).
                        build()).
                build();

        when(organisationServiceMock.getOrganisationIdFromUser(123L, user)).thenReturn(456L);

        when(projectServiceMock.getProjectTeamStatus(123L, Optional.of(user.getId()))).thenReturn(teamStatus);

        OrganisationResource expectedOrganisation = new OrganisationResource();
        expectedOrganisation.setId(456L);
        expectedOrganisation.setOrganisationType(
                teamStatus.getPartnerStatusForOrganisation(456L).get().getOrganisationType().getId());

        when(accessorCheck.apply(accessor, expectedOrganisation)).thenReturn(ACCESSIBLE);

        assertTrue(ruleCheck.get());

        verify(projectServiceMock).getProjectTeamStatus(123L, Optional.of(user.getId()));
    }

    private void assertNonLeadPartnerSuccessfulAccess(BiFunction<ProjectSetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> accessorCheck,
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
                withRoleName(PARTNER).
                build(1);

        when(projectServiceMock.getProjectUsersForProject(123L)).thenReturn(projectUsers);

        when(projectServiceMock.getProjectTeamStatus(123L, Optional.of(user.getId()))).thenReturn(teamStatus);

        when(organisationServiceMock.getOrganisationIdFromUser(123L, user)).thenReturn(789L);

        OrganisationResource expectedOrganisation = new OrganisationResource();
        expectedOrganisation.setId(789L);
        expectedOrganisation.setOrganisationType(
                teamStatus.getPartnerStatusForOrganisation(789L).get().getOrganisationType().getId());

        when(accessorCheck.apply(accessor, expectedOrganisation)).thenReturn(ACCESSIBLE);

        assertTrue(ruleCheck.get());

        verify(projectServiceMock).getProjectTeamStatus(123L, Optional.of(user.getId()));
        accessorCheck.apply(verify(accessor), expectedOrganisation);
    }

    private void assertNotOnProjectExpectations(Supplier<Boolean> ruleCheck) {
        when(organisationServiceMock.getOrganisationIdFromUser(123L, user)).thenThrow(new ForbiddenActionException(CANNOT_GET_ANY_USERS_FOR_PROJECT.getErrorKey(), singletonList(123L)));

        assertFalse(ruleCheck.get());

        verify(projectServiceMock, never()).getProjectTeamStatus(123L, Optional.of(user.getId()));
    }

    private void assertForbiddenExpectations(Supplier<Boolean> ruleCheck) {
        when(organisationServiceMock.getOrganisationIdFromUser(123L, user)).thenThrow(new ForbiddenActionException(CANNOT_GET_ANY_USERS_FOR_PROJECT.getErrorKey(), singletonList(123L)));

        assertFalse(ruleCheck.get());

        verifyZeroInteractions(projectServiceMock);
    }

    private void assertScenariousForSections(BiFunction<ProjectSetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> accessorCheck, Supplier<Boolean> ruleCheck) {
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
