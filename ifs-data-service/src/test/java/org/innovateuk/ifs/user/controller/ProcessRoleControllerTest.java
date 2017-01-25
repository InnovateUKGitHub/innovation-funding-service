package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProcessRoleControllerTest extends BaseControllerMockMVCTest<ProcessRoleController> {

    @Override
    protected ProcessRoleController supplyControllerUnderTest() {
        return new ProcessRoleController();
    }

    @Test
    public void findProcessRoleByIdShouldReturnProcessRoleResource() throws Exception {
        String userName = "username";

        when(usersRolesServiceMock.getProcessRoleById(1L)).thenReturn(serviceSuccess(newProcessRoleResource().withUserName(userName).build()));

        mockMvc.perform(get("/processrole/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is(userName)));
    }

    @Test
    public void findByUserApplicationShouldReturnProcessRoleResource() throws Exception {
        String userName = "username";

        when(usersRolesServiceMock.getProcessRoleByUserIdAndApplicationId(1L,1L)).thenReturn(serviceSuccess(newProcessRoleResource().withUserName(userName).build()));

        mockMvc.perform(get("/processrole/findByUserApplication/{userId}/{applicationId}", 1, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is(userName)));
    }

    @Test
    public void findByUserApplicationShouldReturnProcessRoleResourceList() throws Exception {
        String userName = "username";

        when(usersRolesServiceMock.getProcessRolesByApplicationId(1L)).thenReturn(serviceSuccess(newProcessRoleResource().withUserName(userName).build(3)));

        mockMvc.perform(get("/processrole/findByApplicationId/{applicationId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].userName", is(userName)))
                .andExpect(jsonPath("$[1].userName", is(userName)))
                .andExpect(jsonPath("$[2].userName", is(userName)));
    }

    @Test
    public void findByUserShouldReturnProcessRoleResourceList() throws Exception {
        String userName = "username";

        when(usersRolesServiceMock.getProcessRolesByUserId(1L)).thenReturn(serviceSuccess(newProcessRoleResource().withUserName(userName).build(3)));

        mockMvc.perform(get("/processrole/findByUserId/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].userName", is(userName)))
                .andExpect(jsonPath("$[1].userName", is(userName)))
                .andExpect(jsonPath("$[2].userName", is(userName)));
    }

    @Test
    public void findAssignableShouldReturnProcessRoleResourceList() throws Exception {
        String userName = "username";

        when(usersRolesServiceMock.getAssignableProcessRolesByApplicationId(1L)).thenReturn(serviceSuccess(newProcessRoleResource().withUserName(userName).build(3)));

        mockMvc.perform(get("/processrole/findAssignable/{applicationId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].userName", is(userName)))
                .andExpect(jsonPath("$[1].userName", is(userName)))
                .andExpect(jsonPath("$[2].userName", is(userName)));
    }

    @Test
    public void findByProcessRoleShouldReturnApplicationResourceList() throws Exception {
        String appName = "app1";

        when(applicationServiceMock.findByProcessRole(1L)).thenReturn(serviceSuccess(newApplicationResource().withName(appName).build()));

        mockMvc.perform(get("/processrole/{id}/application", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(appName)));
    }
}