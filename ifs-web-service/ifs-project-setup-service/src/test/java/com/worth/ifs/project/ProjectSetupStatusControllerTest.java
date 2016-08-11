package com.worth.ifs.project;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.builder.ProjectResourceBuilder;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.viewmodel.ProjectSetupStatusViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static com.worth.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
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

        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisationResource.getId())).
                thenReturn(restSuccess(newApplicationFinanceResource().withGrantClaimPercentage(0).build()));

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
        assertFalse(viewModel.isProjectDetailsSubmitted());
        assertFalse(viewModel.isMonitoringOfficerAssigned());
        assertEquals("", viewModel.getMonitoringOfficerName());
        assertFalse(viewModel.isFunded());
        assertNull(viewModel.getBankDetails());
        assertEquals(organisationResource.getId(), viewModel.getOrganisationId());
    }

    @Test
    public void testViewProjectSetupStatusWithProjectDetailsSubmitted() throws Exception {

        ProjectResource project = projectBuilder.withSubmittedDate(LocalDateTime.of(2016, 10, 10, 0, 0)).build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(Optional.empty());
        when(projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId())).thenReturn(organisationResource);

        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisationResource.getId())).
                thenReturn(restSuccess(newApplicationFinanceResource().withGrantClaimPercentage(0).build()));

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
        assertFalse(viewModel.isMonitoringOfficerAssigned());
        assertEquals("", viewModel.getMonitoringOfficerName());
        assertFalse(viewModel.isFunded());
        assertNull(viewModel.getBankDetails());
    }


    @Test
    public void testViewProjectSetupStatusWithMonitoringOfficerAssigned() throws Exception {

        ProjectResource project = projectBuilder.withSubmittedDate(LocalDateTime.of(2016, 10, 10, 0, 0)).build();
        MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();
        OrganisationResource organisationResource = newOrganisationResource().build();
        Optional<MonitoringOfficerResource> monitoringOfficerResult = Optional.of(monitoringOfficer);

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(monitoringOfficerResult);
        when(projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId())).thenReturn(organisationResource);

        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisationResource.getId())).
                thenReturn(restSuccess(newApplicationFinanceResource().withGrantClaimPercentage(0).build()));

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
        assertFalse(viewModel.isFunded());
        assertNull(viewModel.getBankDetails());
    }

    @Test
    public void testViewProjectSetupStatusWithBankDetailsEntered() throws Exception {

        ProjectResource project = projectBuilder.withSubmittedDate(LocalDateTime.of(2016, 10, 10, 0, 0)).build();
        MonitoringOfficerResource monitoringOfficer = newMonitoringOfficerResource().build();
        OrganisationResource organisationResource = newOrganisationResource().build();
        Optional<MonitoringOfficerResource> monitoringOfficerResult = Optional.of(monitoringOfficer);
        BankDetailsResource bankDetailsResource = newBankDetailsResource().build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(monitoringOfficerResult);
        when(projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId())).thenReturn(organisationResource);

        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisationResource.getId())).
                thenReturn(restSuccess(newApplicationFinanceResource().withGrantClaimPercentage(0).build()));

        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId())).thenReturn(restSuccess(bankDetailsResource));

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
        assertFalse(viewModel.isFunded());
        assertEquals(bankDetailsResource, viewModel.getBankDetails());
    }

    @Test
    public void testViewProjectSetupStatusWithFundedPartnerOrganisation() throws Exception {

        ProjectResource project = projectBuilder.build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(Optional.empty());
        when(projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId())).thenReturn(organisationResource);

        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisationResource.getId())).
                thenReturn(restSuccess(newApplicationFinanceResource().withGrantClaimPercentage(10).build()));

        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId())).thenReturn(
                restFailure(notFoundError(BankDetailsResource.class, 1L)));

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertTrue(viewModel.isFunded());
    }

    @Test
    public void testViewProjectSetupStatusWithNullUnfundedPartnerOrganisation() throws Exception {

        ProjectResource project = projectBuilder.build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        when(applicationService.getById(application.getId())).thenReturn(application);
        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionService.getById(application.getCompetition())).thenReturn(competition);
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(Optional.empty());
        when(projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId())).thenReturn(organisationResource);

        when(applicationFinanceRestService.getFinanceDetails(application.getId(), organisationResource.getId())).
                thenReturn(restSuccess(newApplicationFinanceResource().build()));

        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId())).thenReturn(
                restFailure(notFoundError(BankDetailsResource.class, 1L)));

        MvcResult result = mockMvc.perform(get("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/setup-status"))
                .andReturn();

        ProjectSetupStatusViewModel viewModel = (ProjectSetupStatusViewModel) result.getModelAndView().getModel().get("model");
        assertFalse(viewModel.isFunded());
    }
}
