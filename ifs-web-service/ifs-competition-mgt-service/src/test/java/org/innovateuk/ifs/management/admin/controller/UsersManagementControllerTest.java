package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.user.resource.ManageUserPageResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * A mock MVC test for user management controller
 */
public class UsersManagementControllerTest extends AbstractAsyncWaitMockMVCTest<UsersManagementController> {

    private ManageUserPageResource manageUserPageResource;
    private RoleInvitePageResource roleInvitePageResource;

    @Mock
    private UserRestService userRestService;

    @Mock
    private InviteUserRestService inviteUserRestService;

    @Before
    public void setUpCommonExpectations() {

        manageUserPageResource = new ManageUserPageResource();
        roleInvitePageResource = new RoleInvitePageResource();

        when(userRestService.getActiveUsers(null, 0, 5)).thenReturn(restSuccess(manageUserPageResource));
        when(userRestService.getInactiveUsers(null, 0, 5)).thenReturn(restSuccess(manageUserPageResource));
        when(inviteUserRestService.getPendingInternalUserInvites(null, 0, 5)).thenReturn(restSuccess(roleInvitePageResource));

    }

    @Test
    public void viewActive() throws Exception {
        when(userRestService.getActiveExternalUsers(null, 0, 5)).thenReturn(restSuccess(manageUserPageResource));
        when(userRestService.getInactiveExternalUsers(null, 0, 5)).thenReturn(restSuccess(manageUserPageResource));

        mockMvc.perform(get("/admin/users/active")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model",
                        new UserListViewModel(
                                "active",
                                null,
                                manageUserPageResource.getContent(),
                                manageUserPageResource.getContent(),
                                roleInvitePageResource.getContent(),
                                manageUserPageResource.getTotalElements(),
                                manageUserPageResource.getTotalElements(),
                                roleInvitePageResource.getTotalElements(),
                                new PaginationViewModel(manageUserPageResource),
                                new PaginationViewModel(manageUserPageResource),
                                new PaginationViewModel(roleInvitePageResource),
                                false)
                ));
    }

    @Test
    public void viewInactive() throws Exception {
        when(userRestService.getActiveExternalUsers(null, 0, 5)).thenReturn(restSuccess(manageUserPageResource));
        when(userRestService.getInactiveExternalUsers(null, 0, 5)).thenReturn(restSuccess(manageUserPageResource));

        mockMvc.perform(get("/admin/users/inactive")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model",
                        new UserListViewModel(
                                "inactive",
                                null,
                                manageUserPageResource.getContent(),
                                manageUserPageResource.getContent(),
                                roleInvitePageResource.getContent(),
                                manageUserPageResource.getTotalElements(),
                                manageUserPageResource.getTotalElements(),
                                roleInvitePageResource.getTotalElements(),
                                new PaginationViewModel(manageUserPageResource),
                                new PaginationViewModel(manageUserPageResource),
                                new PaginationViewModel(roleInvitePageResource),
                                false)
                ));
    }

    @Test
    public void viewPending() throws Exception {
        mockMvc.perform(get("/admin/users/pending")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model",
                        new UserListViewModel(
                                "pending",
                                null,
                                manageUserPageResource.getContent(),
                                manageUserPageResource.getContent(),
                                roleInvitePageResource.getContent(),
                                manageUserPageResource.getTotalElements(),
                                manageUserPageResource.getTotalElements(), roleInvitePageResource.getTotalElements(),
                                new PaginationViewModel(manageUserPageResource),
                                new PaginationViewModel(manageUserPageResource),
                                new PaginationViewModel(roleInvitePageResource),
                                true)
                ));
    }

    @Test
    public void resendInvite() throws Exception {

        when(inviteUserRestService.resendInternalUserInvite(123L)).
                thenReturn(restSuccess());

        mockMvc.perform(post("/admin/users/pending/resend-invite?inviteId=" + 123L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/users/pending"));

        verify(inviteUserRestService).resendInternalUserInvite(123L);
    }

    @Override
    protected UsersManagementController supplyControllerUnderTest() {
        return new UsersManagementController();
    }
}