package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.innovateuk.ifs.user.transactional.RoleProfileStatusService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RoleProfileStatusControllerTest extends BaseControllerMockMVCTest<RoleProfileStatusController> {

    @Mock
    private RoleProfileStatusService roleProfileStatusServiceMock;

    @Override
    protected RoleProfileStatusController supplyControllerUnderTest() {
        return new RoleProfileStatusController();
    }

    @Test
    public void findByUserId() throws Exception {
        long userId = 1L;
        List<RoleProfileStatusResource> roleProfileStatusResources = newRoleProfileStatusResource().withUserId(userId).build(1);

        when(roleProfileStatusServiceMock.findByUserId(userId)).thenReturn(serviceSuccess(roleProfileStatusResources));

        mockMvc.perform(get("/user/{id}/role-profile-status", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(roleProfileStatusResources)));
    }

    @Test
    public void findByUserIdAndProfileRole() throws Exception {
        long userId = 1L;
        ProfileRole profileRole = ProfileRole.ASSESSOR;
        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource().withUserId(userId).build();

        when(roleProfileStatusServiceMock.findByUserIdAndProfileRole(userId, profileRole)).thenReturn(serviceSuccess(roleProfileStatusResource));

        mockMvc.perform(get("/user/{id}/role-profile-status/{profileRole}", userId, profileRole))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(roleProfileStatusResource)));
    }

    @Test
    public void updateUserStatus() throws Exception {
        long userId = 1L;
        RoleProfileStatusResource roleProfileStatusResource = new RoleProfileStatusResource();

        when(roleProfileStatusServiceMock.updateUserStatus(anyLong(), any(RoleProfileStatusResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/{id}/role-profile-status", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(roleProfileStatusResource)))
                .andExpect(status().isOk());
    }

    @Test
    public void getByRoleProfileStatus() throws Exception {
        RoleProfileState roleProfileState = RoleProfileState.ACTIVE;
        ProfileRole profileRole = ProfileRole.ASSESSOR;
        String filter = "filter";
        UserPageResource userPageResource = new UserPageResource();

        when(roleProfileStatusServiceMock.findByRoleProfile(eq(roleProfileState), eq(profileRole), eq(filter), any(PageRequest.class)))
                .thenReturn(serviceSuccess(userPageResource));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/user/role-profile-status/{roleProfileState}/{profileRole}", roleProfileState, profileRole)
                .param("filter", filter))
                .andExpect(status().isOk());
    }
}