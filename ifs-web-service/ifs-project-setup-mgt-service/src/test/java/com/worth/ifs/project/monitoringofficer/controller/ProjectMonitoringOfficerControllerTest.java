package com.worth.ifs.project.monitoringofficer.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.monitoringofficer.controller.ProjectMonitoringOfficerController;
import com.worth.ifs.monitoringofficer.form.ProjectMonitoringOfficerForm;
import com.worth.ifs.monitoringofficer.viewmodel.ProjectMonitoringOfficerViewModel;
import com.worth.ifs.project.builder.ProjectResourceBuilder;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.*;
import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static com.worth.ifs.project.builder.ProjectLeadStatusResourceBuilder.newProjectLeadStatusResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static com.worth.ifs.project.constant.ProjectActivityStates.PENDING;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page
        assertTrue(model.isExistingMonitoringOfficer());
        assertTrue(model.isDisplayMonitoringOfficerAssignedMessage());
        assertFalse(model.isDisplayAssignMonitoringOfficerButton());
        assertTrue(model.isDisplayChangeMonitoringOfficerLink());
        assertFalse(model.isEditMode());
        assertTrue(model.isReadOnly());

        // assert the form for the MO details have been pre-populated ok
        assertMonitoringOfficerFormPrepopulatedFromExistingMonitoringOfficer(modelMap);
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

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page, especially
        // with regards to the fact that if no MO has yet been assigned, the default behaviour will be to start in
        // edit mode
        assertFalse(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerButton());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details is not prepopulated
        assertMonitoringOfficerFormNotPrepopulated(modelMap);
    }

    @Test
    public void testViewMonitoringOfficerPageButProjectDetailsNotYetSubmitted() throws Exception {

        ProjectResource project = projectBuilder.build();

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(PENDING).
                        build()).
                build();

        when(projectService.getById(123L)).thenReturn(project);
        when(projectService.getProjectTeamStatus(123L, Optional.empty())).thenReturn(teamStatus);

        mockMvc.perform(get("/project/123/monitoring-officer")).
                andExpect(view().name("forbidden")).
                andExpect(status().isForbidden());
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
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page
        assertTrue(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerButton());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details have been pre-populated ok
        assertMonitoringOfficerFormPrepopulatedFromExistingMonitoringOfficer(modelMap);
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

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page
        assertFalse(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerButton());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details is not prepopulated
        assertMonitoringOfficerFormNotPrepopulated(modelMap);
    }

    @Test
    public void testEditMonitoringOfficerPageButProjectDetailsNotYetSubmitted() throws Exception {

        ProjectResource project = projectBuilder.build();

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(PENDING).
                        build()).
                build();

        when(projectService.getById(123L)).thenReturn(project);
        when(projectService.getProjectTeamStatus(123L, Optional.empty())).thenReturn(teamStatus);

        mockMvc.perform(get("/project/123/monitoring-officer/edit")).
                andExpect(view().name("forbidden")).
                andExpect(status().isForbidden());
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
        ProjectMonitoringOfficerViewModel model = (ProjectMonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the form for the MO details have been pre-populated ok
        assertMonitoringOfficerFormPrepopulatedFromExistingMonitoringOfficer(modelMap);
    }



    @Test
    public void testConfirmMonitoringOfficerButBindingErrorOccurs() throws Exception {

        ProjectResource project = projectBuilder.build();

        when(projectService.updateMonitoringOfficer(123L, "First", "Last", "asdf@asdf.com", "1234567890")).thenReturn(serviceSuccess());
        setupViewMonitoringOfficerTestExpectations(project, false);

        MvcResult result = mockMvc.perform(post("/project/123/monitoring-officer/confirm").
                param("firstName", "").
                param("lastName", "").
                param("emailAddress", "asdf").
                param("phoneNumber", "")).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        ProjectMonitoringOfficerViewModel model = (ProjectMonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page
        assertFalse(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerButton());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details have been retained from the ones in error
        ProjectMonitoringOfficerForm form = (ProjectMonitoringOfficerForm) modelMap.get("form");
        assertEquals("", form.getFirstName());
        assertEquals("", form.getLastName());
        assertEquals("asdf", form.getEmailAddress());
        assertEquals("", form.getPhoneNumber());

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(6, bindingResult.getFieldErrorCount());
        assertEquals("NotEmpty", bindingResult.getFieldError("firstName").getCode());
        assertEquals("NotEmpty", bindingResult.getFieldError("lastName").getCode());
        assertEquals("Email", bindingResult.getFieldError("emailAddress").getCode());

        List<FieldError> phoneNumberErrors = new ArrayList<>(bindingResult.getFieldErrors("phoneNumber"));
        phoneNumberErrors.sort((o1, o2) -> o1.getCode().compareTo(o2.getCode()));

        assertEquals("NotEmpty", phoneNumberErrors.get(0).getCode());
        assertEquals("Pattern", phoneNumberErrors.get(1).getCode());
        assertEquals("Size", phoneNumberErrors.get(2).getCode());
    }

    @Test
    public void testConfirmMonitoringOfficerButProjectDetailsNotYetSubmitted() throws Exception {

        ProjectResource project = projectBuilder.build();

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(PENDING).
                        build()).
                build();

        when(projectService.getById(123L)).thenReturn(project);
        when(projectService.getProjectTeamStatus(123L, Optional.empty())).thenReturn(teamStatus);

        mockMvc.perform(post("/project/123/monitoring-officer/confirm").
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@asdf.com").
                param("phoneNumber", "12345")).
                andExpect(view().name("forbidden")).
                andExpect(status().isForbidden());
    }

    @Test
    public void testAssignMonitoringOfficer() throws Exception {

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        build()).
                build();

        when(projectService.getById(123L)).thenReturn(projectBuilder.build());
        when(projectService.getProjectTeamStatus(123L, Optional.empty())).thenReturn(teamStatus);

        when(projectService.updateMonitoringOfficer(123L, "First", "Last", "asdf@asdf.com", "1234567890")).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/123/monitoring-officer/assign").
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@asdf.com").
                param("phoneNumber", "1234567890")).
                andExpect(view().name("redirect:/project/123/monitoring-officer"));

        verify(projectService).updateMonitoringOfficer(123L, "First", "Last", "asdf@asdf.com", "1234567890");
    }

    @Test
    public void testAssignMonitoringOfficerButDataLayerErrorOccurs() throws Exception {

        ProjectResource project = projectBuilder.build();

        ServiceResult<Void> failureResponse = serviceFailure(new Error(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED));

        when(projectService.updateMonitoringOfficer(123L, "First2", "Last2", "asdf2@asdf.com", "0987654321")).thenReturn(failureResponse);
        setupViewMonitoringOfficerTestExpectations(project, false);

        MvcResult result = mockMvc.perform(post("/project/123/monitoring-officer/assign").
                param("firstName", "First2").
                param("lastName", "Last2").
                param("emailAddress", "asdf2@asdf.com").
                param("phoneNumber", "0987654321")).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        ProjectMonitoringOfficerViewModel model = (ProjectMonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page
        assertFalse(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerButton());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details have been retained from the ones that resulted in error
        ProjectMonitoringOfficerForm form = (ProjectMonitoringOfficerForm) modelMap.get("form");
        assertEquals("First2", form.getFirstName());
        assertEquals("Last2", form.getLastName());
        assertEquals("asdf2@asdf.com", form.getEmailAddress());
        assertEquals("0987654321", form.getPhoneNumber());

        assertEquals(1, form.getObjectErrors().size());
        assertEquals(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED.getErrorKey(),
                form.getObjectErrors().get(0).getCode());
    }

    @Test
    public void testAssignMonitoringOfficerButProjectDetailsNotYetSubmitted() throws Exception {

        ProjectResource project = projectBuilder.build();

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectLeadStatusResource().
                        withProjectDetailsStatus(PENDING).
                        build()).
                build();

        when(projectService.getById(123L)).thenReturn(project);
        when(projectService.getProjectTeamStatus(123L, Optional.empty())).thenReturn(teamStatus);

        mockMvc.perform(post("/project/123/monitoring-officer/assign").
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@asdf.com").
                param("phoneNumber", "12345")).
                andExpect(view().name("forbidden")).
                andExpect(status().isForbidden());
    }

    @Test
    public void testAssignMonitoringOfficerButBindingErrorOccurs() throws Exception {

        ProjectResource project = projectBuilder.build();

        when(projectService.updateMonitoringOfficer(123L, "First", "Last", "asdf@asdf.com", "1234567890")).thenReturn(serviceSuccess());
        setupViewMonitoringOfficerTestExpectations(project, false);

        MvcResult result = mockMvc.perform(post("/project/123/monitoring-officer/assign").
                param("firstName", "").
                param("lastName", "").
                param("emailAddress", "asdf").
                param("phoneNumber", "ADFS")).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        ProjectMonitoringOfficerViewModel model = (ProjectMonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page
        assertFalse(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerButton());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details have been retained from the ones in error
        ProjectMonitoringOfficerForm form = (ProjectMonitoringOfficerForm) modelMap.get("form");
        assertEquals("", form.getFirstName());
        assertEquals("", form.getLastName());
        assertEquals("asdf", form.getEmailAddress());
        assertEquals("ADFS", form.getPhoneNumber());

        BindingResult bindingResult = form.getBindingResult();

        assertEquals(5, bindingResult.getFieldErrorCount());
        assertEquals("NotEmpty", bindingResult.getFieldError("firstName").getCode());
        assertEquals("NotEmpty", bindingResult.getFieldError("lastName").getCode());
        assertEquals("Email", bindingResult.getFieldError("emailAddress").getCode());

        List<FieldError> phoneNumberErrors = new ArrayList<>(bindingResult.getFieldErrors("phoneNumber"));
        phoneNumberErrors.sort((o1, o2) -> o1.getCode().compareTo(o2.getCode()));

        assertEquals("Pattern", phoneNumberErrors.get(0).getCode());
        assertEquals("Size", phoneNumberErrors.get(1).getCode());
    }

    private void assertMonitoringOfficerFormPrepopulatedFromExistingMonitoringOfficer(Map<String, Object> modelMap) {
        ProjectMonitoringOfficerForm form = (ProjectMonitoringOfficerForm) modelMap.get("form");
        assertEquals("First", form.getFirstName());
        assertEquals("Last", form.getLastName());
        assertEquals("asdf@asdf.com", form.getEmailAddress());
        assertEquals("1234567890", form.getPhoneNumber());
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
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId)).thenReturn(competitionSummary);
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(newOrganisationResource().withName("Partner Org 1", "Partner Org 2").build(2));
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(teamStatus);
        List<ProjectUserResource> projectUsers = newProjectUserResource().with(id(999L)).withUserName("Dave Smith").
                withRoleName(PROJECT_MANAGER.getName()).build(1);

        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
    }

    private void assertProjectDetailsPrepopulatedOk(ProjectMonitoringOfficerViewModel model) {
        assertEquals(Long.valueOf(123), model.getProjectId());
        assertEquals("My Project", model.getProjectTitle());
        assertEquals(competitionSummary, model.getCompetitionSummary());
        assertEquals("Some Area", competition.getInnovationAreaName());
        assertEquals("Dave Smith", model.getProjectManagerName());
        assertEquals(asList("Line 1", "Line 3", "Line 4", "Line 5"), model.getPrimaryAddressLines());
        assertEquals(asList("Partner Org 1", "Partner Org 2"), model.getPartnerOrganisationNames());
        assertEquals(LocalDate.of(2017, 01, 05), model.getTargetProjectStartDate());
    }

    private void assertMonitoringOfficerFormNotPrepopulated(Map<String, Object> modelMap) {
        ProjectMonitoringOfficerForm form = (ProjectMonitoringOfficerForm) modelMap.get("form");
        assertNull(form.getFirstName());
        assertNull(form.getLastName());
        assertNull(form.getEmailAddress());
        assertNull(form.getPhoneNumber());
    }

    @Override
    protected ProjectMonitoringOfficerController supplyControllerUnderTest() {
        return new ProjectMonitoringOfficerController();
    }
}
