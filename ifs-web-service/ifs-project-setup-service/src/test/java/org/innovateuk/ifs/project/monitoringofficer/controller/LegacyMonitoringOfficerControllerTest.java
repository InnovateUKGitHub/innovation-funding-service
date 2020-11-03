package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.LegacyMonitoringOfficerViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 *
 **/
public class LegacyMonitoringOfficerControllerTest extends BaseControllerMockMVCTest<LegacyMonitoringOfficerController> {

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

    private CompetitionResource competition = newCompetitionResource()
            .withFundingType(FundingType.GRANT)
            .withInnovationAreaNames(CollectionFunctions.asLinkedSet("Some Area", "Some other area"))
            .build();

    private CompetitionSummaryResource competitionSummary = newCompetitionSummaryResource().build();

    ProjectResourceBuilder projectBuilder = newProjectResource().
            withId(projectId).
            withName("My Project").
            withApplication(applicationId).
            withAddress(projectAddress).
            withCompetition(competition.getId()).
            withTargetStartDate(LocalDate.of(2017, 01, 05));

    @Mock
    private ProjectService projectService;

    @Mock
    private StatusService statusService;

    @Mock
    private MonitoringOfficerRestService monitoringOfficerService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Test
    public void viewMonitoringOfficerPage() throws Exception {

        ProjectResource project = projectBuilder.build();

        boolean existingMonitoringOfficer = true;

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        MvcResult result = mockMvc.perform(get("/project/123/monitoring-officer")).
                andExpect(view().name("project/monitoring-officer")).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        LegacyMonitoringOfficerViewModel model = (LegacyMonitoringOfficerViewModel) modelMap.get("model");

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
        LegacyMonitoringOfficerViewModel model = (LegacyMonitoringOfficerViewModel) modelMap.get("model");
        Boolean readOnlyView = (Boolean) modelMap.get("readOnlyView");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        assertTrue(model.isMonitoringOfficerAssigned());
        assertTrue(readOnlyView);
    }

    private void assertProjectDetailsPrepopulatedOk(LegacyMonitoringOfficerViewModel model) {
        assertEquals(Long.valueOf(123), model.getProjectId());
        assertEquals("My Project", model.getProjectName());
        assertEquals(competition.getInnovationAreaNames(), CollectionFunctions.asLinkedSet("Some Area", "Some other area"));
    }

    private void setupViewMonitoringOfficerTestExpectations(ProjectResource project, boolean existingMonitoringOfficer) {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withIsLeadPartner(true).
                        build()).
                build();

        when(monitoringOfficerService.findMonitoringOfficerForProject(projectId)).thenReturn(existingMonitoringOfficer ? restSuccess(mo) : restFailure(HttpStatus.NOT_FOUND));

        when(projectService.getById(projectId)).thenReturn(project);
        when(applicationService.getById(applicationId)).thenReturn(application);
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummary));
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(newOrganisationResource().withName("Partner Org 1", "Partner Org 2").build(2));
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(teamStatus);
        List<ProjectUserResource> projectUsers = newProjectUserResource().with(id(999L)).withUserName("Dave Smith").
                withRole(PROJECT_MANAGER).build(1);

        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
    }

    @Override
    protected LegacyMonitoringOfficerController supplyControllerUnderTest() {
        return new LegacyMonitoringOfficerController();
    }
}