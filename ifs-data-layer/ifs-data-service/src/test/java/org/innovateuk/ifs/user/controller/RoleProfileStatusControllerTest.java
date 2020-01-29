package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.transactional.RoleProfileStatusService;
import org.junit.Test;
import org.mockito.Mock;

import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    public void findByUserId() throws Exception {
        long userId = 1L;
        List<RoleProfileStatusResource> roleProfileStatusResources = newRoleProfileStatusResource().withUserId(userId).build(1);

        when(roleProfileStatusService.findByUserId(userId)).thenReturn(serviceSuccess(roleProfileStatusResources));

        mockMvc.perform(get("/user/{id}/role-profile-status", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(roleProfileStatusResources)));
    }

    @Test
    public void findByUserIdAndProfileRole() throws Exception {
        long userId = 1L;
        ProfileRole profileRole = ProfileRole.ASSESSOR;
        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource().withUserId(userId).build();

        when(roleProfileStatusService.findByUserIdAndProfileRole(userId, profileRole)).thenReturn(serviceSuccess(roleProfileStatusResource));

        mockMvc.perform(get("/user/{id}/role-profile-status/{profileRole}", userId, profileRole))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(roleProfileStatusResource)));
    }

    @Test
    public void updateUserStatus() throws Exception {
        long userId = 1L;
        RoleProfileStatusResource roleProfileStatusResource = new RoleProfileStatusResource();

        when(roleProfileStatusService.updateUserStatus(anyLong(), any(RoleProfileStatusResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/{id}/role-profile-status", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(roleProfileStatusResource)))
                .andExpect(status().isOk());
    }
}