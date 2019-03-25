package org.innovateuk.ifs.project.monitoringofficer.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.monitoring.service.ProjectMonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerSearchByEmailForm;
import org.innovateuk.ifs.project.monitoringofficer.populator.MonitoringOfficerProjectsViewModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
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
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.MONITORING_OFFICER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class MonitoringOfficerControllerTest extends BaseControllerMockMVCTest<MonitoringOfficerController> {

    @Mock
    private MonitoringOfficerProjectsViewModelPopulator modelPopulator;

    @Mock
    private ProjectMonitoringOfficerRestService projectMonitoringOfficerRestService;

    @Mock
    private UserService userService;

    private static final String EMAIL_ADDRESS = "emailAddress";

    @Before
    public void logInCompAdminUser() {
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(COMP_ADMIN)).build());
    }

    @Test
    public void testMonitoringOfficerSearchByEmailUrlFormAndView() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/monitoring-officer/search-by-email"))
                .andReturn();

        assertThat(mvcResult.getModelAndView().getModel().get("form"), instanceOf(MonitoringOfficerSearchByEmailForm.class));
        assertEquals("project/monitoring-officer/search-by-email", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void testMonitoringOfficerCreateUrlWhenUserNotFound() throws Exception {
        when(userService.findUserByEmail(anyString())).thenReturn(empty());
        MvcResult mvcResult = mockMvc.perform(post("/monitoring-officer/create")
                .param(EMAIL_ADDRESS, "123@test.com"))
                .andReturn();

        assertEquals("project/monitoring-officer/create", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void testMonitoringOfficerCreateUrlWhenUserIsFoundButIsNotMonitoringOfficer() throws Exception {
        Optional<UserResource> userResourceOptional = of(newUserResource()
                .withEmail("123@test.com")
                .withId(999L)
                .withRoleGlobal(COMP_ADMIN)
                .build());
        when(userService.findUserByEmail(anyString())).thenReturn(userResourceOptional);
        MvcResult mvcResult = mockMvc.perform(post("/monitoring-officer/create")
                .param("emailAddress", "123@test.com"))
                .andReturn();

        assertEquals("project/monitoring-officer/assign-role", mvcResult.getModelAndView().getViewName());
    }

    @Test
    public void testMonitoringOfficerCreateUrlWhenUserIsFoundAndIsMonitoringOfficer() throws Exception {
        Optional<UserResource> userResourceOptional = of(newUserResource()
                .withEmail("123@test.com")
                .withId(999L)
                .withRoleGlobal(MONITORING_OFFICER)
                .build());
        when(userService.findUserByEmail(anyString())).thenReturn(userResourceOptional);
        MvcResult mvcResult = mockMvc.perform(post("/monitoring-officer/create")
                .param("emailAddress", "123@test.com"))
                .andReturn();

        assertEquals("redirect:/monitoring-officer/999/projects", mvcResult.getModelAndView().getViewName());
    }

    @Override
    protected MonitoringOfficerController supplyControllerUnderTest() {
        return new MonitoringOfficerController();
    }
}
