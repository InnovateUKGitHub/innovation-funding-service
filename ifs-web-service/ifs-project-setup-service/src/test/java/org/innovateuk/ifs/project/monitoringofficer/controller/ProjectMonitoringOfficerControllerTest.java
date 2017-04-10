package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.ProjectMonitoringOfficerViewModel;
import org.innovateuk.ifs.project.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 *
 **/
public class ProjectMonitoringOfficerControllerTest extends BaseControllerMockMVCTest<ProjectMonitoringOfficerController> {

    private long projectId = 123L;
    private long applicationId = 456L;
    private long competitionId = 789L;

    private MonitoringOfficerResource mo = newMonitoringOfficerResource().
            withFirstName("First").
            withLastName("Last").
            withEmail("asdf@asdf.com").
            withPhoneNumber("1234567890").
            build();

    private AddressResource projectAddress = newAddressResource().
            withAddressLine1("Line 1").
            withAddressLine2().
            withAddressLine3("Line 3").
            withTown("Line 4").
            withCounty("Line 5").
            withPostcode("").
            build();

    private ApplicationResource application = newApplicationResource().
            withId(applicationId).
            withCompetition(competitionId).
            build();

    private CompetitionResource competition = newCompetitionResource().
            withInnovationAreaNames(CollectionFunctions.asLinkedSet("Some Area", "Some other area")).
            build();

    private CompetitionSummaryResource competitionSummary = newCompetitionSummaryResource().build();

    ProjectResourceBuilder projectBuilder = newProjectResource().
            withId(projectId).
            withName("My Project").
            withApplication(applicationId).
            withAddress(projectAddress).
            withTargetStartDate(LocalDate.of(2017, 01, 05));

    @Test
    public void testViewMonitoringOfficerPage() throws Exception {

        ProjectResource project = projectBuilder.build();

        boolean existingMonitoringOfficer = true;

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        MvcResult result = mockMvc.perform(get("/project/123/monitoring-officer")).
                andExpect(view().name("project/monitoring-officer")).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        ProjectMonitoringOfficerViewModel model = (ProjectMonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        assertTrue(model.isMonitoringOfficerAssigned());
    }

    @Test
    public void viewMonitoringOfficerInReadOnly() throws Exception {
        ProjectResource project = projectBuilder.build();

        boolean existingMonitoringOfficer = true;

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        MvcResult result = mockMvc.perform(get("/project/123/monitoring-officer/readonly")).
                andExpect(view().name("project/monitoring-officer")).
                andExpect(model().attributeExists("readOnlyView")).
                andExpect(model().attribute("readOnlyView", true)).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        ProjectMonitoringOfficerViewModel model = (ProjectMonitoringOfficerViewModel) modelMap.get("model");
        Boolean readOnlyView = (Boolean) modelMap.get("readOnlyView");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        assertTrue(model.isMonitoringOfficerAssigned());
        assertTrue(readOnlyView);
    }

    private void assertProjectDetailsPrepopulatedOk(ProjectMonitoringOfficerViewModel model) {
        assertEquals(Long.valueOf(123), model.getProjectId());
        assertEquals("My Project", model.getProjectName());
        assertEquals(competition.getInnovationAreaNames(), CollectionFunctions.asLinkedSet("Some Area", "Some other area"));
    }


    private void setupViewMonitoringOfficerTestExpectations(ProjectResource project, boolean existingMonitoringOfficer) {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        build()).
                build();

        Optional<MonitoringOfficerResource> monitoringOfficerToUse = existingMonitoringOfficer ? Optional.of(mo) : Optional.empty();
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(monitoringOfficerToUse);

        when(projectService.getById(projectId)).thenReturn(project);
        when(applicationService.getById(applicationId)).thenReturn(application);
        when(competitionService.getById(competitionId)).thenReturn(competition);
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(RestResult.restSuccess(competitionSummary));
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(newOrganisationResource().withName("Partner Org 1", "Partner Org 2").build(2));
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(teamStatus);
        List<ProjectUserResource> projectUsers = newProjectUserResource().with(id(999L)).withUserName("Dave Smith").
                withRoleName(PROJECT_MANAGER.getName()).build(1);

        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
    }


    @Override
    protected ProjectMonitoringOfficerController supplyControllerUnderTest() {
        return new ProjectMonitoringOfficerController();
    }
}