package org.innovateuk.ifs.project;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.sections.SectionStatus;
import org.innovateuk.ifs.project.viewmodel.ProjectSetupStatusViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectSetupStatusControllerTest extends BaseControllerMockMVCTest<ProjectSetupStatusController> {

    @Override
    protected ProjectSetupStatusController supplyControllerUnderTest() {
        return new ProjectSetupStatusController();
    }

    private static final boolean monitoringOfficerNotExpected = false;
    private static final boolean monitoringOfficerExpected = true;

    private CompetitionResource competition = newCompetitionResource().build();
    private ApplicationResource application = newApplicationResource().withCompetition(competition.getId()).build();
    private ProjectResourceBuilder projectBuilder = newProjectResource().withApplication(application);

    private ProjectResource project = projectBuilder.build();
    private OrganisationResource organisationResource = newOrganisationResource().build();

    private BankDetailsResource bankDetailsResource = newBankDetailsResource().build();
    private RestResult<BankDetailsResource> bankDetailsFoundResult = restSuccess(bankDetailsResource);
    private RestResult<BankDetailsResource> bankDetailsNotFoundResult = restFailure(notFoundError(BankDetailsResource.class, 123L));

    private MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();
    private Optional<MonitoringOfficerResource> monitoringOfficerFoundResult = Optional.of(monitoringOfficer);
    private Optional<MonitoringOfficerResource> monitoringOfficerNotFoundResult = Optional.empty();

    private Map<String, SectionStatus> partnerStatusFlagChecks = new HashMap<>();

    @Before
    public void setUpDefaults() {
        partnerStatusFlagChecks.put("projectDetailsStatus", SectionStatus.FLAG);
        partnerStatusFlagChecks.put("monitoringOfficerStatus", SectionStatus.EMPTY);
        partnerStatusFlagChecks.put("bankDetailsStatus", SectionStatus.EMPTY);
        partnerStatusFlagChecks.put("financeChecksStatus", SectionStatus.EMPTY);
        partnerStatusFlagChecks.put("spendProfileStatus", SectionStatus.EMPTY);
        partnerStatusFlagChecks.put("otherDocumentsStatus", SectionStatus.FLAG);
        partnerStatusFlagChecks.put("grantOfferLetterStatus", SectionStatus.EMPTY);
    }

    @Test
    public void testViewProjectSetupStatus() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectLeadStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerNotExpected);

        assertStatuses(viewModel);

    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedAsLead() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectLeadStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerNotExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS));
    }

    @Test
    public void testViewProjectSetupStatusForNonLeadPartnerWithFinanceContactNotSubmitted() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectLeadStatusResource()
                        .withOrganisationId(999L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerNotExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS),
                Pair.of("otherDocumentsStatus", SectionStatus.HOURGLASS));
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedAndFinanceContactSubmitted() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectLeadStatusResource()
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(COMPLETE)
                        .build(1)).
                        build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerNotExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS));
    }

    @Test
    public void testViewProjectSetupStatusWhenAwaitingProjectDetailsActionFromOtherPartners() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource()
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withSpendProfileStatus(NOT_REQUIRED)
                        .withOrganisationId(organisationResource.getId())
                        .build())
                .withPartnerStatuses(newProjectPartnerStatusResource()
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerNotExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("monitoringOfficerStatus", SectionStatus.HOURGLASS));
    }

    @Test
    public void testViewProjectSetupStatusWithMonitoringOfficerAssigned() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.EMPTY));
    }

    @Test
    public void testViewProjectSetupStatusWithBankDetailsEntered() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));
    }

    @Test
    public void testViewProjectSetupStatusWithAllBankDetailsCompleteOrNotRequired() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(NOT_REQUIRED).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.HOURGLASS));
    }

    @Test
    public void testViewProjectSetupStatusWithAllFinanceChecksApproved() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(NOT_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK));
    }

    @Test
    public void testViewProjectSetupStatusWithSpendProfile() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(ACTION_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(ACTION_REQUIRED).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.FLAG));
    }

    @Test
    public void testViewProjectSetupStatusWithSpendProfilePartnerComplete() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(ACTION_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.FLAG));
    }

    @Test
    public void testViewProjectSetupStatusWithSpendAwaitingApproval() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(PENDING).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.HOURGLASS));
    }

    @Test
    public void testViewProjectSetupStatusWithSpendApproved() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.TICK));
    }

    @Test
    public void testViewProjectSetupStatusWithGOLNotSent() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withGrantOfferStatus(NOT_STARTED).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectDetailsStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.TICK),
                Pair.of("grantOfferLetterStatus", SectionStatus.HOURGLASS));
    }

    @Test
    public void testViewProjectSetupStatusWithGOLSent() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withGrantOfferStatus(ACTION_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectDetailsStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.TICK),
                Pair.of("grantOfferLetterStatus", SectionStatus.FLAG));
    }

    @Test
    public void testViewProjectSetupStatusWithGOLReturned() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withGrantOfferStatus(PENDING).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectDetailsStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.TICK),
                Pair.of("grantOfferLetterStatus", SectionStatus.HOURGLASS));
    }

    @Test
    public void testViewProjectSetupStatusWithGOLApproved() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withFinanceChecksStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withGrantOfferStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(newProjectPartnerStatusResource().
                        withFinanceChecksStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withSpendProfileStatus(COMPLETE).
                        withProjectDetailsStatus(COMPLETE).
                        build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsFoundResult, teamStatus);

        ProjectSetupStatusViewModel viewModel = performViewProjectStatusCallAndAssertBasicDetails(monitoringOfficerExpected);
        assertPartnerStatusFlagsCorrect(viewModel,
                Pair.of("projectDetailsStatus", SectionStatus.TICK),
                Pair.of("monitoringOfficerStatus", SectionStatus.TICK),
                Pair.of("bankDetailsStatus", SectionStatus.TICK),
                Pair.of("financeChecksStatus", SectionStatus.TICK),
                Pair.of("spendProfileStatus", SectionStatus.TICK),
                Pair.of("grantOfferLetterStatus", SectionStatus.TICK));
    }


    private ProjectSetupStatusViewModel performViewProjectStatusCallAndAssertBasicDetails(boolean expectedMonitoringOfficer) throws Exception {

        MvcResult result = performViewProjectStatusCall();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertStandardViewModelValuesCorrect(viewModel, expectedMonitoringOfficer);
        return viewModel;
    }

    private MvcResult performViewProjectStatusCall() throws Exception {
        return mockMvc.perform(get("/project/{id}", project.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();
    }

    private void setupLookupProjectDetailsExpectations(Optional<MonitoringOfficerResource> monitoringOfficerResult, RestResult<BankDetailsResource> bankDetailsResult, ProjectTeamStatusResource teamStatus) {

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(project.getId())).thenReturn(monitoringOfficerResult);
        when(projectService.getOrganisationByProjectAndUser(project.getId(), loggedInUser.getId())).thenReturn(organisationResource);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(newProjectUserResource().
                withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRoleName(PARTNER).build(1));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(project.getId(), organisationResource.getId())).thenReturn(bankDetailsResult);
        when(projectService.getProjectTeamStatus(project.getId(), Optional.empty())).thenReturn(teamStatus);
    }

    private void assertStandardViewModelValuesCorrect(ProjectSetupStatusViewModel viewModel, boolean existingMonitoringOfficerExpected) {
        assertEquals(project.getId(), viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(competition.getName(), viewModel.getCompetitionName());
        assertEquals(application.getId(), viewModel.getApplicationId());
        assertEquals(organisationResource.getId(), viewModel.getOrganisationId());

        if (existingMonitoringOfficerExpected) {
            assertEquals(monitoringOfficer.getFullName(), viewModel.getMonitoringOfficerName());
        } else {
            assertEquals("", viewModel.getMonitoringOfficerName());
        }
    }

    private final void assertPartnerStatusFlagsCorrect(ProjectSetupStatusViewModel viewModel, Pair<String, SectionStatus>... expectedTrueFlags) {
        for (Pair<String, SectionStatus> section : expectedTrueFlags) {
            partnerStatusFlagChecks.replace(section.getLeft(), section.getRight());
        }
        assertStatuses(viewModel);
    }

    private void assertStatuses(ProjectSetupStatusViewModel viewModel) {
        assertTrue(partnerStatusFlagChecks.get("projectDetailsStatus") == viewModel.getProjectDetailsStatus());
        assertTrue(partnerStatusFlagChecks.get("monitoringOfficerStatus") == viewModel.getMonitoringOfficerStatus());
        assertTrue(partnerStatusFlagChecks.get("bankDetailsStatus") == viewModel.getBankDetailsStatus());
        assertTrue(partnerStatusFlagChecks.get("financeChecksStatus") == viewModel.getFinanceChecksStatus());
        assertTrue(partnerStatusFlagChecks.get("spendProfileStatus") == viewModel.getSpendProfileStatus());
        assertTrue(partnerStatusFlagChecks.get("otherDocumentsStatus") == viewModel.getOtherDocumentsStatus());
        assertTrue(partnerStatusFlagChecks.get("grantOfferLetterStatus") == viewModel.getGrantOfferLetterStatus());
    }

}
