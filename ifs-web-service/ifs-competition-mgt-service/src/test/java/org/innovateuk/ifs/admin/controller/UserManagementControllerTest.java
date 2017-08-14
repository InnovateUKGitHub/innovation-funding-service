package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.admin.form.EditUserForm;
import org.innovateuk.ifs.admin.viewmodel.EditUserViewModel;
import org.innovateuk.ifs.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.registration.service.InternalUserService;
import org.innovateuk.ifs.user.builder.RoleResourceBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * A mock MVC test for user management controller
 */
@RunWith(MockitoJUnitRunner.class)
public class UserManagementControllerTest extends BaseControllerMockMVCTest<UserManagementController>{

    private UserPageResource userPageResource;

    private RoleInvitePageResource roleInvitePageResource;

    @Mock
    private InternalUserService internalUserServiceMock;

    @Before
    public void setUp(){
        super.setUp();

        userPageResource = new UserPageResource();

        roleInvitePageResource = new RoleInvitePageResource();

        when(userRestServiceMock.getActiveInternalUsers(1, 5)).thenReturn(RestResult.restSuccess(userPageResource));

        when(userRestServiceMock.getInactiveInternalUsers(1, 5)).thenReturn(RestResult.restSuccess(userPageResource));

        when(inviteUserRestServiceMock.getPendingInternalUserInvites(1, 5)).thenReturn(RestResult.restSuccess(roleInvitePageResource));
    }

