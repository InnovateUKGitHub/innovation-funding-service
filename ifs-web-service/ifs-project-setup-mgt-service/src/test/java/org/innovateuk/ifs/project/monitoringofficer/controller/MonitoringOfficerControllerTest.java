package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerForm;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder.newMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.PENDING;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class MonitoringOfficerControllerTest extends BaseControllerMockMVCTest<MonitoringOfficerController> {

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

    @Override
    @Before
    public void setUp() {
        super.setUp();
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.COMP_ADMIN).build())).build());
    }

    @Test
    public void testViewMonitoringOfficerPage() throws Exception {

        ProjectResource project = projectBuilder.build();

        boolean existingMonitoringOfficer = true;

        String url = "/project/123/monitoring-officer";

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        MvcResult result = mockMvc.perform(get(url)).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        MonitoringOfficerViewModel model = (MonitoringOfficerViewModel) modelMap.get("model");

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
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.SUPPORT).build())).build());
        MvcResult result = mockMvc.perform(post ? post(url) : get(url)).andReturn();
        Map<String, Object> modelMap = result.getModelAndView().getModel();
        MonitoringOfficerViewModel model = (MonitoringOfficerViewModel) modelMap.get("model");
        assertFalse(model.isEditable());
    }

    @Test
    public void testViewMonitoringOfficerPageWithNoExistingMonitoringOfficer() throws Exception {

        ProjectResource project = projectBuilder.build();

        boolean existingMonitoringOfficer = false;

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        String url = "/project/123/monitoring-officer";

        MvcResult result = mockMvc.perform(get(url)).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        MonitoringOfficerViewModel model = (MonitoringOfficerViewModel) modelMap.get("model");

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
        assertTrue(model.isEditable());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details is not prepopulated
        assertMonitoringOfficerFormNotPrepopulated(modelMap);
        checkEditableFlagIsSetCorrectlyForSupportUser(url, false);
    }

    @Test
    public void testViewMonitoringOfficerPageButProjectDetailsNotYetSubmitted() throws Exception {

        ProjectResource project = projectBuilder.build();

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(PENDING).
                        withIsLeadPartner(true).
                        build()).
                build();

        when(projectService.getById(123L)).thenReturn(project);
        when(statusService.getProjectTeamStatus(123L, Optional.empty())).thenReturn(teamStatus);

        mockMvc.perform(get("/project/123/monitoring-officer")).
                andExpect(view().name("forbidden")).
                andExpect(status().isForbidden());
    }

    @Test
    public void testEditMonitoringOfficerPage() throws Exception {

        ProjectResource project = projectBuilder.build();

        boolean existingMonitoringOfficer = true;

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        String url = "/project/123/monitoring-officer/edit";

        MvcResult result = mockMvc.perform(get(url)).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        MonitoringOfficerViewModel model = (MonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page
        assertTrue(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerButton());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertTrue(model.isEditable());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details have been pre-populated ok
        assertMonitoringOfficerFormPrepopulatedFromExistingMonitoringOfficer(modelMap);
        checkEditableFlagIsSetCorrectlyForSupportUser(url, false);
    }

    @Test
    public void testEditMonitoringOfficerPageWithNoExistingMonitoringOfficer() throws Exception {

        ProjectResource project = projectBuilder.build();

        boolean existingMonitoringOfficer = false;

        setupViewMonitoringOfficerTestExpectations(project, existingMonitoringOfficer);

        String url = "/project/123/monitoring-officer/edit";

        MvcResult result = mockMvc.perform(get(url)).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        MonitoringOfficerViewModel model = (MonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page
        assertFalse(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerButton());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertTrue(model.isEditable());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details is not prepopulated
        assertMonitoringOfficerFormNotPrepopulated(modelMap);
        checkEditableFlagIsSetCorrectlyForSupportUser(url, false);
    }

    @Test
    public void testEditMonitoringOfficerPageButProjectDetailsNotYetSubmitted() throws Exception {

        ProjectResource project = projectBuilder.build();

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(PENDING).
                        withIsLeadPartner(true).
                        build()).
                build();

        when(projectService.getById(123L)).thenReturn(project);
        when(statusService.getProjectTeamStatus(123L, Optional.empty())).thenReturn(teamStatus);

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
        MonitoringOfficerViewModel model = (MonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the form for the MO details have been pre-populated ok
        assertMonitoringOfficerFormPrepopulatedFromExistingMonitoringOfficer(modelMap);
    }



    @Test
    public void testConfirmMonitoringOfficerButBindingErrorOccurs() throws Exception {

        ProjectResource project = projectBuilder.build();

        when(monitoringOfficerService.updateMonitoringOfficer(123L, "First", "Last", "asdf@asdf.com", "1234567890")).thenReturn(serviceSuccess());
        setupViewMonitoringOfficerTestExpectations(project, false);
        String url = "/project/123/monitoring-officer/confirm";
        MvcResult result = mockMvc.perform(post(url).
                param("firstName", "").
                param("lastName", "").
                param("emailAddress", "asdf").
                param("phoneNumber", "")).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        MonitoringOfficerViewModel model = (MonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page
        assertFalse(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerButton());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertTrue(model.isEditable());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details have been retained from the ones in error
        MonitoringOfficerForm form = (MonitoringOfficerForm) modelMap.get("form");
        assertEquals("", form.getFirstName());
        assertEquals("", form.getLastName());
        assertEquals("asdf", form.getEmailAddress());
        assertEquals("", form.getPhoneNumber());

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(8, bindingResult.getFieldErrorCount());

        assertTrue(bindingResult.getFieldErrors("firstName").stream().anyMatch(fieldError -> fieldError.getCode().equals("Size")));
        assertTrue(bindingResult.getFieldErrors("firstName").stream().anyMatch(fieldError -> fieldError.getCode().equals("NotEmpty")));

        assertTrue(bindingResult.getFieldErrors("lastName").stream().anyMatch(fieldError -> fieldError.getCode().equals("Size")));
        assertTrue(bindingResult.getFieldErrors("lastName").stream().anyMatch(fieldError -> fieldError.getCode().equals("NotEmpty")));

        assertEquals("Email", bindingResult.getFieldError("emailAddress").getCode());

        List<FieldError> phoneNumberErrors = new ArrayList<>(bindingResult.getFieldErrors("phoneNumber"));
        phoneNumberErrors.sort((o1, o2) -> o1.getCode().compareTo(o2.getCode()));

        assertEquals("NotEmpty", phoneNumberErrors.get(0).getCode());
        assertEquals("Pattern", phoneNumberErrors.get(1).getCode());
        assertEquals("Size", phoneNumberErrors.get(2).getCode());

        checkEditableFlagIsSetCorrectlyForSupportUser(url, true);
    }

    @Test
    public void testConfirmMonitoringOfficerButProjectDetailsNotYetSubmitted() throws Exception {

        ProjectResource project = projectBuilder.build();

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(PENDING).
                        withIsLeadPartner(true).
                        build()).
                build();

        when(projectService.getById(123L)).thenReturn(project);
        when(statusService.getProjectTeamStatus(123L, Optional.empty())).thenReturn(teamStatus);

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
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(COMPLETE).
                        withIsLeadPartner(true).
                        build()).
                build();

        when(projectService.getById(123L)).thenReturn(projectBuilder.build());
        when(statusService.getProjectTeamStatus(123L, Optional.empty())).thenReturn(teamStatus);

        when(monitoringOfficerService.updateMonitoringOfficer(123L, "First", "Last", "asdf@asdf.com", "1234567890")).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/123/monitoring-officer/assign").
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@asdf.com").
                param("phoneNumber", "1234567890")).
                andExpect(view().name("redirect:/project/123/monitoring-officer"));

        verify(monitoringOfficerService).updateMonitoringOfficer(123L, "First", "Last", "asdf@asdf.com", "1234567890");
    }

    @Test
    public void testAssignMonitoringOfficerButDataLayerErrorOccurs() throws Exception {

        ProjectResource project = projectBuilder.build();

        ServiceResult<Void> failureResponse = serviceFailure(new Error(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED));

        when(monitoringOfficerService.updateMonitoringOfficer(123L, "First", "Last", "asdf2@asdf.com", "0987654321")).thenReturn(failureResponse);
        setupViewMonitoringOfficerTestExpectations(project, false);
        String url = "/project/123/monitoring-officer/assign";
        MvcResult result = mockMvc.perform(post(url).
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf2@asdf.com").
                param("phoneNumber", "0987654321")).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        MonitoringOfficerViewModel model = (MonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page
        assertFalse(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerButton());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertTrue(model.isEditable());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details have been retained from the ones that resulted in error
        MonitoringOfficerForm form = (MonitoringOfficerForm) modelMap.get("form");
        assertEquals("First", form.getFirstName());
        assertEquals("Last", form.getLastName());
        assertEquals("asdf2@asdf.com", form.getEmailAddress());
        assertEquals("0987654321", form.getPhoneNumber());

        assertEquals(1, form.getObjectErrors().size());
        assertEquals(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED.getErrorKey(),
                form.getObjectErrors().get(0).getCode());
        checkEditableFlagIsSetCorrectlyForSupportUser(url, true);
    }

    @Test
    public void testAssignMonitoringOfficerButProjectDetailsNotYetSubmitted() throws Exception {

        ProjectResource project = projectBuilder.build();

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().
                        withProjectDetailsStatus(PENDING).
                        withIsLeadPartner(true).
                        build()).
                build();

        when(projectService.getById(123L)).thenReturn(project);
        when(statusService.getProjectTeamStatus(123L, Optional.empty())).thenReturn(teamStatus);

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

        when(monitoringOfficerService.updateMonitoringOfficer(123L, "First", "Last", "asdf@asdf.com", "1234567890")).thenReturn(serviceSuccess());
        setupViewMonitoringOfficerTestExpectations(project, false);
        String url = "/project/123/monitoring-officer/assign";

        MvcResult result = mockMvc.perform(post(url).
                param("firstName", "").
                param("lastName", "").
                param("emailAddress", "asdf").
                param("phoneNumber", "ADFS")).
                andExpect(view().name("project/monitoring-officer")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        MonitoringOfficerViewModel model = (MonitoringOfficerViewModel) modelMap.get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        // assert the various flags are correct for helping to drive what's visible on the page
        assertFalse(model.isExistingMonitoringOfficer());
        assertFalse(model.isDisplayMonitoringOfficerAssignedMessage());
        assertTrue(model.isDisplayAssignMonitoringOfficerButton());
        assertFalse(model.isDisplayChangeMonitoringOfficerLink());
        assertTrue(model.isEditMode());
        assertTrue(model.isEditable());
        assertFalse(model.isReadOnly());

        // assert the form for the MO details have been retained from the ones in error
        MonitoringOfficerForm form = (MonitoringOfficerForm) modelMap.get("form");
        assertEquals("", form.getFirstName());
        assertEquals("", form.getLastName());
        assertEquals("asdf", form.getEmailAddress());
        assertEquals("ADFS", form.getPhoneNumber());

        BindingResult bindingResult = form.getBindingResult();

        assertEquals(7, bindingResult.getFieldErrorCount());

        assertTrue(bindingResult.getFieldErrors("firstName").stream().anyMatch(fieldError -> fieldError.getCode().equals("Size")));
        assertTrue(bindingResult.getFieldErrors("firstName").stream().anyMatch(fieldError -> fieldError.getCode().equals("NotEmpty")));

        assertTrue(bindingResult.getFieldErrors("lastName").stream().anyMatch(fieldError -> fieldError.getCode().equals("Size")));
        assertTrue(bindingResult.getFieldErrors("lastName").stream().anyMatch(fieldError -> fieldError.getCode().equals("NotEmpty")));
        
        assertEquals("Email", bindingResult.getFieldError("emailAddress").getCode());

        List<FieldError> phoneNumberErrors = new ArrayList<>(bindingResult.getFieldErrors("phoneNumber"));
        phoneNumberErrors.sort((o1, o2) -> o1.getCode().compareTo(o2.getCode()));

        assertEquals("Pattern", phoneNumberErrors.get(0).getCode());
        assertEquals("Size", phoneNumberErrors.get(1).getCode());

        checkEditableFlagIsSetCorrectlyForSupportUser(url, true);
    }

    private void assertMonitoringOfficerFormPrepopulatedFromExistingMonitoringOfficer(Map<String, Object> modelMap) {
        MonitoringOfficerForm form = (MonitoringOfficerForm) modelMap.get("form");
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

        Optional<MonitoringOfficerResource> monitoringOfficerToUse = existingMonitoringOfficer ? Optional.of(mo) : Optional.empty();
        when(monitoringOfficerService.getMonitoringOfficerForProject(projectId)).thenReturn(monitoringOfficerToUse);

        when(projectService.getById(projectId)).thenReturn(project);
        when(applicationService.getById(applicationId)).thenReturn(application);
        when(competitionService.getById(competitionId)).thenReturn(competition);
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummary));
        when(projectService.getLeadOrganisation(projectId)).thenReturn(newOrganisationResource().withName("Partner Org 1").build());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(newOrganisationResource().withName("Partner Org 1", "Partner Org 2").build(2));
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(teamStatus);
        List<ProjectUserResource> projectUsers = newProjectUserResource().with(id(999L)).withUserName("Dave Smith").
                withRoleName(PROJECT_MANAGER.getName()).build(1);

        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
    }

    private void assertProjectDetailsPrepopulatedOk(MonitoringOfficerViewModel model) {
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

    private void assertMonitoringOfficerFormNotPrepopulated(Map<String, Object> modelMap) {
        MonitoringOfficerForm form = (MonitoringOfficerForm) modelMap.get("form");
        assertNull(form.getFirstName());
        assertNull(form.getLastName());
        assertNull(form.getEmailAddress());
        assertNull(form.getPhoneNumber());
    }

    @Override
    protected MonitoringOfficerController supplyControllerUnderTest() {
        return new MonitoringOfficerController();
    }
}
