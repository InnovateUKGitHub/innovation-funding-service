package org.innovateuk.ifs.admin.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.admin.form.InviteUserForm;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
public class InviteUserControllerTest extends BaseControllerMockMVCTest<InviteUserController> {

    @Test
    public void inviteNewUser() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/invite-user"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/invite-new-user"))
                .andExpect(model().attribute("form", new InviteUserForm()));
    }

    @Test
    public void saveUserInviteWhenSaveInviteFails() throws Exception {

        when(inviteUserServiceMock.saveUserInvite(Mockito.any()))
                .thenReturn(ServiceResult.serviceFailure(CommonFailureKeys.USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/invite-user").
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@asdf.com").
                param("role", "IFS_ADMINISTRATOR"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/invite-new-user"));
    }

    @Test
    public void saveUserInviteSuccess() throws Exception {

        when(inviteUserServiceMock.saveUserInvite(Mockito.any())).thenReturn(ServiceResult.serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/invite-user").
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@asdf.com").
                param("role", "IFS_ADMINISTRATOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/users/active"));
    }

    @Override
    protected InviteUserController supplyControllerUnderTest() {
        return new InviteUserController();
    }
}

