package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.admin.viewmodel.EditUserViewModel;
import org.innovateuk.ifs.admin.viewmodel.UserListViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.user.resource.UserPageResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;

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

    @Before
    public void setUp(){
        super.setUp();

        userPageResource = new UserPageResource();

        when(userRestServiceMock.getActiveInternalUsers(1, 5)).thenReturn(RestResult.restSuccess(userPageResource));

        when(userRestServiceMock.getInactiveInternalUsers(1, 5)).thenReturn(RestResult.restSuccess(userPageResource));
    }

    @Test
    public void testViewActive() throws Exception {
        mockMvc.perform(get("/admin/users/active")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model", new UserListViewModel("active", userPageResource.getContent(), userPageResource.getContent(), userPageResource.getTotalElements(), userPageResource.getTotalElements(), new PaginationViewModel(userPageResource, "active"), new PaginationViewModel(userPageResource, "inactive"))));
    }

    @Test
    public void testViewInactive() throws Exception {
        mockMvc.perform(get("/admin/users/inactive")
                .param("page", "1")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attribute("model", new UserListViewModel("inactive", userPageResource.getContent(), userPageResource.getContent(), userPageResource.getTotalElements(), userPageResource.getTotalElements(), new PaginationViewModel(userPageResource, "active"), new PaginationViewModel(userPageResource, "inactive"))));
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

    @Override
    protected UserManagementController supplyControllerUnderTest() {
        return new UserManagementController();
    }
}
