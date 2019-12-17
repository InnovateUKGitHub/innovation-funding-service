package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.admin.form.EditUserForm;
import org.innovateuk.ifs.management.admin.form.SearchExternalUsersForm;
import org.innovateuk.ifs.management.admin.viewmodel.EditUserViewModel;
import org.innovateuk.ifs.management.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.management.registration.service.InternalUserService;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.Collections;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * A mock MVC test for user management controller
 */
public class UserManagementControllerTest extends AbstractAsyncWaitMockMVCTest<UserManagementController> {

    private UserPageResource userPageResource;

    private RoleInvitePageResource roleInvitePageResource;

    @Mock
    private InternalUserService internalUserService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private InviteUserRestService inviteUserRestService;

    @Mock
    private EncryptedCookieService cookieService;

    @Before
    public void setUpCommonExpectations() {

        userPageResource = new UserPageResource();

        roleInvitePageResource = new RoleInvitePageResource();

        when(userRestService.getActiveUsers(1, 5)).thenReturn(restSuccess(userPageResource));

        when(userRestService.getInactiveUsers(1, 5)).thenReturn(restSuccess(userPageResource));

        when(inviteUserRestService.getPendingInternalUserInvites(1, 5)).thenReturn(restSuccess(roleInvitePageResource));
    }

