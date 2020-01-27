package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.transactional.RoleProfileStatusService;
import org.junit.Test;
import org.mockito.Mock;

import javax.ws.rs.core.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RoleProfileStatusControllerTest extends BaseControllerMockMVCTest<RoleProfileStatusController> {

    @Mock
    private RoleProfileStatusService roleProfileStatusService;

    @Override
    protected RoleProfileStatusController supplyControllerUnderTest() {
        return new RoleProfileStatusController();
    }

    @Test
    public void getUserStatus() throws Exception {
        long userId = 1L;
        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource().withUserId(userId).build();

        when(roleProfileStatusService.findByUserId(1L)).thenReturn(serviceSuccess(newRoleProfileStatusResource().withUserId(userId).build()));

        mockMvc.perform(get("/user/{id}/role-profile-status", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(roleProfileStatusResource)));
    }

    @Test
    public void updateUserStatus() throws Exception {
        long userId = 1L;
        RoleProfileStatusResource roleProfileStatusResource = new RoleProfileStatusResource();

        when(roleProfileStatusService.updateUserStatus(userId, roleProfileStatusResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/{id}/role-profile-status", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleProfileStatusResource)))
                .andExpect(status().isOk());
    }
}