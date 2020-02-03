package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.admin.form.EditUserForm;
import org.innovateuk.ifs.management.admin.form.SearchExternalUsersForm;
import org.innovateuk.ifs.management.admin.viewmodel.AssessorListViewModel;
import org.innovateuk.ifs.management.admin.viewmodel.EditUserViewModel;
import org.innovateuk.ifs.management.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.management.registration.service.InternalUserService;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.service.RoleProfileStatusRestService;
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
    private InternalUserService internalUserServiceMock;

    @Mock
    private UserRestService userRestServiceMock;

    @Mock
    private InviteUserRestService inviteUserRestServiceMock;

    @Mock
    private EncryptedCookieService cookieServiceMock;

    @Mock
    private RoleProfileStatusRestService roleProfileStatusRestServiceMock;

    @Before
    public void setUpCommonExpectations() {

        userPageResource = new UserPageResource();

        roleInvitePageResource = new RoleInvitePageResource();

        when(userRestServiceMock.getActiveUsers(null, 0, 5)).thenReturn(restSuccess(userPageResource));

        when(userRestServiceMock.getInactiveUsers(null, 0, 5)).thenReturn(restSuccess(userPageResource));

        when(inviteUserRestServiceMock.getPendingInternalUserInvites(null, 0, 5)).thenReturn(restSuccess(roleInvitePageResource));
    }

    @Test
    public void viewActive() throws Exception {
        when(userRestServiceMock.getActiveExternalUsers(null, 0, 5)).thenReturn(restSuccess(userPageResource));
        when(userRestServiceMock.getInactiveExternalUsers(null,0, 5)).thenReturn(restSuccess(userPageResource));

        mockMvc.perform(get("/admin/users/active")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model",
                        new UserListViewModel(
                                "active",
                                null,
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
    public void viewInactive() throws Exception {
        when(userRestServiceMock.getActiveExternalUsers(null,0, 5)).thenReturn(restSuccess(userPageResource));
        when(userRestServiceMock.getInactiveExternalUsers(null,0, 5)).thenReturn(restSuccess(userPageResource));

        mockMvc.perform(get("/admin/users/inactive")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model",
                        new UserListViewModel(
                                "inactive",
                                null,
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
                                userPageResource.getContent(),
                                userPageResource.getContent(),
                                roleInvitePageResource.getContent(),
                                userPageResource.getTotalElements(),
                                userPageResource.getTotalElements(), roleInvitePageResource.getTotalElements(),
                                new PaginationViewModel(userPageResource),
                                new PaginationViewModel(userPageResource),
                                new PaginationViewModel(roleInvitePageResource),
                                true)
                ));
    }

    @Test
    public void viewUser() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        UserResource user = newUserResource().withCreatedOn(now).withCreatedBy("abc").withModifiedOn(now).withModifiedBy("abc").build();
        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(restSuccess(user));

        mockMvc.perform(get("/admin/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user"))
                .andExpect(model().attribute("model", new EditUserViewModel(user, false)));
    }

    @Test
    public void updateUserSuccess() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        when(internalUserServiceMock.editInternalUser(Mockito.any()))
                .thenReturn(serviceSuccess());
        UserResource user = newUserResource().withCreatedOn(now).withCreatedBy("abc").withModifiedOn(now).withModifiedBy("abc")
                .withEmail("asdf@asdf.com")
                .build();
        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(restSuccess(user));

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
        when(internalUserServiceMock.editInternalUser(Mockito.any()))
                .thenReturn(serviceSuccess());
        UserResource user = newUserResource().withCreatedOn(now).withCreatedBy("abc").withModifiedOn(now).withModifiedBy("abc")
                .withEmail("asdf@asdf.com")
                .build();
        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(restSuccess(user));

        mockMvc.perform(post("/admin/user/{userId}/edit", 1L).
                param("firstName", "First").
                param("lastName", "Last").
                param("email", "new-email@asdf.com").
                param("role", "IFS_ADMINISTRATOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/user/" + 1 + "/edit/confirm"));

        verify(cookieServiceMock).saveToCookie(any(), eq("NEW_EMAIL_COOKIE"), any());
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

        when(userRestServiceMock.retrieveUserById(1L))
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

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(restSuccess(userResource));
        when(userRestServiceMock.deactivateUser(1L)).thenReturn(restSuccess());
        mockMvc.perform(post("/admin/user/{userId}/edit", 1L).
                    param("deactivateUser", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user/1"));

        verify(userRestServiceMock).deactivateUser(1L);
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

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(restSuccess(userResource));
        when(userRestServiceMock.deactivateUser(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_NOT_FOUND));
        mockMvc.perform(post("/admin/user/{userId}/edit", 1L).
                    param("deactivateUser", ""))
                .andExpect(status().isNotFound());

        verify(userRestServiceMock).deactivateUser(1L);
    }

    @Test
    public void deactivateUserFindUserFails() throws Exception {
        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_FORBIDDEN));
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

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(restSuccess(userResource));
        when(userRestServiceMock.reactivateUser(1L)).thenReturn(restSuccess());
        mockMvc.perform(post("/admin/user/{userId}", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user/1"));

        verify(userRestServiceMock).reactivateUser(1L);
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

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(restSuccess(userResource));
        when(userRestServiceMock.reactivateUser(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_NOT_FOUND));
        mockMvc.perform(post("/admin/user/{userId}", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().isNotFound());

        verify(userRestServiceMock).reactivateUser(1L);
    }

    @Test
    public void reactivateUserFindUserFails() throws Exception {

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_FORBIDDEN));
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

        when(userRestServiceMock.findExternalUsers(searchString, SearchCategory.EMAIL)).thenReturn(restSuccess(emptyList()));
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

        when(inviteUserRestServiceMock.findExternalInvites(searchString, SearchCategory.ORGANISATION_NAME)).thenReturn(restSuccess(emptyList()));
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

        when(inviteUserRestServiceMock.resendInternalUserInvite(123L)).
                thenReturn(restSuccess());

        mockMvc.perform(post("/admin/users/pending/resend-invite?inviteId=" + 123L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/users/pending"));

        verify(inviteUserRestServiceMock).resendInternalUserInvite(123L);
    }

    @Test
    public void viewAvailableAssessors() throws Exception {
        when(roleProfileStatusRestServiceMock.getAvailableAssessors(null, 0, 5)).thenReturn(restSuccess(userPageResource));
        when(roleProfileStatusRestServiceMock.getUnavailableAssessors(null, 0, 5)).thenReturn(restSuccess(userPageResource));
        when(roleProfileStatusRestServiceMock.getDisabledAssessors(null, 0, 5)).thenReturn(restSuccess(userPageResource));

        mockMvc.perform(get("/admin/assessors/available")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/assessors"))
                .andExpect(model().attribute("model",
                        new AssessorListViewModel(
                                "available",
                                null,
                                userPageResource.getContent(),
                                userPageResource.getContent(),
                                userPageResource.getContent(),
                                userPageResource.getTotalElements(),
                                userPageResource.getTotalElements(),
                                userPageResource.getTotalElements(),
                                new PaginationViewModel(userPageResource),
                                new PaginationViewModel(userPageResource),
                                new PaginationViewModel(userPageResource)
                                )
                ));
    }


    @Test
    public void viewUnavailableAssessors() throws Exception {
        when(roleProfileStatusRestServiceMock.getAvailableAssessors(null, 0, 5)).thenReturn(restSuccess(userPageResource));
        when(roleProfileStatusRestServiceMock.getUnavailableAssessors(null, 0, 5)).thenReturn(restSuccess(userPageResource));
        when(roleProfileStatusRestServiceMock.getDisabledAssessors(null, 0, 5)).thenReturn(restSuccess(userPageResource));

        mockMvc.perform(get("/admin/assessors/unavailable")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/assessors"))
                .andExpect(model().attribute("model",
                        new AssessorListViewModel(
                                "unavailable",
                                null,
                                userPageResource.getContent(),
                                userPageResource.getContent(),
                                userPageResource.getContent(),
                                userPageResource.getTotalElements(),
                                userPageResource.getTotalElements(),
                                userPageResource.getTotalElements(),
                                new PaginationViewModel(userPageResource),
                                new PaginationViewModel(userPageResource),
                                new PaginationViewModel(userPageResource)
                        )
                ));
    }

    @Test
    public void viewDisabledAssessors() throws Exception {
        when(roleProfileStatusRestServiceMock.getAvailableAssessors(null, 0, 5)).thenReturn(restSuccess(userPageResource));
        when(roleProfileStatusRestServiceMock.getUnavailableAssessors(null, 0, 5)).thenReturn(restSuccess(userPageResource));
        when(roleProfileStatusRestServiceMock.getDisabledAssessors(null, 0, 5)).thenReturn(restSuccess(userPageResource));

        mockMvc.perform(get("/admin/assessors/disabled")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/assessors"))
                .andExpect(model().attribute("model",
                        new AssessorListViewModel(
                                "disabled",
                                null,
                                userPageResource.getContent(),
                                userPageResource.getContent(),
                                userPageResource.getContent(),
                                userPageResource.getTotalElements(),
                                userPageResource.getTotalElements(),
                                userPageResource.getTotalElements(),
                                new PaginationViewModel(userPageResource),
                                new PaginationViewModel(userPageResource),
                                new PaginationViewModel(userPageResource)
                        )
                ));
    }

    @Override
    protected UserManagementController supplyControllerUnderTest() {
        return new UserManagementController();
    }
}