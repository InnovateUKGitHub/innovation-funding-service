package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.transactional.RoleProfileStatusService;
import org.junit.Test;
import org.mockito.Mock;

import javax.ws.rs.core.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.RoleProfileStatusResourceDocs.roleProfileStatusResourceFields;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RoleProfileStatusControllerDocumentation extends BaseControllerMockMVCTest<RoleProfileStatusController> {

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

        when(roleProfileStatusService.findByUserId(1L)).thenReturn(serviceSuccess(roleProfileStatusResource));

        mockMvc.perform(get("/user/{id}/role-profile-status", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("roleProfileStatus/{method-name}",
                        responseFields(roleProfileStatusResourceFields)
                ));
    }

    @Test
    public void updateUserStatus() throws Exception {
        long userId = 1L;

        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource()
                .withUserId(userId)
                .withRoleProfileState(RoleProfileState.UNAVAILABLE)
                .withProfileRole(ProfileRole.ASSESSOR)
                .withDescription("Description")
                .build();

        when(roleProfileStatusService.updateUserStatus(userId, roleProfileStatusResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/{id}/role-profile-status", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleProfileStatusResource)))
                .andDo(document("roleProfileStatus/{method-name}"))
                .andExpect(status().isOk());
    }
}
