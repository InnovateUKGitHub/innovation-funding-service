package org.innovateuk.ifs.management.roleprofile.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.user.resource.RoleProfileState;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.RoleProfileStatusRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * A mock MVC test for user management controller
 */
public class AssessorManagementControllerTest extends BaseControllerMockMVCTest<AssessorManagementController> {

    @Override
    protected AssessorManagementController supplyControllerUnderTest() {
        return new AssessorManagementController();
    }

    @Mock
    private UserRestService userRestService;

    @Mock
    private RoleProfileStatusRestService roleProfileStatusRestService;

    @Mock
    private AssessorRestService assessorRestService;

    @Test
    public void viewFindExternalUsers() throws Exception {

        long userId = 1L;
        long modifiedId = 2L;

        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource().withModifiedBy(modifiedId).build();
        UserResource userResource = newUserResource().build();

        when(roleProfileStatusRestService.findByUserIdAndProfileRole(userId, ASSESSOR)).thenReturn(restSuccess(roleProfileStatusResource));
        when(userRestService.retrieveUserById(modifiedId)).thenReturn(restSuccess(userResource));
        when(assessorRestService.hasApplicationsAssigned(userId)).thenReturn(restSuccess(TRUE));

        mockMvc.perform(get("/admin/user/{userId}/role-profile",userId))
                .andExpect(status().isOk())
                .andExpect(view().name("roleprofile/role-profile-details"));
    }

    @Test
    public void viewStatus() throws Exception {
        long userId = 1L;
        long modifiedId = 2L;
        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource()
                .withUserId(userId)
                .withProfileRole(ASSESSOR)
                .withRoleProfileState(RoleProfileState.ACTIVE)
                .withModifiedBy(modifiedId)
                .build();
        UserResource userResource = newUserResource().withId(modifiedId).build();

        when(roleProfileStatusRestService.findByUserIdAndProfileRole(userId, ASSESSOR)).thenReturn(restSuccess(roleProfileStatusResource));
        when(userRestService.retrieveUserById(modifiedId)).thenReturn(restSuccess(userResource));
        when(assessorRestService.hasApplicationsAssigned(userId)).thenReturn(restSuccess(TRUE));

        mockMvc.perform(get("/admin/user/{userId}/role-profile/status",userId))
                .andExpect(status().isOk())
                .andExpect(view().name("roleprofile/change-status"));
    }

    @Test
    public void UpdateStatus_success() throws Exception {
        long userId = 1L;
        String roleProfileState = "ACTIVE";
        RoleProfileStatusResource roleProfileStatusResource = new RoleProfileStatusResource(userId, ASSESSOR, RoleProfileState.ACTIVE, "");

        when(roleProfileStatusRestService.updateUserStatus(anyLong(), any(RoleProfileStatusResource.class))).thenReturn(restSuccess());

        mockMvc.perform(post("/admin/user/{userId}/role-profile/status",userId).
                param("roleProfileState", roleProfileState))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/admin/user/%d/active", userId)));
    }

    @Test
    public void UpdateStatus_failure() throws Exception {
        long userId = 1L;
        long modifiedId = 2L;
        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource()
                .withUserId(userId)
                .withProfileRole(ASSESSOR)
                .withRoleProfileState(RoleProfileState.UNAVAILABLE)
                .withModifiedBy(modifiedId)
                .build();
        UserResource userResource = newUserResource().withId(modifiedId).build();

        when(roleProfileStatusRestService.findByUserIdAndProfileRole(userId, ASSESSOR)).thenReturn(restSuccess(roleProfileStatusResource));
        when(userRestService.retrieveUserById(modifiedId)).thenReturn(restSuccess(userResource));
        when(assessorRestService.hasApplicationsAssigned(userId)).thenReturn(restSuccess(TRUE));

        mockMvc.perform(post("/admin/user/{userId}/role-profile/status",userId).
                param("roleProfileState", roleProfileStatusResource.getRoleProfileState().name()))
                .andExpect(status().isOk())
                .andExpect(view().name("roleprofile/change-status"));
    }

}