    @Test
    public void testViewActive() throws Exception {
        when(userRestService.getActiveExternalUsers(1, 5)).thenReturn(restSuccess(userPageResource));
        when(userRestService.getInactiveExternalUsers(1, 5)).thenReturn(restSuccess(userPageResource));

        mockMvc.perform(get("/admin/users/active")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model",
                        new UserListViewModel(
                                "active",
                                userPageResource.getContent(),
                                userPageResource.getContent(),
                                roleInvitePageResource.getContent(),
                                userPageResource.getTotalElements(),
                                userPageResource.getTotalElements(),
                                roleInvitePageResource.getTotalElements(),
                                new PaginationViewModel(userPageResource),
                                new PaginationViewModel(userPageResource),
                                new PaginationViewModel(roleInvitePageResource),
                                false)
                ));
    }

    @Test
    public void testViewInactive() throws Exception {
        when(userRestService.getActiveExternalUsers(1, 5)).thenReturn(restSuccess(userPageResource));
        when(userRestService.getInactiveExternalUsers(1, 5)).thenReturn(restSuccess(userPageResource));

        mockMvc.perform(get("/admin/users/inactive")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model",
                        new UserListViewModel(
                                "inactive",
                                userPageResource.getContent(),
                                userPageResource.getContent(),
                                roleInvitePageResource.getContent(),
                                userPageResource.getTotalElements(),
                                userPageResource.getTotalElements(),
                                roleInvitePageResource.getTotalElements(),
                                new PaginationViewModel(userPageResource),
                                new PaginationViewModel(userPageResource),
                                new PaginationViewModel(roleInvitePageResource),
                                false)
                ));
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
                        new PaginationViewModel(userPageResource),
                        new PaginationViewModel(userPageResource),
                        new PaginationViewModel(roleInvitePageResource),
                        true)));
    }

    @Test
    public void testViewUser() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        UserResource user = newUserResource().withCreatedOn(now).withCreatedBy("abc").withModifiedOn(now).withModifiedBy("abc").build();
        when(userRestService.retrieveUserById(1L)).thenReturn(restSuccess(user));

        mockMvc.perform(get("/admin/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user"))
                .andExpect(model().attribute("model", new EditUserViewModel(user, false)));
    }

    @Test
    public void updateUserSuccess() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        when(internalUserService.editInternalUser(Mockito.any()))
                .thenReturn(serviceSuccess());
        UserResource user = newUserResource().withCreatedOn(now).withCreatedBy("abc").withModifiedOn(now).withModifiedBy("abc")
                .withEmail("asdf@asdf.com")
                .build();
        when(userRestService.retrieveUserById(1L)).thenReturn(restSuccess(user));

        mockMvc.perform(post("/admin/user/{userId}/edit", 1L).
                param("firstName", "First").
                param("lastName", "Last").
                param("email", "asdf@asdf.com").
                param("role", "IFS_ADMINISTRATOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/users/active"));
    }
    @Test
    public void updateUserSuccess_changeEmail() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        when(internalUserService.editInternalUser(Mockito.any()))
                .thenReturn(serviceSuccess());
        UserResource user = newUserResource().withCreatedOn(now).withCreatedBy("abc").withModifiedOn(now).withModifiedBy("abc")
                .withEmail("asdf@asdf.com")
                .build();
        when(userRestService.retrieveUserById(1L)).thenReturn(restSuccess(user));

        mockMvc.perform(post("/admin/user/{userId}/edit", 1L).
                param("firstName", "First").
                param("lastName", "Last").
                param("email", "new-email@asdf.com").
                param("role", "IFS_ADMINISTRATOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/user/" + 1 + "/edit/confirm"));

        verify(cookieService).saveToCookie(any(), eq("NEW_EMAIL_COOKIE"), any());
    }

    @Test
    public void viewEditUserSuccess() throws Exception {
        String email = "asdf@asdf.com";
        UserResource userResource = newUserResource()
                .withFirstName("first")
                .withLastName("last")
                .withEmail(email)
                .withRolesGlobal(Collections.singletonList(IFS_ADMINISTRATOR))
                .withStatus(UserStatus.ACTIVE)
                .build();

        when(userRestService.retrieveUserById(1L))
                .thenReturn(restSuccess(userResource));

        EditUserForm expectedForm = new EditUserForm();
        expectedForm.setFirstName("first");
        expectedForm.setLastName("last");
        expectedForm.setRole(IFS_ADMINISTRATOR);
        expectedForm.setEmail(email);

        mockMvc.perform(get("/admin/user/{userId}/edit", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-user"))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", new EditUserViewModel(userResource, false)));
    }

    @Test
    public void deactivateUserSuccess() throws Exception {

        String email = "asdf@asdf.com";
        Role role = IFS_ADMINISTRATOR;
        UserResource userResource = newUserResource()
                .withFirstName("first")
                .withLastName("last")
                .withEmail(email)
                .withRolesGlobal(Collections.singletonList(role))
                .build();

        when(userRestService.retrieveUserById(1L)).thenReturn(restSuccess(userResource));
        when(userRestService.deactivateUser(1L)).thenReturn(restSuccess());
        mockMvc.perform(post("/admin/user/{userId}/edit", 1L).
                    param("deactivateUser", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user/1"));

        verify(userRestService).deactivateUser(1L);
    }

    @Test
    public void deactivateUserDeactivateFails() throws Exception {

        String email = "asdf@asdf.com";
        Role role = IFS_ADMINISTRATOR;
        UserResource userResource = newUserResource()
                .withFirstName("first")
                .withLastName("last")
                .withEmail(email)
                .withRolesGlobal(Collections.singletonList(role))
                .build();

        when(userRestService.retrieveUserById(1L)).thenReturn(restSuccess(userResource));
        when(userRestService.deactivateUser(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_NOT_FOUND));
        mockMvc.perform(post("/admin/user/{userId}/edit", 1L).
                    param("deactivateUser", ""))
                .andExpect(status().isNotFound());

        verify(userRestService).deactivateUser(1L);
    }

    @Test
    public void deactivateUserFindUserFails() throws Exception {
        when(userRestService.retrieveUserById(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_FORBIDDEN));
        mockMvc.perform(post("/admin/user/{userId}/edit", 1L).
                    param("deactivateUser", ""))
                .andExpect(status().isForbidden());
    }

    @Test
    public void reactivateUserSuccess() throws Exception {

        String email = "asdf@asdf.com";
        Role role = IFS_ADMINISTRATOR;
        UserResource userResource = newUserResource()
                .withFirstName("first")
                .withLastName("last")
                .withEmail(email)
                .withRolesGlobal(Collections.singletonList(role))
                .build();

        when(userRestService.retrieveUserById(1L)).thenReturn(restSuccess(userResource));
        when(userRestService.reactivateUser(1L)).thenReturn(restSuccess());
        mockMvc.perform(post("/admin/user/{userId}", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user/1"));

        verify(userRestService).reactivateUser(1L);
    }

    @Test
    public void reactivateUserReactivateFails() throws Exception {

        String email = "asdf@asdf.com";
        Role role = IFS_ADMINISTRATOR;
        UserResource userResource = newUserResource()
                .withFirstName("first")
                .withLastName("last")
                .withEmail(email)
                .withRolesGlobal(Collections.singletonList(role))
                .build();

        when(userRestService.retrieveUserById(1L)).thenReturn(restSuccess(userResource));
        when(userRestService.reactivateUser(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_NOT_FOUND));
        mockMvc.perform(post("/admin/user/{userId}", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().isNotFound());

        verify(userRestService).reactivateUser(1L);
    }

    @Test
    public void reactivateUserFindUserFails() throws Exception {

        when(userRestService.retrieveUserById(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_FORBIDDEN));
        mockMvc.perform(post("/admin/user/{userId}", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().isForbidden());
    }

    @Test
    public void viewFindExternalUsers() throws Exception {
        mockMvc.perform(get("/admin/external/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/search-external-users"))
                .andExpect(model().attribute("form", new SearchExternalUsersForm()))
                .andExpect(model().attribute("tab", "users"))
                .andExpect(model().attribute("mode", "init"))
                .andExpect(model().attribute("users", emptyList()));
    }

    @Test
    public void viewFindExternalInvites() throws Exception {
        mockMvc.perform(get("/admin/external/invites"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/search-external-users"))
                .andExpect(model().attribute("form", new SearchExternalUsersForm()))
                .andExpect(model().attribute("tab", "invites"))
                .andExpect(model().attribute("mode", "init"))
                .andExpect(model().attribute("users", emptyList()));
    }

    @Test
    public void findExternalUsers() throws Exception {
        String searchString = "smith";

        when(userRestService.findExternalUsers(searchString, SearchCategory.EMAIL)).thenReturn(restSuccess(emptyList()));
        mockMvc.perform(post("/admin/external/users").
                param("searchString", searchString).
                param("searchCategory", "EMAIL"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("tab", "users"))
                .andExpect(model().attribute("mode", "search"))
                .andExpect(model().attribute("users", emptyList()));
    }

    @Test
    public void findExternalInvites() throws Exception {
        String searchString = "smith";

        when(inviteUserRestService.findExternalInvites(searchString, SearchCategory.ORGANISATION_NAME)).thenReturn(restSuccess(emptyList()));
        mockMvc.perform(post("/admin/external/users").
                param("searchString", searchString).
                param("searchCategory", "ORGANISATION_NAME").
                param("pending", ""))
                .andExpect(status().isOk())
                .andExpect(model().attribute("tab", "invites"))
                .andExpect(model().attribute("mode", "search"))
                .andExpect(model().attribute("invites", emptyList()));
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
    protected UserManagementController supplyControllerUnderTest() {
        return new UserManagementController();
    }
}