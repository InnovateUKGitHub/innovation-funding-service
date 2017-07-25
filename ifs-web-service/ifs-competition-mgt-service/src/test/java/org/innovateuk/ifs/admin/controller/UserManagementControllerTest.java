package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.admin.form.EditUserForm;
import org.innovateuk.ifs.admin.viewmodel.EditUserViewModel;
import org.innovateuk.ifs.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.registration.service.InternalUserService;
import org.innovateuk.ifs.user.builder.RoleResourceBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * A mock MVC test for user management controller
 */
@RunWith(MockitoJUnitRunner.class)
public class UserManagementControllerTest extends BaseControllerMockMVCTest<UserManagementController>{

    private UserPageResource userPageResource;

    @Mock
    private InternalUserService internalUserServiceMock;

    @Before
    public void setUp(){
        super.setUp();

        userPageResource = new UserPageResource();

        when(userRestServiceMock.getActiveInternalUsers(1, 5)).thenReturn(RestResult.restSuccess(userPageResource));

        when(userRestServiceMock.getInactiveInternalUsers(1, 5)).thenReturn(RestResult.restSuccess(userPageResource));

        when(inviteUserRestServiceMock.getPendingInternalUsers(1, 5)).thenReturn(RestResult.restSuccess(userPageResource));
    }

    @Test
    public void testViewActive() throws Exception {
        mockMvc.perform(get("/admin/users/active")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model", new UserListViewModel("active", userPageResource.getContent(), userPageResource.getContent(), userPageResource.getContent(),
                        userPageResource.getTotalElements(), userPageResource.getTotalElements(), userPageResource.getTotalElements(),
                        new PaginationViewModel(userPageResource, "active"), new PaginationViewModel(userPageResource, "inactive"), new PaginationViewModel(userPageResource, "pending"))));
    }

    @Test
    public void testViewInactive() throws Exception {
        mockMvc.perform(get("/admin/users/inactive")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model", new UserListViewModel("inactive", userPageResource.getContent(), userPageResource.getContent(), userPageResource.getContent(),
                        userPageResource.getTotalElements(), userPageResource.getTotalElements(), userPageResource.getTotalElements(),
                        new PaginationViewModel(userPageResource, "active"), new PaginationViewModel(userPageResource, "inactive"), new PaginationViewModel(userPageResource, "pending"))));
    }

    @Test
    public void testViewPending() throws Exception {
        mockMvc.perform(get("/admin/users/pending")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model", new UserListViewModel("pending", userPageResource.getContent(), userPageResource.getContent(), userPageResource.getContent(),
                        userPageResource.getTotalElements(), userPageResource.getTotalElements(), userPageResource.getTotalElements(),
                        new PaginationViewModel(userPageResource, "active"), new PaginationViewModel(userPageResource, "inactive"), new PaginationViewModel(userPageResource, "pending"))));
    }

    @Test
    public void testViewUser() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        UserResource user = newUserResource().build();
        when(userRestServiceMock.retrieveUserById(1L)).thenReturn(RestResult.restSuccess(user));
        when(profileRestService.getUserProfile(1L)).thenReturn(RestResult.restSuccess(newUserProfileResource().withCreatedBy("abc").withCreatedOn(now).build()));

        mockMvc.perform(get("/admin/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user"))
                .andExpect(model().attribute("model", new EditUserViewModel("abc", now, user)));
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
                .andExpect(model().attribute("form", expectedForm));
    }

    @Override
    protected UserManagementController supplyControllerUnderTest() {
        return new UserManagementController();
    }
}
