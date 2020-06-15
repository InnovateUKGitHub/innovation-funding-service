package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserCompositeId;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.CANNOT_GET_ANY_USERS_FOR_PROJECT;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.sections.SectionAccess.NOT_ACCESSIBLE;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class SetupSectionsPermissionRulesTest extends BasePermissionRulesTest<SetupSectionsPermissionRules> {

    @Mock
    private SetupSectionsPermissionRules.SetupSectionPartnerAccessorSupplier accessorSupplier;

    @Mock
    private SetupSectionAccessibilityHelper accessor;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ProjectService projectService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private StatusService statusService;

    private UserResource user = newUserResource().build();
    private UserResource monitoringOfficer = newUserResource().withRoleGlobal(MONITORING_OFFICER).build();

    private CompetitionResource competition = newCompetitionResource().build();

    private ApplicationResource application = newApplicationResource()
            .withCompetition(competition.getId())
            .build();

    private ProjectResource activeProject = newProjectResource()
            .withProjectState(ProjectState.SETUP)
            .withApplication(application)
            .build();

    private ProjectResource withdrawnProject = newProjectResource()
            .withProjectState(ProjectState.WITHDRAWN)
            .withApplication(application)
            .build();

    @Before
    public void setupAccessorLookup() {
        when(accessorSupplier.apply(isA(ProjectTeamStatusResource.class))).thenReturn(accessor);
    }

    @Test
    public void readPostAwardServiceForProjectSetupProvideAccess() {
        ProjectCompositeId projectCompositeId = ProjectCompositeId.id(14L);
        UserCompositeId userCompositeId = UserCompositeId.id(5L);

        UserResource loggedInUser = UserResourceBuilder.newUserResource().withId(userCompositeId.id()).build();
        ProjectUserResource projectUserResource = ProjectUserResourceBuilder.newProjectUserResource().withUser(userCompositeId.id()).build();
        when(projectService.getProjectUsersForProject(projectCompositeId.id())).thenReturn(Collections.singletonList(projectUserResource));

        assertTrue(rules.readPostAwardServiceForProjectSetup(projectCompositeId, loggedInUser));
        verify(projectService).getProjectUsersForProject(projectCompositeId.id());
    }

    @Test
    public void readPostAwardServiceForProjectSetupRestrictAccess() {
        ProjectCompositeId projectCompositeId = ProjectCompositeId.id(14L);
        UserCompositeId userCompositeId = UserCompositeId.id(5L);

        UserResource loggedInUser = UserResourceBuilder.newUserResource().withId(userCompositeId.id()).build();
        when(projectService.getProjectUsersForProject(projectCompositeId.id())).thenReturn(Collections.emptyList());

        assertFalse(rules.readPostAwardServiceForProjectSetup(projectCompositeId, loggedInUser));
        verify(projectService).getProjectUsersForProject(projectCompositeId.id());
    }

    @Test
    public void projectDetailsSectionAccess() {
        assertScenariosForSections(SetupSectionAccessibilityHelper::canAccessProjectDetailsSection, () -> rules.partnerCanAccessProjectDetailsSection(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void projectManagerPageAccess() {
        assertLeadPartnerSuccessfulAccess(SetupSectionAccessibilityHelper::leadCanAccessProjectManagerPage,
                () -> rules.leadCanAccessProjectManagerPage(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService, times(2)).getById(activeProject.getId());
    }

    @Test
    public void projectAddressPageAccess() {
        assertLeadPartnerSuccessfulAccess(SetupSectionAccessibilityHelper::leadCanAccessProjectAddressPage,
                () -> rules.leadCanAccessProjectAddressPage(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService, times(2)).getById(activeProject.getId());
    }

    @Test
    public void financeContactPageAccess() {
        assertNonLeadPartnerSuccessfulAccess(SetupSectionAccessibilityHelper::canAccessFinanceContactPage,
                () -> rules.partnerCanAccessFinanceContactPage(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void partnerProjectLocationPageAccess() {
        setUpPartnerProjectLocationRequiredMocking();

        assertNonLeadPartnerSuccessfulAccess((setupSectionAccessibilityHelper, organisation) ->
                setupSectionAccessibilityHelper.canAccessPartnerProjectLocationPage(organisation, true),
                () -> rules.partnerCanAccessProjectLocationPage(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService, times(2)).getById(activeProject.getId());
    }

    @Test
    public void monitoringOfficerSectionAccess() {
        setUpPartnerProjectLocationRequiredMocking();

        assertNonLeadPartnerSuccessfulAccess((setupSectionAccessibilityHelper, organisation) ->
                setupSectionAccessibilityHelper.canAccessMonitoringOfficerSection(organisation, true),
                () -> rules.partnerCanAccessMonitoringOfficerSection(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService, times(2)).getById(activeProject.getId());
    }

    private void setUpPartnerProjectLocationRequiredMocking() {
        when(applicationService.getById(activeProject.getApplication())).thenReturn(application);
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
    }

    @Test
    public void bankDetailsSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(SetupSectionAccessibilityHelper::canAccessBankDetailsSection,
                () -> rules.partnerCanAccessBankDetailsSection(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService, times(2)).getById(activeProject.getId());
    }

    @Test
    public void bankDetailsSectionAccessMonitoringOfficer() {
        activeProject.setMonitoringOfficerUser(monitoringOfficer.getId());
        when(projectService.getById(activeProject.getId())).thenReturn(activeProject);

        assertMonitoringOfficerUnSuccessfulAccess(SetupSectionAccessibilityHelper::canAccessBankDetailsSection,
                () -> rules.partnerCanAccessBankDetailsSection(ProjectCompositeId.id(activeProject.getId()), monitoringOfficer));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void spendProfileSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(SetupSectionAccessibilityHelper::canAccessSpendProfileSection, () -> rules.partnerCanAccessSpendProfileSection(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void projectManagerTotalSpendProfileSectionAccess() {
        assertLeadPartnerSuccessfulAccess(SetupSectionAccessibilityHelper::canAccessSpendProfileSection, () -> rules.projectManagerCanAccessSpendProfileSection(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void partnerTotalSpendProfileSectionNoAccess() {
        assertNonLeadPartnerAndNotMOUnsuccessfulAccess(SetupSectionAccessibilityHelper::canAccessSpendProfileSection, () -> rules.projectManagerCanAccessSpendProfileSection(ProjectCompositeId.id(activeProject.getId()), user));
    }

    @Test
    public void spendProfileSectionAccessMonitoringOfficer() {
        assertMonitoringOfficerSuccessfulAccess(SetupSectionAccessibilityHelper::canAccessSpendProfileSection, () -> rules.partnerCanAccessSpendProfileSection(ProjectCompositeId.id(activeProject.getId()), monitoringOfficer));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void editSpendProfileSectionAccess() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(activeProject.getId(), 22L);
        assertNonLeadPartnerSuccessfulAccess((setupSectionAccessibilityHelper, organisation) -> setupSectionAccessibilityHelper.canEditSpendProfileSection(organisation, projectOrganisationCompositeId.getOrganisationId()),
                () -> rules.partnerCanEditSpendProfileSection(projectOrganisationCompositeId, user));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void documentsSectionAccessLead() {
        assertLeadPartnerSuccessfulAccess(SetupSectionAccessibilityHelper::canAccessDocumentsSection, () -> rules.canAccessDocumentsSection(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void documentsSectionAccessMonitoringOfficer() {
        assertMonitoringOfficerSuccessfulAccess(SetupSectionAccessibilityHelper::canAccessDocumentsSection, () -> rules.canAccessDocumentsSection(ProjectCompositeId.id(activeProject.getId()), monitoringOfficer));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void documentsSectionAccessNonLead() {
        assertNonLeadPartnerSuccessfulAccess(SetupSectionAccessibilityHelper::canAccessDocumentsSection, () -> rules.canAccessDocumentsSection(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void projectManagerCanEditDocumentsSection() {
        when(projectService.isProjectManager(user.getId(), activeProject.getId())).thenReturn(true);
        assertTrue(rules.projectManagerCanEditDocumentsSection(ProjectCompositeId.id(activeProject.getId()), user));
    }

    @Test
    public void nonProjectManagerCannotEditDocumentsSection() {
        when(projectService.isProjectManager(user.getId(), activeProject.getId())).thenReturn(false);
        assertFalse(rules.projectManagerCanEditDocumentsSection(ProjectCompositeId.id(activeProject.getId()), user));
    }

    @Test
    public void projectTeamStatusAccessUnavailableForWithdrawnProject() {
        assertLeadPartnerWithdrawnProjectAccess(() -> rules.partnerCanAccessProjectTeamStatus(ProjectCompositeId.id(withdrawnProject.getId()), user));
        verify(projectService).getById(withdrawnProject.getId());
    }

    @Test
    public void projectDetailsSectionAccessUnavailableForWithdrawnProject() {
        assertLeadPartnerWithdrawnProjectAccess(() -> rules.partnerCanAccessProjectDetailsSection(ProjectCompositeId.id(withdrawnProject.getId()), user));
        verify(projectService).getById(withdrawnProject.getId());
    }

    @Test
    public void projectManagerPageAccessUnavailableForWithdrawnProject() {
        assertLeadPartnerWithdrawnProjectAccess(() -> rules.leadCanAccessProjectManagerPage(ProjectCompositeId.id(withdrawnProject.getId()), user));
        verify(projectService, times(2)).getById(withdrawnProject.getId());
    }

    @Test
    public void projectAddressPageAccessUnavailableForWithdrawnProject() {
        assertLeadPartnerWithdrawnProjectAccess(() -> rules.leadCanAccessProjectAddressPage(ProjectCompositeId.id(withdrawnProject.getId()), user));
        verify(projectService, times(2)).getById(withdrawnProject.getId());
    }

    @Test
    public void financeContactPageAccessUnavailableForWithdrawnProject() {
        assertNonLeadPartnerWithdrawnProjectAccess(() -> rules.partnerCanAccessFinanceContactPage(ProjectCompositeId.id(withdrawnProject.getId()), user));
        verify(projectService).getById(withdrawnProject.getId());
    }

    @Test
    public void monitoringOfficerSectionAccessUnavailableForWithdrawnProject() {
        setUpPartnerProjectLocationRequiredMocking();
        assertNonLeadPartnerWithdrawnProjectAccess(() -> rules.partnerCanAccessMonitoringOfficerSection(ProjectCompositeId.id(withdrawnProject.getId()), user));
        verify(projectService, times(2)).getById(withdrawnProject.getId());
    }

    @Test
    public void projectLocationSectionAccessUnavailableForWithdrawnProject() {
        setUpPartnerProjectLocationRequiredMocking();
        assertNonLeadPartnerWithdrawnProjectAccess(() -> rules.partnerCanAccessProjectLocationPage(ProjectCompositeId.id(withdrawnProject.getId()), user));
        verify(projectService, times(2)).getById(withdrawnProject.getId());
    }

    @Test
    public void bankDetailsSectionAccessUnavailableForWithdrawnProject() {
        assertNonLeadPartnerWithdrawnProjectAccess(() -> rules.partnerCanAccessBankDetailsSection(ProjectCompositeId.id(withdrawnProject.getId()), user));
        verify(projectService, times(2)).getById(withdrawnProject.getId());
    }

    @Test
    public void spendProfileSectionAccessUnavailableForWithdrawnProject() {
        assertNonLeadPartnerWithdrawnProjectAccess(() -> rules.partnerCanAccessSpendProfileSection(ProjectCompositeId.id(withdrawnProject.getId()), user));
        verify(projectService).getById(withdrawnProject.getId());
    }

    @Test
    public void editSpendProfileSectionAccessUnavailableForWithdrawnProject() {
        ProjectOrganisationCompositeId projectOrganisationCompositeId = new ProjectOrganisationCompositeId(withdrawnProject.getId(), 22L);
        assertNonLeadPartnerWithdrawnProjectAccess(() -> rules.partnerCanEditSpendProfileSection(projectOrganisationCompositeId, user));
        verify(projectService).getById(withdrawnProject.getId());
    }

    @Test
    public void grantOfferLetterSectionAccess() {
        assertNonLeadPartnerSuccessfulAccess(SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection,
                () -> rules.partnerCanAccessGrantOfferLetterSection(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void grantOfferLetterSectionAccessMonitoringOfficer() {
        assertMonitoringOfficerSuccessfulAccess(SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection,
                () -> rules.partnerCanAccessGrantOfferLetterSection(ProjectCompositeId.id(activeProject.getId()), monitoringOfficer));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void signedGrantOfferLetterSuccessfulAccessByLead() {
        assertLeadPartnerSuccessfulAccess(SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection,
                () -> rules.leadPartnerAccessToSignedGrantOfferLetter(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService).getById(activeProject.getId());
    }

    @Test
    public void signedGrantOfferLetterUnSuccessfulAccessByNonLead() {
        assertNonLeadPartnerUnsuccessfulAccess(SetupSectionAccessibilityHelper::canAccessGrantOfferLetterSection,
                () -> rules.leadPartnerAccessToSignedGrantOfferLetter(ProjectCompositeId.id(activeProject.getId()), user));
    }

    @Test
    public void markSpendProfileIncompleteAccess() {
        ProjectUserResource leadPartnerProjectUserResource = newProjectUserResource().withUser(user.getId()).build();

        when(projectService.getLeadPartners(activeProject.getId())).thenReturn(singletonList(leadPartnerProjectUserResource));
        assertTrue(rules.userCanMarkSpendProfileIncomplete(ProjectCompositeId.id(activeProject.getId()), user));
        verify(projectService).getLeadPartners(activeProject.getId());
    }

    @Test
    public void userCannotMarkOwnOrganisationAsIncomplete() {
        long userId = 1L;
        long organisationId = 2L;
        long projectId = 3L;

        UserResource userResource = newUserResource().withId(userId).build();
        OrganisationResource organisationResource = newOrganisationResource().withId(organisationId).build();

        when(organisationRestService.getByUserAndProjectId(userId, projectId)).thenReturn(restSuccess(organisationResource));
        assertFalse(rules.userCannotMarkOwnSpendProfileIncomplete(new ProjectOrganisationCompositeId(projectId, organisationId), userResource));
        verify(organisationRestService).getByUserAndProjectId(userId, projectId);
    }

    @Test
    public void userCanMarkOtherOrganisationAsIncomplete() {
        long userId = 1L;
        long organisationId = 2L;
        long otherOrganisationId = 3L;
        long projectId = 4L;

        UserResource userResource = newUserResource().withId(userId).build();
        OrganisationResource organisationResource = newOrganisationResource().withId(otherOrganisationId).build();

        when(organisationRestService.getByUserAndProjectId(userId, projectId)).thenReturn(restSuccess(organisationResource));
        assertTrue(rules.userCannotMarkOwnSpendProfileIncomplete(new ProjectOrganisationCompositeId(projectId, organisationId), userResource));
        verify(organisationRestService).getByUserAndProjectId(userId, projectId);
    }

    @Test
    public void partnerAccess() {
        long organisationId = 234L;

        UserResource user = newUserResource().withRolesGlobal(singletonList(PARTNER)).build();

        BaseIntegrationTest.setLoggedInUser(user);

        OrganisationResource o = newOrganisationResource().withId(organisationId).build();

        ProjectPartnerStatusResource partnerStatus = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).withOrganisationType(OrganisationTypeEnum.valueOf(BUSINESS.toString())).build();
        List<ProjectUserResource> pu = newProjectUserResource().withProject(activeProject.getId()).withOrganisation(o.getId()).withUser(user.getId()).build(1);
        pu.get(0).setRoleName(PARTNER.getName());

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().withPartnerStatuses(singletonList(partnerStatus)).build();

        when(projectService.getById(activeProject.getId())).thenReturn(activeProject);
        when(projectService.getOrganisationIdFromUser(activeProject.getId(), user)).thenReturn(organisationId);
        when(statusService.getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()))).thenReturn(teamStatus);
        when(accessor.canAccessFinanceChecksSection(any())).thenReturn(ACCESSIBLE);

        assertTrue(rules.partnerCanAccessFinanceChecksSection(ProjectCompositeId.id(activeProject.getId()), user));

        verify(accessor).canAccessFinanceChecksSection(any());
    }

    @Test
    public void partnerNoAccess() {
        
        long organisationId = 234L;

        UserResource user = newUserResource().withRolesGlobal(singletonList(PARTNER)).build();

        BaseIntegrationTest.setLoggedInUser(user);

        OrganisationResource o = newOrganisationResource().withId(organisationId).build();

        ProjectPartnerStatusResource partnerStatus = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).withOrganisationType(OrganisationTypeEnum.valueOf(BUSINESS.toString())).build();
        List<ProjectUserResource> pu = newProjectUserResource().withProject(activeProject.getId()).withOrganisation(o.getId()).withUser(user.getId()).build(1);
        pu.get(0).setRoleName(PARTNER.getName());

        when(projectService.getById(activeProject.getId())).thenReturn(activeProject);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().withPartnerStatuses(singletonList(partnerStatus)).build();
        when(projectService.getProjectUsersForProject(activeProject.getId())).thenReturn(pu);
        when(statusService.getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()))).thenReturn(teamStatus);
        when(accessor.canAccessFinanceChecksSection(any())).thenReturn(NOT_ACCESSIBLE);
        when(projectService.getOrganisationIdFromUser(activeProject.getId(), user)).thenReturn(organisationId);

        assertFalse(rules.partnerCanAccessFinanceChecksSection(ProjectCompositeId.id(activeProject.getId()), user));

    }

    @Test
    public void financeContactAccess() {
        long organisationId = 234L;

        UserResource user = newUserResource().withRolesGlobal(singletonList(FINANCE_CONTACT)).build();

        BaseIntegrationTest.setLoggedInUser(user);

        OrganisationResource o = newOrganisationResource().withId(organisationId).build();

        ProjectPartnerStatusResource partnerStatus = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).withOrganisationType(OrganisationTypeEnum.valueOf(BUSINESS.toString())).build();
        List<ProjectUserResource> pu = newProjectUserResource().withProject(activeProject.getId()).withOrganisation(o.getId()).withUser(user.getId()).build(2);
        pu.get(0).setRoleName(PARTNER.getName());
        pu.get(1).setRoleName(FINANCE_CONTACT.getName());

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().withPartnerStatuses(singletonList(partnerStatus)).build();

        when(projectService.getById(activeProject.getId())).thenReturn(activeProject);
        when(projectService.getOrganisationIdFromUser(activeProject.getId(), user)).thenReturn(organisationId);
        when(statusService.getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()))).thenReturn(teamStatus);
        when(accessor.canAccessFinanceChecksSection(any())).thenReturn(ACCESSIBLE);

        assertTrue(rules.partnerCanAccessFinanceChecksSection(ProjectCompositeId.id(activeProject.getId()), user));

        verify(projectService, times(2)).getById(activeProject.getId());
        verify(projectService).getOrganisationIdFromUser(activeProject.getId(), user);
        verify(statusService).getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()));
    }

    private void assertLeadPartnerSuccessfulAccess(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> accessorCheck,
                                                   Supplier<Boolean> ruleCheck) {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withOrganisationId(456L).
                        withOrganisationType(BUSINESS).
                        withIsLeadPartner(true).
                        build()).
                build();

        when(projectService.getById(activeProject.getId())).thenReturn(activeProject);
        when(projectService.getOrganisationIdFromUser(activeProject.getId(), user)).thenReturn(456L);
        when(statusService.getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()))).thenReturn(teamStatus);
        when(projectService.isUserLeadPartner(activeProject.getId(), user.getId())).thenReturn(true);
        when(projectService.isProjectManager(user.getId(), activeProject.getId())).thenReturn(true);

        OrganisationResource expectedOrganisation = new OrganisationResource();
        expectedOrganisation.setId(456L);
        expectedOrganisation.setOrganisationType(
                teamStatus.getPartnerStatusForOrganisation(456L).get().getOrganisationType().getId());

        when(accessorCheck.apply(accessor, expectedOrganisation)).thenReturn(ACCESSIBLE);

        assertTrue(ruleCheck.get());

        verify(projectService).getOrganisationIdFromUser(activeProject.getId(), user);
        verify(statusService).getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()));
    }

    private void assertLeadPartnerWithdrawnProjectAccess(Supplier<Boolean> ruleCheck) {

        when(projectService.getById(withdrawnProject.getId())).thenReturn(withdrawnProject);

        assertFalse(ruleCheck.get());

        verify(projectService, never()).getOrganisationIdFromUser(withdrawnProject.getId(), user);
        verify(statusService, never()).getProjectTeamStatus(withdrawnProject.getId(), Optional.of(user.getId()));
    }

    private void assertNonLeadPartnerSuccessfulAccess(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> accessorCheck,
                                                      Supplier<Boolean> ruleCheck) {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withOrganisationId(456L).
                        withOrganisationType(BUSINESS).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withOrganisationId(789L).
                        withOrganisationType(BUSINESS).
                        build(1)).
                build();

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(user.getId()).
                withOrganisation(789L).
                withRole(PARTNER).
                build(1);

        when(projectService.getById(activeProject.getId())).thenReturn(activeProject);

        when(projectService.getProjectUsersForProject(activeProject.getId())).thenReturn(projectUsers);

        when(statusService.getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()))).thenReturn(teamStatus);

        when(projectService.getOrganisationIdFromUser(activeProject.getId(), user)).thenReturn(789L);

        when(projectService.isUserLeadPartner(activeProject.getId(), user.getId())).thenReturn(false);

        OrganisationResource expectedOrganisation = new OrganisationResource();
        expectedOrganisation.setId(789L);
        expectedOrganisation.setOrganisationType(
                teamStatus.getPartnerStatusForOrganisation(789L).get().getOrganisationType().getId());

        when(accessorCheck.apply(accessor, expectedOrganisation)).thenReturn(ACCESSIBLE);

        assertTrue(ruleCheck.get());

        verify(projectService, atLeastOnce()).getById(activeProject.getId());
        verify(projectService).getOrganisationIdFromUser(activeProject.getId(), user);
        verify(statusService).getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()));

        accessorCheck.apply(verify(accessor), expectedOrganisation);
    }

    private void assertMonitoringOfficerSuccessfulAccess(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> accessorCheck,
                                                         Supplier<Boolean> ruleCheck) {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withOrganisationId(456L).
                        withOrganisationType(BUSINESS).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withOrganisationId(789L).
                        withOrganisationType(BUSINESS).
                        build(1)).
                build();

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(user.getId()).
                withOrganisation(789L).
                withRole(PARTNER).
                build(1);

        when(projectService.getById(activeProject.getId())).thenReturn(activeProject);

        when(projectService.getProjectUsersForProject(activeProject.getId())).thenReturn(projectUsers);

        when(statusService.getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()))).thenReturn(teamStatus);

        List<ProjectUserResource> leadPartners = newProjectUserResource().
                withUser(user.getId()).
                withOrganisation(789L).
                withRole(PROJECT_MANAGER).
                build(1);

        when(projectService.getLeadOrganisation(activeProject.getId())).thenReturn(newOrganisationResource().withId(789L).build());

        when(projectService.getLeadPartners(activeProject.getId())).thenReturn(leadPartners);

        OrganisationResource expectedOrganisation = new OrganisationResource();
        expectedOrganisation.setId(789L);
        expectedOrganisation.setOrganisationType(
                teamStatus.getPartnerStatusForOrganisation(789L).get().getOrganisationType().getId());

        when(accessorCheck.apply(accessor, expectedOrganisation)).thenReturn(ACCESSIBLE);

        assertTrue(ruleCheck.get());

        verify(projectService, atLeastOnce()).getById(activeProject.getId());
        verify(statusService).getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()));
        verify(projectService).getLeadPartners(activeProject.getId());

        accessorCheck.apply(verify(accessor), expectedOrganisation);
    }

    private void assertMonitoringOfficerUnSuccessfulAccess(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> accessorCheck,
                                                           Supplier<Boolean> ruleCheck) {
        assertFalse(ruleCheck.get());
    }

    private void assertNonLeadPartnerUnsuccessfulAccess(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> accessorCheck,
                                                      Supplier<Boolean> ruleCheck) {
        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withOrganisationId(456L).
                        withOrganisationType(BUSINESS).
                        withIsLeadPartner(true).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withOrganisationId(789L).
                        withOrganisationType(BUSINESS).
                        build(1)).
                build();

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(user.getId()).
                withOrganisation(789L).
                withRole(PARTNER).
                build(1);

        when(projectService.getById(activeProject.getId())).thenReturn(activeProject);

        when(projectService.getProjectUsersForProject(activeProject.getId())).thenReturn(projectUsers);

        when(statusService.getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()))).thenReturn(teamStatus);

        when(projectService.getOrganisationIdFromUser(activeProject.getId(), user)).thenReturn(789L);

        when(projectService.isProjectManager(user.getId(), activeProject.getId())).thenReturn(false);

        when(projectService.isUserLeadPartner(activeProject.getId(), user.getId())).thenReturn(false);

        OrganisationResource expectedOrganisation = new OrganisationResource();
        expectedOrganisation.setId(789L);
        expectedOrganisation.setOrganisationType(
                teamStatus.getPartnerStatusForOrganisation(789L).get().getOrganisationType().getId());

        when(accessorCheck.apply(accessor, expectedOrganisation)).thenReturn(ACCESSIBLE);

        assertFalse(ruleCheck.get());

        verify(projectService, atLeastOnce()).getById(activeProject.getId());
        verify(projectService).getOrganisationIdFromUser(activeProject.getId(), user);
        verify(statusService).getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()));

        accessorCheck.apply(verify(accessor), expectedOrganisation);
    }

    private void assertNonLeadPartnerAndNotMOUnsuccessfulAccess(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> accessorCheck,
                                                        Supplier<Boolean> ruleCheck) {
        assertFalse(ruleCheck.get());
    }


    private void assertNonLeadPartnerWithdrawnProjectAccess(Supplier<Boolean> ruleCheck) {

        when(projectService.getById(withdrawnProject.getId())).thenReturn(withdrawnProject);

        assertFalse(ruleCheck.get());

        verify(projectService, atLeastOnce()).getById(withdrawnProject.getId());
        verify(projectService, never()).getOrganisationIdFromUser(withdrawnProject.getId(), user);
        verify(statusService, never()).getProjectTeamStatus(withdrawnProject.getId(), Optional.of(user.getId()));
    }

    private void assertNotOnProjectExpectations(Supplier<Boolean> ruleCheck) {

        when(projectService.getById(activeProject.getId())).thenReturn(activeProject);

        when(projectService.getOrganisationIdFromUser(activeProject.getId(), user)).thenThrow(new ForbiddenActionException(CANNOT_GET_ANY_USERS_FOR_PROJECT.getErrorKey(), singletonList(activeProject.getId())));

        assertFalse(ruleCheck.get());

        verify(projectService).getById(activeProject.getId());
        verify(projectService).getOrganisationIdFromUser(activeProject.getId(), user);
        verify(statusService, never()).getProjectTeamStatus(activeProject.getId(), Optional.of(user.getId()));
    }

    private void assertForbiddenExpectations(Supplier<Boolean> ruleCheck) {

        when(projectService.getById(activeProject.getId())).thenReturn(activeProject);

        when(projectService.getOrganisationIdFromUser(activeProject.getId(), user)).thenThrow(new ForbiddenActionException(CANNOT_GET_ANY_USERS_FOR_PROJECT.getErrorKey(), singletonList(activeProject.getId())));

        assertFalse(ruleCheck.get());

        verify(projectService).getById(activeProject.getId());
        verify(projectService).getOrganisationIdFromUser(activeProject.getId(), user);
    }

    private void assertScenariosForSections(BiFunction<SetupSectionAccessibilityHelper, OrganisationResource, SectionAccess> accessorCheck, Supplier<Boolean> ruleCheck) {
        assertLeadPartnerSuccessfulAccess(accessorCheck, ruleCheck);
        resetMocks();

        assertNonLeadPartnerSuccessfulAccess(accessorCheck, ruleCheck);
        resetMocks();

        assertNotOnProjectExpectations(ruleCheck);
        resetMocks();

        assertForbiddenExpectations(ruleCheck);
    }

    private void resetMocks() {
        reset(projectService, statusService, accessor);
    }

    @Override
    protected SetupSectionsPermissionRules supplyPermissionRulesUnderTest() {
        return new SetupSectionsPermissionRules();
    }
}
