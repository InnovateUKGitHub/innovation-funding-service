package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.RoleProfileStatusRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

    @Test
    public void viewFindExternalUsers() throws Exception {

        long userId = 1L;
        long modifiedId = 2L;

        RoleProfileStatusResource roleProfileStatusResource = newRoleProfileStatusResource().withModifiedBy(modifiedId).build();
        UserResource userResource = newUserResource().build();

        when(roleProfileStatusRestService.findByUserIdAndProfileRole(userId, ASSESSOR)).thenReturn(restSuccess(roleProfileStatusResource));
        when(userRestService.retrieveUserById(modifiedId)).thenReturn(restSuccess(userResource));


        mockMvc.perform(get("/admin/user/{userId}/role-profile",userId))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/role-profile-details"));
    }

}