    @Test
    public void testViewActive() throws Exception {
        mockMvc.perform(get("/admin/users/active")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model", new UserListViewModel("active", userPageResource.getContent(), userPageResource.getContent(), roleInvitePageResource.getContent(),
                        userPageResource.getTotalElements(), userPageResource.getTotalElements(), roleInvitePageResource.getTotalElements(),
                        new PaginationViewModel(userPageResource, "active"), new PaginationViewModel(userPageResource, "inactive"), new PaginationViewModel(roleInvitePageResource, "pending"))));
    }

    @Test
    public void testViewInactive() throws Exception {
        mockMvc.perform(get("/admin/users/inactive")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model", new UserListViewModel("inactive", userPageResource.getContent(), userPageResource.getContent(), roleInvitePageResource.getContent(),
                        userPageResource.getTotalElements(), userPageResource.getTotalElements(), roleInvitePageResource.getTotalElements(),
                        new PaginationViewModel(userPageResource, "active"), new PaginationViewModel(userPageResource, "inactive"), new PaginationViewModel(roleInvitePageResource, "pending"))));
    }

    @Test
    public void testViewPending() throws Exception {
        mockMvc.perform(get("/admin/users/pending")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model", new UserListViewModel("pending", userPageResource.getContent(), userPageResource.getContent(), roleInvitePageResource.getContent(),
                        userPageResource.getTotalElements(), userPageResource.getTotalElements(), roleInvitePageResource.getTotalElements(),
                        new PaginationViewModel(userPageResource, "active"), new PaginationViewModel(userPageResource, "inactive"), new PaginationViewModel(roleInvitePageResource, "pending"))));
    }

    @Test
    public void testViewUser() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        UserResource user = newUserResource().withCreatedOn(now).withCreatedBy("abc").withModifiedOn(now).withModifiedBy("abc").build();
        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(RestResult.restSuccess(user));

        mockMvc.perform(get("/admin/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user"))
                .andExpect(model().attribute("model", new EditUserViewModel(user)));
    }

    @Test
    public void updateUserWhenUpdateFails() throws Exception {

        when(internalUserServiceMock.editInternalUser(Mockito.any()))
                .thenReturn(ServiceResult.serviceFailure(CommonFailureKeys.NOT_AN_INTERNAL_USER_ROLE));

        RoleResource role = RoleResourceBuilder.newRoleResource()
                .withName("ifs_administrator")
                .build();

        UserResource userResource = UserResourceBuilder.newUserResource()
                .withRolesGlobal(Collections.singletonList(role))
                .build();
        when(userRestServiceMock.retrieveUserById(1L))
                .thenReturn(RestResult.restSuccess(userResource));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/{userId}/edit", 1L).
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@asdf.com").
                param("role", "COLLABORATOR"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-user"));
    }

    @Test
    public void updateUserSuccess() throws Exception {

        when(internalUserServiceMock.editInternalUser(Mockito.any()))
                .thenReturn(ServiceResult.serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/{userId}/edit", 1L).
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@asdf.com").
                param("role", "IFS_ADMINISTRATOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/users/active"));
    }

    @Test
    public void viewEditUserSuccess() throws Exception {

        RoleResource role = RoleResourceBuilder.newRoleResource()
                .withName("ifs_administrator")
                .build();

        String email = "asdf@asdf.com";
        UserResource userResource = UserResourceBuilder.newUserResource()
                .withFirstName("first")
                .withLastName("last")
                .withEmail(email)
                .withRolesGlobal(Collections.singletonList(role))
                .withStatus(UserStatus.ACTIVE)
                .build();

        when(userRestServiceMock.retrieveUserById(1L))
                .thenReturn(RestResult.restSuccess(userResource));

        EditUserForm expectedForm = new EditUserForm();
        expectedForm.setFirstName("first");
        expectedForm.setLastName("last");
        expectedForm.setRole(UserRoleType.IFS_ADMINISTRATOR);
        expectedForm.setEmailAddress(email);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/user/{userId}/edit", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-user"))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("user", userResource));
    }

    @Test
    public void deactivateUserSuccess() throws Exception {

        String email = "asdf@asdf.com";
        RoleResource role = RoleResourceBuilder.newRoleResource()
                .withName("ifs_administrator")
                .build();
        UserResource userResource = UserResourceBuilder.newUserResource()
                .withFirstName("first")
                .withLastName("last")
                .withEmail(email)
                .withRolesGlobal(Collections.singletonList(role))
                .build();

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(RestResult.restSuccess(userResource));
        when(userRestServiceMock.deactivateUser(1L)).thenReturn(RestResult.restSuccess());
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/{userId}/edit", 1L).
                    param("deactivateUser", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user/1"));

        verify(userRestServiceMock).deactivateUser(1L);
    }

    @Test
    public void deactivateUserDeactivateFails() throws Exception {

        String email = "asdf@asdf.com";
        RoleResource role = RoleResourceBuilder.newRoleResource()
                .withName("ifs_administrator")
                .build();
        UserResource userResource = UserResourceBuilder.newUserResource()
                .withFirstName("first")
                .withLastName("last")
                .withEmail(email)
                .withRolesGlobal(Collections.singletonList(role))
                .build();

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(RestResult.restSuccess(userResource));
        when(userRestServiceMock.deactivateUser(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_NOT_FOUND));
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/{userId}/edit", 1L).
                    param("deactivateUser", ""))
                .andExpect(status().isNotFound());

        verify(userRestServiceMock).deactivateUser(1L);
    }

    @Test
    public void deactivateUserFindUserFails() throws Exception {
        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_FORBIDDEN));
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/{userId}/edit", 1L).
                    param("deactivateUser", ""))
                .andExpect(status().isForbidden());
    }

    @Test
    public void reactivateUserSuccess() throws Exception {

        String email = "asdf@asdf.com";
        RoleResource role = RoleResourceBuilder.newRoleResource()
                .withName("ifs_administrator")
                .build();
        UserResource userResource = UserResourceBuilder.newUserResource()
                .withFirstName("first")
                .withLastName("last")
                .withEmail(email)
                .withRolesGlobal(Collections.singletonList(role))
                .build();

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(RestResult.restSuccess(userResource));
        when(userRestServiceMock.reactivateUser(1L)).thenReturn(RestResult.restSuccess());
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/{userId}", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user/1"));

        verify(userRestServiceMock).reactivateUser(1L);
    }

    @Test
    public void reactivateUserReactivateFails() throws Exception {

        String email = "asdf@asdf.com";
        RoleResource role = RoleResourceBuilder.newRoleResource()
                .withName("ifs_administrator")
                .build();
        UserResource userResource = UserResourceBuilder.newUserResource()
                .withFirstName("first")
                .withLastName("last")
                .withEmail(email)
                .withRolesGlobal(Collections.singletonList(role))
                .build();

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(RestResult.restSuccess(userResource));
        when(userRestServiceMock.reactivateUser(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_NOT_FOUND));
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/{userId}", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().isNotFound());

        verify(userRestServiceMock).reactivateUser(1L);
    }

    @Test
    public void reactivateUserFindUserFails() throws Exception {

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_FORBIDDEN));
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/user/{userId}", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().isForbidden());
    }

    @Override
    protected UserManagementController supplyControllerUnderTest() {
        return new UserManagementController();
    }
}
