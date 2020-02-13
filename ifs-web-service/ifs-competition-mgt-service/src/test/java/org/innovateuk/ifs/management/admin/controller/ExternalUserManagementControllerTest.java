package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.admin.form.SearchExternalUsersForm;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * A mock MVC test for user management controller
 */
public class ExternalUserManagementControllerTest extends BaseControllerMockMVCTest<ExternalUserManagementController> {

    @Mock
    private UserRestService userRestService;

    @Mock
    private InviteUserRestService inviteUserRestService;

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

    @Override
    protected ExternalUserManagementController supplyControllerUnderTest() {
        return new ExternalUserManagementController();
    }
}