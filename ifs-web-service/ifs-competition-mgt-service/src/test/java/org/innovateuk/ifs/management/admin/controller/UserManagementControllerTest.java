package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.admin.form.EditUserForm;
import org.innovateuk.ifs.management.admin.viewmodel.AssessorListViewModel;
import org.innovateuk.ifs.management.admin.viewmodel.ViewUserViewModel;
import org.innovateuk.ifs.management.registration.service.InternalUserService;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
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
        setField(controller, "profileFeatureToggle", true);

        userPageResource = new UserPageResource();

        when(roleProfileStatusRestServiceMock.findByUserId(anyLong())).thenReturn(restSuccess(emptyList()));
    }

    @Test
    public void viewUser() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        UserResource user = newUserResource().withCreatedOn(now).withCreatedBy("abc").withModifiedOn(now).withModifiedBy("abc").build();
        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(restSuccess(user));

        mockMvc.perform(get("/admin/user/{userId}/inactive", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/inactive-user"))
                .andExpect(model().attribute("model", new ViewUserViewModel(user, getLoggedInUser(), emptyList(), true)));
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
        when(internalUserServiceMock.editInternalUser(Mockito.any()))
                .thenReturn(serviceSuccess());
        UserResource user = newUserResource().withCreatedOn(now).withCreatedBy("abc").withModifiedOn(now).withModifiedBy("abc")
                .withEmail("asdf@asdf.com")
                .build();
        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(restSuccess(user));

        mockMvc.perform(post("/admin/user/{userId}/active", 1L).
                param("firstName", "First").
                param("lastName", "Last").
                param("email", "new-email@asdf.com").
                param("role", "IFS_ADMINISTRATOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/user/" + 1 + "/active/confirm"));

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

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(restSuccess(userResource));
        when(userRestServiceMock.deactivateUser(1L)).thenReturn(restSuccess());
        mockMvc.perform(post("/admin/user/{userId}/active", 1L).
                    param("deactivateUser", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user/1/inactive"));

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
        mockMvc.perform(post("/admin/user/{userId}/active", 1L).
                    param("deactivateUser", ""))
                .andExpect(status().isNotFound());

        verify(userRestServiceMock).deactivateUser(1L);
    }

    @Test
    public void deactivateUserFindUserFails() throws Exception {
        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_FORBIDDEN));
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

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(restSuccess(userResource));
        when(userRestServiceMock.reactivateUser(1L)).thenReturn(restSuccess());
        mockMvc.perform(post("/admin/user/{userId}/inactive", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/user/1/active"));

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
        mockMvc.perform(post("/admin/user/{userId}/inactive", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().isNotFound());

        verify(userRestServiceMock).reactivateUser(1L);
    }

    @Test
    public void reactivateUserFindUserFails() throws Exception {

        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(RestResult.restFailure(CommonFailureKeys.GENERAL_FORBIDDEN));
        mockMvc.perform(post("/admin/user/{userId}/inactive", 1L).
                    param("reactivateUser", ""))
                .andExpect(status().isForbidden());
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