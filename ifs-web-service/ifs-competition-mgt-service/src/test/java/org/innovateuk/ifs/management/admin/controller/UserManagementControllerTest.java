package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.admin.form.EditUserForm;
import org.innovateuk.ifs.management.admin.form.SearchExternalUsersForm;
import org.innovateuk.ifs.management.admin.viewmodel.ViewUserViewModel;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * A mock MVC test for user management controller
 */
public class UserManagementControllerTest extends BaseControllerMockMVCTest<UserManagementController> {

    @Mock
    private InternalUserService internalUserService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private EncryptedCookieService cookieService;

    @Mock
    private RoleProfileStatusRestService roleProfileStatusRestService;

    @Before
    public void setUpCommonExpectations() {

        setField(controller, "profileFeatureToggle", true);

        when(roleProfileStatusRestService.findByUserId(anyLong())).thenReturn(restSuccess(emptyList()));
    }

    @Test
    public void testViewUser() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        UserResource user = newUserResource().withCreatedOn(now).withCreatedBy("abc").withModifiedOn(now).withModifiedBy("abc").build();
        when(userRestService.retrieveUserById(1L)).thenReturn(restSuccess(user));

        mockMvc.perform(get("/admin/user/{userId}/inactive", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/inactive-user"))
                .andExpect(model().attribute("model", new ViewUserViewModel(user, getLoggedInUser(), emptyList(), true)));
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

        mockMvc.perform(post("/admin/user/{userId}/active", 1L).
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

        mockMvc.perform(post("/admin/user/{userId}/active", 1L).
                param("firstName", "First").
                param("lastName", "Last").
                param("email", "new-email@asdf.com").
                param("role", "IFS_ADMINISTRATOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/user/" + 1 + "/active/confirm"));

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

        mockMvc.perform(get("/admin/user/{userId}/active", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/active-user"))
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", new ViewUserViewModel(userResource, getLoggedInUser(), emptyList(), true)));
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
        mockMvc.perform(post("/admin/user/{userId}/active", 1L).
                    param("deactivateUser", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user/1/inactive"));

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
        mockMvc.perform(post("/admin/user/{userId}/active", 1L).
                    param("deactivateUser", ""))
                .andExpect(status().isNotFound());

        verify(userRestService).deactivateUser(1L);
    }

    @Test
    public void deactivateUserFindUserFails() throws Exception {
        when(userRestService.retrieveUserById(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_FORBIDDEN));
        mockMvc.perform(post("/admin/user/{userId}/active", 1L).
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
        mockMvc.perform(post("/admin/user/{userId}/inactive", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user/1/active"));

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
        mockMvc.perform(post("/admin/user/{userId}/inactive", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().isNotFound());

        verify(userRestService).reactivateUser(1L);
    }

    @Test
    public void reactivateUserFindUserFails() throws Exception {

        when(userRestService.retrieveUserById(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_FORBIDDEN));
        mockMvc.perform(post("/admin/user/{userId}/inactive", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().isForbidden());
    }

    @Override
    protected UserManagementController supplyControllerUnderTest() {
        return new UserManagementController();
    }
}