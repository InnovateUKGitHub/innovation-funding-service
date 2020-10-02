package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.service.MonitoringOfficerRegistrationRestService;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerCreateResource;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerAssignRoleForm;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerSearchByEmailForm;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerAssignRoleViewModelPopulator;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerAssignRoleViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class MonitoringOfficerControllerTest extends BaseControllerMockMVCTest<MonitoringOfficerController> {


    @Mock
    private MonitoringOfficerRegistrationRestService monitoringOfficerRegistrationRestServiceMock;
    @Mock
    private UserService userService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private MonitoringOfficerAssignRoleViewModelPopulator monitoringOfficerAssignRoleViewModelPopulator;

    private static final String EMAIL_ADDRESS = "emailAddress";

    @Before
    public void logInCompAdminUser() {
        setLoggedInUser(newUserResource().withRoleGlobal(COMP_ADMIN).build());
    }

    @Test
    public void searchByEmailGet() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/monitoring-officer/search-by-email"))
                .andReturn();

        assertThat(mvcResult.getModelAndView().getModel().get("form"), instanceOf(MonitoringOfficerSearchByEmailForm.class));
        assertEquals("project/monitoring-officer/search-by-email", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void searchByEmailPostWhenUserNotFound() throws Exception {
        when(userService.findUserByEmail(anyString())).thenReturn(empty());
        MvcResult mvcResult = mockMvc.perform(post("/monitoring-officer/search-by-email")
                .param(EMAIL_ADDRESS, "123@test.com"))
                .andReturn();

        assertEquals("redirect:/monitoring-officer/create/" + "123@test.com", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void searchByEmailPostWhenUserIsFoundButIsNotMonitoringOfficer() throws Exception {
        long userId = 999L;
        String emailAddress = "123@test.com";
        Optional<UserResource> userResourceOptional = of(newUserResource()
                .withEmail(emailAddress)
                .withId(userId)
                .withRoleGlobal(COMP_ADMIN)
                .build());
        when(userService.findUserByEmail(anyString())).thenReturn(userResourceOptional);
        MonitoringOfficerAssignRoleViewModel viewModel = new MonitoringOfficerAssignRoleViewModel(userId,
                "firstName",
                "lastName",
                emailAddress,
                null);
        when(monitoringOfficerAssignRoleViewModelPopulator.populate(anyLong())).thenReturn(viewModel);
        MvcResult mvcResult = mockMvc.perform(post("/monitoring-officer/search-by-email")
                .param("emailAddress", emailAddress))
                .andReturn();

        assertEquals("redirect:/monitoring-officer/999/assign-role", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void searchByEmailPostWhenUserIsFoundAndIsMonitoringOfficer() throws Exception {
        Optional<UserResource> userResourceOptional = of(newUserResource()
                .withEmail("123@test.com")
                .withId(999L)
                .withRoleGlobal(MONITORING_OFFICER)
                .build());
        when(userService.findUserByEmail(anyString())).thenReturn(userResourceOptional);
        MvcResult mvcResult = mockMvc.perform(post("/monitoring-officer/search-by-email")
                .param("emailAddress", "123@test.com"))
                .andReturn();

        assertEquals("redirect:/monitoring-officer/999/projects", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void assignRoleGet() throws Exception {
        UserResource userResource = new UserResource();
        userResource.setId(999L);
        userResource.setEmail("test@test.test");
        when(userRestService.retrieveUserById(999L)).thenReturn(restSuccess(userResource));
        MonitoringOfficerAssignRoleViewModel viewModel = new MonitoringOfficerAssignRoleViewModel(userResource.getId(),
                "firstName",
                "lastName",
                userResource.getEmail(),
                "");
        when(monitoringOfficerAssignRoleViewModelPopulator.populate(anyLong())).thenReturn(viewModel);
        MvcResult mvcResult = mockMvc.perform(get("/monitoring-officer/" + userResource.getId() + "/assign-role"))
                .andReturn();

        assertThat(mvcResult.getModelAndView().getModel().get("form"), instanceOf(MonitoringOfficerAssignRoleForm.class));
        assertThat(mvcResult.getModelAndView().getModel().get("model"), instanceOf(MonitoringOfficerAssignRoleViewModel.class));
        assertEquals("project/monitoring-officer/assign-role", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void assignRoleGetWhereUserHasPhoneNumber() throws Exception {
        UserResource userResource = new UserResource();
        userResource.setId(999L);
        userResource.setEmail("test@test.test");
        when(userRestService.retrieveUserById(999L)).thenReturn(restSuccess(userResource));
        MonitoringOfficerAssignRoleViewModel viewModel = new MonitoringOfficerAssignRoleViewModel(userResource.getId(),
                "firstName",
                "lastName",
                userResource.getEmail(),
                "0123456789");
        when(monitoringOfficerAssignRoleViewModelPopulator.populate(anyLong())).thenReturn(viewModel);
        MvcResult mvcResult = mockMvc.perform(get("/monitoring-officer/" + userResource.getId() + "/assign-role"))
                .andReturn();

        assertThat(mvcResult.getModelAndView().getModel().get("form"), instanceOf(MonitoringOfficerAssignRoleForm.class));
        assertThat(mvcResult.getModelAndView().getModel().get("model"), instanceOf(MonitoringOfficerAssignRoleViewModel.class));
        assertEquals("project/monitoring-officer/assign-role-without-edit", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void assignRoleGetWhenUserIsAlreadyMonitoringOfficer() throws Exception {
        UserResource userResource = newUserResource()
                .withId(999L)
                .withEmail("test@test.test")
                .withRolesGlobal(singletonList(MONITORING_OFFICER))
                .build();
        when(userRestService.retrieveUserById(999L)).thenReturn(restSuccess(userResource));
        MonitoringOfficerAssignRoleViewModel viewModel = new MonitoringOfficerAssignRoleViewModel(userResource.getId(),
                "firstName",
                "lastName",
                userResource.getEmail(),
                null);
        when(monitoringOfficerAssignRoleViewModelPopulator.populate(anyLong())).thenReturn(viewModel);
        MvcResult mvcResult = mockMvc.perform(get("/monitoring-officer/" + userResource.getId() + "/assign-role"))
                .andReturn();

        assertEquals("redirect:/monitoring-officer/999/projects", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void searchByEmailPostWhenValidationErrors() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/monitoring-officer/search-by-email"))
                .andReturn();

        assertEquals("project/monitoring-officer/search-by-email", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void assignRolePost() throws Exception {
        UserResource userResource = newUserResource()
                .withId(999L)
                .withPhoneNumber("01234567890")
                .build();
        when(userRestService.retrieveUserById(999L)).thenReturn(restSuccess(userResource));
        when(userService.updateDetails(anyLong(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean()))
                .thenReturn(ServiceResult.serviceSuccess(userResource));
        when(userRestService.grantRole(userResource.getId(), MONITORING_OFFICER, Optional.empty())).thenReturn(restSuccess());

        MvcResult mvcResult = mockMvc.perform(post("/monitoring-officer/" + userResource.getId() + "/assign-role")
                .param("phoneNumber", userResource.getPhoneNumber()))
                .andReturn();

        assertEquals("redirect:/monitoring-officer/999/projects", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void assignRoleWithoutEditPost() throws Exception {
        UserResource userResource = newUserResource()
                .withId(999L)
                .build();
        when(userRestService.retrieveUserById(999L)).thenReturn(restSuccess(userResource));
        when(userRestService.grantRole(userResource.getId(), MONITORING_OFFICER, Optional.empty())).thenReturn(restSuccess());

        MvcResult mvcResult = mockMvc
                .perform(post("/monitoring-officer/" + userResource.getId() + "/assign-role-without-edit"))
                .andReturn();

        assertEquals("redirect:/monitoring-officer/999/projects", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void assignRolePostWithValidationErrors() throws Exception {
        UserResource userResource = newUserResource()
                .withId(999L)
                .build();
        MvcResult mvcResult = mockMvc.perform(post("/monitoring-officer/" + userResource.getId() + "/assign-role"))
                .andReturn();

        assertEquals("project/monitoring-officer/assign-role", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void create() throws Exception {
        mockMvc.perform(get("/monitoring-officer/create/"+ EMAIL_ADDRESS))
                .andExpect(status().isOk())
                .andExpect(view().name("project/monitoring-officer/create-new"));
    }

    @Test
    public void createUser() throws Exception {

        when(monitoringOfficerRegistrationRestServiceMock.createMonitoringOfficer(any(MonitoringOfficerCreateResource.class)))
                .thenReturn(restSuccess());

        mockMvc.perform(post("/monitoring-officer/create")
                                .param("emailAddress", "test@test.com")
                                .param("firstName", "Steve")
                                .param("lastName", "Smith")
                                .param("phoneNumber", "01142222222"))
                .andExpect(status().is3xxRedirection());

        verify(monitoringOfficerRegistrationRestServiceMock).createMonitoringOfficer(any(MonitoringOfficerCreateResource.class));
    }

    @Test
    public void createUserInvalidData() throws Exception {
        mockMvc.perform(post("/monitoring-officer/create")
                                .param("emailAddress", "invalidEmailAddress")
                                .param("firstName", "Steve")
                                .param("lastName", "Smith")
                                .param("phoneNumber", "01142222222"))
                .andExpect(view().name("project/monitoring-officer/create-new"));  // failure view

        verifyZeroInteractions(monitoringOfficerRegistrationRestServiceMock);
    }

    @Override
    protected MonitoringOfficerController supplyControllerUnderTest() {
        return new MonitoringOfficerController();
    }
}
