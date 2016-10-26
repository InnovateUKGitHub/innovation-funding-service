package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.builder.ProjectPartnerStatusResourceBuilder;
import com.worth.ifs.project.builder.ProjectResourceBuilder;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.viewmodel.ProjectSetupStatusViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static com.worth.ifs.project.builder.ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.project.constant.ProjectActivityStates.ACTION_REQUIRED;
import static com.worth.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static com.worth.ifs.project.constant.ProjectActivityStates.NOT_STARTED;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectSetupStatusControllerTest extends BaseControllerMockMVCTest<ProjectSetupStatusController> {
	
    @Override
    protected ProjectSetupStatusController supplyControllerUnderTest() {
        return new ProjectSetupStatusController();
    }

    private Long projectId = 20L;
    private CompetitionResource competition = newCompetitionResource().build();
    private ApplicationResource application = newApplicationResource().withCompetition(competition.getId()).build();
    private ProjectResourceBuilder projectBuilder = newProjectResource().withId(projectId).withApplication(application);

    private ProjectResource project = projectBuilder.build();
    private OrganisationResource organisationResource = newOrganisationResource().build();

    private BankDetailsResource bankDetailsResource = newBankDetailsResource().build();
    private RestResult<BankDetailsResource> bankDetailsFoundResult = restSuccess(bankDetailsResource);
    private RestResult<BankDetailsResource> bankDetailsNotFoundResult = restFailure(notFoundError(BankDetailsResource.class, 123L));

    private MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();
    private Optional<MonitoringOfficerResource> monitoringOfficerFoundResult = Optional.of(monitoringOfficer);
    private Optional<MonitoringOfficerResource> monitoringOfficerNotFoundResult = Optional.empty();

    private ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
            withProjectLeadStatus(newProjectLeadStatusResource().
                    withOrganisationId(organisationResource.getId()).
                    build()).
            withPartnerStatuses(ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource()
                    .withOrganisationId(organisationResource.getId())
                    .withFinanceContactStatus(COMPLETE)
                    .build(1))
            .build();

    @Test
    public void testViewProjectSetupStatus() throws Exception {

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertStandardViewModelValuesCorrect(viewModel);

        assertFalse(viewModel.isProjectDetailsSubmitted());
        assertFalse(viewModel.isProjectDetailsProcessCompleted());
        assertFalse(viewModel.isAwaitingProjectDetailsActionFromOtherPartners());
        assertFalse(viewModel.isMonitoringOfficerAssigned());
        assertEquals("", viewModel.getMonitoringOfficerName());
        assertFalse(viewModel.isBankDetailsActionRequired());
        assertFalse(viewModel.isBankDetailsComplete());
        assertFalse(viewModel.isOwnFinanceCheckApproved());
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedButFinanceContactNotYetSubmittedAsLead() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectLeadStatusResource()
                        .withOrganisationId(5L)
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(NOT_STARTED)
                        .build())
                .withPartnerStatuses(ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertStandardViewModelValuesCorrect(viewModel);

        assertTrue(viewModel.isProjectDetailsSubmitted());
        assertFalse(viewModel.isProjectDetailsProcessCompleted());
        assertFalse(viewModel.isAwaitingProjectDetailsActionFromOtherPartners());
        assertFalse(viewModel.isMonitoringOfficerAssigned());
        assertEquals("", viewModel.getMonitoringOfficerName());
        assertFalse(viewModel.isBankDetailsActionRequired());
        assertFalse(viewModel.isBankDetailsComplete());
        assertFalse(viewModel.isOwnFinanceCheckApproved());
    }

    @Test
    public void testViewProjectSetupStatusForNonLeadPartnerWithFinanceContactNotSubmitted() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource()
                .withProjectLeadStatus(newProjectLeadStatusResource()
                        .withOrganisationId(5L)
                        .withProjectDetailsStatus(COMPLETE)
                        .build())
                .withPartnerStatuses(ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertStandardViewModelValuesCorrect(viewModel);

        assertTrue(viewModel.isProjectDetailsSubmitted());
        assertFalse(viewModel.isProjectDetailsProcessCompleted());
        assertFalse(viewModel.isAwaitingProjectDetailsActionFromOtherPartners());
        assertFalse(viewModel.isMonitoringOfficerAssigned());
        assertEquals("", viewModel.getMonitoringOfficerName());
        assertFalse(viewModel.isBankDetailsActionRequired());
        assertFalse(viewModel.isBankDetailsComplete());
        assertFalse(viewModel.isOwnFinanceCheckApproved());
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmittedAndFinanceContactSubmitted() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withFinanceContactStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                withPartnerStatuses(ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withFinanceContactStatus(COMPLETE)
                        .build(1)).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertStandardViewModelValuesCorrect(viewModel);

        assertTrue(viewModel.isProjectDetailsSubmitted());
        assertTrue(viewModel.isProjectDetailsProcessCompleted());
        assertFalse(viewModel.isAwaitingProjectDetailsActionFromOtherPartners());
        assertFalse(viewModel.isMonitoringOfficerAssigned());
        assertEquals("", viewModel.getMonitoringOfficerName());
        assertFalse(viewModel.isBankDetailsActionRequired());
        assertFalse(viewModel.isBankDetailsComplete());
        assertFalse(viewModel.isOwnFinanceCheckApproved());
    }

    @Test
    public void testViewProjectSetupStatusWhenAwaitingProjectDetailsActionFromOtherPartners() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource()
                        .withProjectDetailsStatus(COMPLETE)
                        .withFinanceContactStatus(COMPLETE)
                        .withOrganisationId(organisationResource.getId())
                        .build())
                .withPartnerStatuses(ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource()
                        .withOrganisationId(organisationResource.getId())
                        .withFinanceContactStatus(NOT_STARTED)
                        .build(1))
                .build();

        setupLookupProjectDetailsExpectations(monitoringOfficerNotFoundResult, bankDetailsNotFoundResult, teamStatus);

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertStandardViewModelValuesCorrect(viewModel);

        assertTrue(viewModel.isProjectDetailsSubmitted());
        assertFalse(viewModel.isProjectDetailsProcessCompleted());
        assertTrue(viewModel.isAwaitingProjectDetailsActionFromOtherPartners());
        assertFalse(viewModel.isMonitoringOfficerAssigned());
        assertEquals("", viewModel.getMonitoringOfficerName());
        assertFalse(viewModel.isBankDetailsActionRequired());
        assertFalse(viewModel.isBankDetailsComplete());
        assertFalse(viewModel.isOwnFinanceCheckApproved());
    }

    @Test
    public void testViewProjectSetupStatusWithMonitoringOfficerAssigned() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(ACTION_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertStandardViewModelValuesCorrect(viewModel);

        assertTrue(viewModel.isProjectDetailsSubmitted());
        assertTrue(viewModel.isProjectDetailsProcessCompleted());
        assertTrue(viewModel.isMonitoringOfficerAssigned());
        assertEquals(monitoringOfficer.getFullName(), viewModel.getMonitoringOfficerName());
        assertTrue(viewModel.isBankDetailsActionRequired());
        assertFalse(viewModel.isBankDetailsComplete());
        assertFalse(viewModel.isOwnFinanceCheckApproved());
    }

    @Test
    public void testViewProjectSetupStatusWithBankDetailsEntered() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                build();

        setupLookupProjectDetailsExpectations(monitoringOfficerFoundResult, bankDetailsNotFoundResult, teamStatus);

        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId())).thenReturn(bankDetailsFoundResult);

        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(teamStatus);

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertStandardViewModelValuesCorrect(viewModel);

        assertTrue(viewModel.isProjectDetailsSubmitted());
        assertTrue(viewModel.isProjectDetailsProcessCompleted());
        assertTrue(viewModel.isMonitoringOfficerAssigned());
        assertEquals(monitoringOfficer.getFullName(), viewModel.getMonitoringOfficerName());
        assertFalse(viewModel.isBankDetailsActionRequired());
        assertTrue(viewModel.isBankDetailsComplete());
        assertFalse(viewModel.isOwnFinanceCheckApproved());
    }

    private void setupLookupProjectDetailsExpectations(Optional<MonitoringOfficerResource> monitoringOfficerResult, RestResult<BankDetailsResource> bankDetailsResult, ProjectTeamStatusResource teamStatus) {

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(monitoringOfficerResult);
        when(projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId())).thenReturn(organisationResource);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(newProjectUserResource().
                withUser(loggedInUser.getId())
                .withOrganisation(organisationResource.getId())
                .withRoleName(PARTNER).build(1));
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId())).thenReturn(bankDetailsResult);
        when(projectService.getProjectTeamStatus(project.getId(), Optional.empty())).thenReturn(teamStatus);
    }

    private void assertStandardViewModelValuesCorrect(ProjectSetupStatusViewModel viewModel) {
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(competition.getName(), viewModel.getCompetitionName());
        assertEquals(application.getId(), viewModel.getApplicationId());
        assertEquals(organisationResource.getId(), viewModel.getOrganisationId());
    }
}
