package com.worth.ifs.project;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.controller.BaseControllerMockMVCTest;
import com.worth.ifs.project.builder.ProjectResourceBuilder;
import com.worth.ifs.project.controller.ProjectMonitoringOfficerController;
import com.worth.ifs.project.controller.form.ProjectMonitoringOfficerForm;
import com.worth.ifs.project.controller.viewmodel.ProjectMonitoringOfficerViewModel;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.RoleResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
            withInnovationAreaName("Some Area").
            build();

    private CompetitionSummaryResource competitionSummary = newCompetitionSummaryResource().build();

    ProjectResourceBuilder projectBuilder = newProjectResource().
            withId(projectId).
            withName("My Project").
            withApplication(applicationId).
            withProjectManager(999L).
            withAddress(projectAddress).
            withTargetStartDate(LocalDate.of(2017, 01, 05));

    @Test
    public void testViewMonitoringOfficerPage() throws Exception {

        ProjectResource project = projectBuilder.build();

        boolean existingMonitoringOfficer = true;

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        MvcResult result = mockMvc.perform(get("/project/123/monitoring-officer")).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        ProjectMonitoringOfficerViewModel model = (ProjectMonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertEquals(Long.valueOf(123), model.getProjectId());
        assertEquals("My Project", model.getProjectTitle());
        assertEquals(competitionSummary, model.getCompetitionSummary());
        assertEquals("Some Area", competition.getInnovationAreaName());
        assertEquals("Dave Smith", model.getProjectManagerName());
        assertEquals(asList("Line 1", "Line 3", "Line 4", "Line 5"), model.getPrimaryAddressLines());
        assertEquals(asList("Partner Org 1", "Partner Org 2"), model.getPartnerOrganisationNames());
        assertEquals(LocalDate.of(2017, 01, 05), model.getTargetProjectStartDate());

        // assert the various flags are correct for helping to drive what's visible on the page
        assertTrue(model.isExistingMonitoringOfficer());
        assertTrue(model.isDisplayMonitoringOfficerAssignedMessage());
        assertFalse(model.isDisplayAssignMonitoringOfficerLink());
        assertTrue(model.isDisplayChangeMonitoringOfficerLink());
        assertFalse(model.isEditMode());
        assertTrue(model.isReadOnly());

        // assert the form for the MO details have been pre-populated ok
        ProjectMonitoringOfficerForm form = (ProjectMonitoringOfficerForm) modelMap.get("form");
        assertEquals("First", form.getFirstName());
        assertEquals("Last", form.getLastName());
        assertEquals("asdf@asdf.com", form.getEmailAddress());
        assertEquals("1234567890", form.getPhoneNumber());
    }

    private void setupViewMonitoringOfficerTestExpectations(ProjectResource project, boolean existingMonitoringOfficer) {

        Optional<MonitoringOfficerResource> monitoringOfficerToUse = existingMonitoringOfficer ? Optional.of(mo) : Optional.empty();
        when(projectService.getMonitoringOfficerForProject(projectId)).thenReturn(monitoringOfficerToUse);

        when(projectService.getById(projectId)).thenReturn(project);
        when(applicationService.getById(applicationId)).thenReturn(application);
        when(competitionService.getById(competitionId)).thenReturn(competition);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId)).thenReturn(competitionSummary);
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(newOrganisationResource().withName("Partner Org 1", "Partner Org 2").build(2));

        RoleResource projectManagerRole = newRoleResource().withType(PROJECT_MANAGER).build();

        // TODO DW - Project Manager needs to be a Project User rather than a ProcessRole
        List<ProcessRoleResource> processRoles = newProcessRoleResource().with(id(999L)).withUserName("Dave Smith").
                withRole(projectManagerRole).build(1);

        when(processRoleService.findProcessRolesByApplicationId(project.getApplication())).thenReturn(processRoles);
    }

    @Test
    public void testViewMonitoringOfficerPageWithNoExistingMonitoringOfficer() throws Exception {

        ProjectResource project = projectBuilder.build();

        boolean existingMonitoringOfficer = false;

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        MvcResult result = mockMvc.perform(get("/project/123/monitoring-officer")).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        ProjectMonitoringOfficerViewModel model = (ProjectMonitoringOfficerViewModel) modelMap.get("model");

        // assert the various flags are correct for helping to drive what's visible on the page
        assertFalse(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertFalse(model.isDisplayAssignMonitoringOfficerLink());
        assertTrue(model.isDisplayChangeMonitoringOfficerLink());
        assertFalse(model.isEditMode());
        assertTrue(model.isReadOnly());

        // assert the form for the MO details is not prepopulated
        ProjectMonitoringOfficerForm form = (ProjectMonitoringOfficerForm) modelMap.get("form");
        assertNull(form.getFirstName());
        assertNull(form.getLastName());
        assertNull(form.getEmailAddress());
        assertNull(form.getPhoneNumber());
    }

    @Test
    public void testEditMonitoringOfficerPage() throws Exception {

        ProjectResource project = projectBuilder.build();

        boolean existingMonitoringOfficer = true;

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        MvcResult result = mockMvc.perform(get("/project/123/monitoring-officer/edit")).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        ProjectMonitoringOfficerViewModel model = (ProjectMonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertEquals(Long.valueOf(123), model.getProjectId());
        assertEquals("My Project", model.getProjectTitle());
        assertEquals(competitionSummary, model.getCompetitionSummary());
        assertEquals("Some Area", competition.getInnovationAreaName());
        assertEquals("Dave Smith", model.getProjectManagerName());
        assertEquals(asList("Line 1", "Line 3", "Line 4", "Line 5"), model.getPrimaryAddressLines());
        assertEquals(asList("Partner Org 1", "Partner Org 2"), model.getPartnerOrganisationNames());
        assertEquals(LocalDate.of(2017, 01, 05), model.getTargetProjectStartDate());

        // assert the various flags are correct for helping to drive what's visible on the page
        assertTrue(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerLink());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details have been pre-populated ok
        ProjectMonitoringOfficerForm form = (ProjectMonitoringOfficerForm) modelMap.get("form");
        assertEquals("First", form.getFirstName());
        assertEquals("Last", form.getLastName());
        assertEquals("asdf@asdf.com", form.getEmailAddress());
        assertEquals("1234567890", form.getPhoneNumber());
    }

    @Test
    public void testEditMonitoringOfficerPageWithNoExistingMonitoringOfficer() throws Exception {

        ProjectResource project = projectBuilder.build();

        boolean existingMonitoringOfficer = false;

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        MvcResult result = mockMvc.perform(get("/project/123/monitoring-officer/edit")).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        ProjectMonitoringOfficerViewModel model = (ProjectMonitoringOfficerViewModel) modelMap.get("model");

        // assert the various flags are correct for helping to drive what's visible on the page
        assertFalse(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerLink());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details is not prepopulated
        ProjectMonitoringOfficerForm form = (ProjectMonitoringOfficerForm) modelMap.get("form");
        assertNull(form.getFirstName());
        assertNull(form.getLastName());
        assertNull(form.getEmailAddress());
        assertNull(form.getPhoneNumber());
    }

    @Test
    public void testConfirmMonitoringOfficer() throws Exception {

        ProjectResource project = projectBuilder.build();

        setupViewMonitoringOfficerTestExpectations(project, false);

        MvcResult result = mockMvc.perform(post("/project/123/monitoring-officer/confirm").
                    param("firstName", "First").
                    param("lastName", "Last").
                    param("emailAddress", "asdf@asdf.com").
                    param("phoneNumber", "1234567890")).
                andExpect(view().name("project/monitoring-officer-confirm")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();

        // assert the form for the MO details have been pre-populated ok
        ProjectMonitoringOfficerForm form = (ProjectMonitoringOfficerForm) modelMap.get("form");
        assertEquals("First", form.getFirstName());
        assertEquals("Last", form.getLastName());
        assertEquals("asdf@asdf.com", form.getEmailAddress());
        assertEquals("1234567890", form.getPhoneNumber());
    }

    @Override
    protected ProjectMonitoringOfficerController supplyControllerUnderTest() {
        return new ProjectMonitoringOfficerController();
    }
}
