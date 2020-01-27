package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.transactional.RoleProfileStatusService;
import org.junit.Test;
import org.mockito.Mock;

import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.RoleProfileStatusResourceDocs.roleProfileStatusResourceFields;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

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
    public void findByUserId() throws Exception {
        long userId = 1L;
        List<RoleProfileStatusResource> roleProfileStatusResources = newRoleProfileStatusResource()
                .withUserId(userId)
                .withRoleProfileState(RoleProfileState.UNAVAILABLE)
                .withProfileRole(ProfileRole.ASSESSOR)
                .withDescription("Description")
                .build(1);

        when(roleProfileStatusService.findByUserId(userId)).thenReturn(serviceSuccess(roleProfileStatusResources));

        mockMvc.perform(get("/user/{userId}/role-profile-status", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("roleProfileStatus/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Id of the user")
                        ),
                        responseFields(fieldWithPath("[]").description("List of Project Users the user is allowed to see"))
                                .andWithPrefix("[].", roleProfileStatusResourceFields)
                ));
    }

    @Test
    public void findByUserIdAndProfileRole() throws Exception {
        long userId = 1L;
        ProfileRole profileRole = ProfileRole.ASSESSOR;
        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource().withUserId(userId).build();

        when(roleProfileStatusService.findByUserIdAndProfileRole(userId, profileRole)).thenReturn(serviceSuccess(roleProfileStatusResource));

        mockMvc.perform(get("/user/{userId}/role-profile-status/{profileRole}", userId, profileRole)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("roleProfileStatus/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Id of the user"),
                                parameterWithName("profileRole").description("role of the user")
                        ),
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

        when(roleProfileStatusService.updateUserStatus(anyLong(), any(RoleProfileStatusResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/{userId}/role-profile-status", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleProfileStatusResource)))
                .andExpect(status().isOk())
                .andDo(document("roleProfileStatus/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Id of the user"))));
    }
}
