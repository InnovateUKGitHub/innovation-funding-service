package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.form.LegacyMonitoringOfficerForm;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.LegacyMonitoringOfficerViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
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
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

    private CompetitionResource competition = newCompetitionResource().
            withInnovationAreaNames(CollectionFunctions.asLinkedSet("Some Area", "Some other area")).
            build();

    private CompetitionSummaryResource competitionSummary = newCompetitionSummaryResource().build();

    ProjectResourceBuilder projectBuilder = newProjectResource().
            withId(projectId).
            withName("My Project").
            withApplication(applicationId).
            withAddress(projectAddress).
            withCompetition(competitionId).
            withTargetStartDate(LocalDate.of(2017, 01, 05));

    @Mock
    private ProjectService projectService;

    @Mock
    private MonitoringOfficerRestService monitoringOfficerService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Before
    public void logInCompAdminUser() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());
    }

    @Test
    public void testViewMonitoringOfficerPage() throws Exception {

        ProjectResource project = projectBuilder.withProjectState(SETUP).build();

        boolean existingMonitoringOfficer = true;

        String url = "/project/123/monitoring-officer";

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        MvcResult result = mockMvc.perform(get(url)).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        LegacyMonitoringOfficerViewModel model = (LegacyMonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page
        assertTrue(model.isExistingMonitoringOfficer());
        assertTrue(model.isDisplayMonitoringOfficerAssignedMessage());
        assertFalse(model.isDisplayAssignMonitoringOfficerButton());
        assertTrue(model.isDisplayChangeMonitoringOfficerLink());
        assertFalse(model.isEditMode());
        assertTrue(model.isEditable());
        assertTrue(model.isReadOnly());

        // assert the form for the MO details have been pre-populated ok
        assertMonitoringOfficerFormPrepopulatedFromExistingMonitoringOfficer(modelMap);
        checkEditableFlagIsSetCorrectlyForSupportUser(url, false);
    }

    private void checkEditableFlagIsSetCorrectlyForSupportUser(String url, boolean post) throws Exception {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.SUPPORT)).build());
        MvcResult result = mockMvc.perform(post ? post(url) : get(url)).andReturn();
        Map<String, Object> modelMap = result.getModelAndView().getModel();
        LegacyMonitoringOfficerViewModel model = (LegacyMonitoringOfficerViewModel) modelMap.get("model");
        assertFalse(model.isEditable());
    }

    @Test
    public void testViewMonitoringOfficerPageWithNoExistingMonitoringOfficer() throws Exception {

        ProjectResource project = projectBuilder.build();

        boolean existingMonitoringOfficer = false;

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        String url = "/project/123/monitoring-officer";

        mockMvc.perform(get(url)).
                andExpect(redirectedUrl("/monitoring-officer/view-all")).
                andReturn();
    }

    private void assertMonitoringOfficerFormPrepopulatedFromExistingMonitoringOfficer(Map<String, Object> modelMap) {
        LegacyMonitoringOfficerForm form = (LegacyMonitoringOfficerForm) modelMap.get("form");
        assertEquals("First", form.getFirstName());
        assertEquals("Last", form.getLastName());
        assertEquals("asdf@asdf.com", form.getEmailAddress());
        assertEquals("1234567890", form.getPhoneNumber());
    }

    private void setupViewMonitoringOfficerTestExpectations(ProjectResource project, boolean existingMonitoringOfficer) {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withIsLeadPartner(true).
                        build()).
                build();

        if (existingMonitoringOfficer) {
            project.setProjectMonitoringOfficer(1L);
        }

        when(monitoringOfficerService.findMonitoringOfficerForProject(projectId)).thenReturn(existingMonitoringOfficer ? restSuccess(mo) : restFailure(NOT_FOUND));

        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummary));
        when(projectService.getLeadOrganisation(projectId)).thenReturn(newOrganisationResource().withName("Partner Org 1").build());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(newOrganisationResource().withName("Partner Org 1", "Partner Org 2").build(2));
        List<ProjectUserResource> projectUsers = newProjectUserResource().with(id(999L)).withUserName("Dave Smith").
                withRole(PROJECT_MANAGER).build(1);

        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
    }

    private void assertProjectDetailsPrepopulatedOk(LegacyMonitoringOfficerViewModel model) {
        assertEquals(Long.valueOf(123), model.getProjectId());
        assertEquals("My Project", model.getProjectTitle());
        assertEquals(competitionSummary, model.getCompetitionSummary());
        assertEquals(competition.getInnovationAreaNames(), CollectionFunctions.asLinkedSet("Some Area", "Some other area"));
        assertEquals("Dave Smith", model.getProjectManagerName());
        assertEquals(asList("Line 1", "Line 3", "Line 4", "Line 5"), model.getPrimaryAddressLines());
        assertEquals(asList("Partner Org 1", "Partner Org 2"), model.getPartnerOrganisationNames());
        assertEquals(LocalDate.of(2017, 01, 05), model.getTargetProjectStartDate());
        assertTrue(model.isEditable());
    }

    @Override
    protected LegacyMonitoringOfficerController supplyControllerUnderTest() {
        return new LegacyMonitoringOfficerController();
    }
}
