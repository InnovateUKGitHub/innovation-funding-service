package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.competition.resource.CompetitionResource;
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

    @Test
    public void testViewProjectSetupStatus() throws Exception {

        ProjectResource project = projectBuilder.build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(Optional.empty());
        when(projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId())).thenReturn(organisationResource);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(newProjectUserResource().
                withUser(loggedInUser.getId()).withRoleName(PARTNER).build(1));

        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId())).thenReturn(
                restFailure(notFoundError(BankDetailsResource.class, 1L)));

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withOrganisationId(organisationResource.getId()).
                        build()).
                build();

        when(projectService.getProjectTeamStatus(project.getId(), Optional.empty())).thenReturn(teamStatus);

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(competition.getName(), viewModel.getCompetitionName());
        assertEquals(application.getId(), viewModel.getApplicationId());
        assertFalse(viewModel.isProjectDetailsSubmitted());
        assertFalse(viewModel.isMonitoringOfficerAssigned());
        assertEquals("", viewModel.getMonitoringOfficerName());
        assertFalse(viewModel.isBankDetailsActionRequired());
        assertFalse(viewModel.isBankDetailsComplete());
        assertEquals(organisationResource.getId(), viewModel.getOrganisationId());
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmitted() throws Exception {

        ProjectResource project = projectBuilder.
                build();

        OrganisationResource organisationResource = newOrganisationResource().build();

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(Optional.empty());
        when(projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId())).thenReturn(organisationResource);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(newProjectUserResource().
                withUser(loggedInUser.getId()).withRoleName(PARTNER).build(1));

        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId())).thenReturn(
                restFailure(notFoundError(BankDetailsResource.class, 1L)));

        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(teamStatus);

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(competition.getName(), viewModel.getCompetitionName());
        assertEquals(application.getId(), viewModel.getApplicationId());
        assertTrue(viewModel.isProjectDetailsSubmitted());
        assertFalse(viewModel.isMonitoringOfficerAssigned());
        assertEquals("", viewModel.getMonitoringOfficerName());
        assertFalse(viewModel.isBankDetailsActionRequired());
        assertFalse(viewModel.isBankDetailsComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithMonitoringOfficerAssigned() throws Exception {

        ProjectResource project = projectBuilder.withId(projectId).build();
        MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();
        OrganisationResource organisationResource = newOrganisationResource().build();
        Optional<MonitoringOfficerResource> monitoringOfficerResult = Optional.of(monitoringOfficer);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(ACTION_REQUIRED).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(teamStatus);
        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(monitoringOfficerResult);
        when(projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId())).thenReturn(organisationResource);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(newProjectUserResource().
                withUser(loggedInUser.getId()).withRoleName(PARTNER).build(1));

        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId())).thenReturn(
                restFailure(notFoundError(BankDetailsResource.class, 1L)));

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(competition.getName(), viewModel.getCompetitionName());
        assertEquals(application.getId(), viewModel.getApplicationId());
        assertTrue(viewModel.isProjectDetailsSubmitted());
        assertTrue(viewModel.isMonitoringOfficerAssigned());
        assertEquals(monitoringOfficer.getFullName(), viewModel.getMonitoringOfficerName());
        assertTrue(viewModel.isBankDetailsActionRequired());
        assertFalse(viewModel.isBankDetailsComplete());
    }

    @Test
    public void testViewProjectSetupStatusWithBankDetailsEntered() throws Exception {

        ProjectResource project = projectBuilder.build();

        MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();
        OrganisationResource organisationResource = newOrganisationResource().build();
        Optional<MonitoringOfficerResource> monitoringOfficerResult = Optional.of(monitoringOfficer);
        BankDetailsResource bankDetailsResource = newBankDetailsResource().build();

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withBankDetailsStatus(COMPLETE).
                        withOrganisationId(organisationResource.getId()).
                        build()).
                build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(monitoringOfficerResult);
        when(projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId())).thenReturn(organisationResource);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(newProjectUserResource().
                withUser(loggedInUser.getId()).withRoleName(PARTNER).build(1));

        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId())).thenReturn(restSuccess(bankDetailsResource));

        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(teamStatus);

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(competition.getName(), viewModel.getCompetitionName());
        assertEquals(application.getId(), viewModel.getApplicationId());
        assertTrue(viewModel.isProjectDetailsSubmitted());
        assertTrue(viewModel.isMonitoringOfficerAssigned());
        assertEquals(monitoringOfficer.getFullName(), viewModel.getMonitoringOfficerName());
        assertFalse(viewModel.isBankDetailsActionRequired());
        assertTrue(viewModel.isBankDetailsComplete());
    }
}
