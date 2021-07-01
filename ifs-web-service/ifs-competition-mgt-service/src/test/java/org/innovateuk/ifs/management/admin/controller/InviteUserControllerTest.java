package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.management.admin.form.InviteUserForm;
import org.innovateuk.ifs.management.admin.populator.InviteUserModelPopulator;
import org.innovateuk.ifs.management.invite.service.InviteUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.innovateuk.ifs.user.resource.Role.SUPPORTER;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class InviteUserControllerTest extends BaseControllerMockMVCTest<InviteUserController> {

    @Mock
    private InviteUserService inviteUserServiceMock;

    @Mock
    private InviteUserModelPopulator populator;

    @Test
    public void inviteInternalNewUser() throws Exception {
        InviteUserForm expectedUserForm = new InviteUserForm();

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/invite-user"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/invite-new-user"))
                .andExpect(model().attribute("form", expectedUserForm));
    }

    @Test
    public void inviteExternalNewUser() throws Exception {
        InviteUserForm expectedUserForm = new InviteUserForm();

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/invite-external-user")
                .param("role", SUPPORTER.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/invite-new-user"))
                .andExpect(model().attribute("form", expectedUserForm));
    }

    @Test
    public void selectExternalRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/select-external-role"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/select-external-role"));
    }

    @Test
    public void selectedRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/select-external-role")
                .param("role", SUPPORTER.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/admin/invite-external-user?role=%s", SUPPORTER.toString())));
    }

    @Test
    public void saveInternalUserInviteWhenSaveInviteFails() throws Exception {

        when(inviteUserServiceMock.saveUserInvite(Mockito.any()))
                .thenReturn(ServiceResult.serviceFailure(CommonFailureKeys.USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/invite-user").
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@innovateuk.ukri.test").
                param("role", "IFS_ADMINISTRATOR"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/invite-new-user"));
    }

    @Test
    public void saveInternalUserInviteSuccess() throws Exception {

        when(inviteUserServiceMock.saveUserInvite(Mockito.any())).thenReturn(ServiceResult.serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/invite-external-user").
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@innovateuk.ukri.test").
                param("role", "IFS_ADMINISTRATOR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/users/pending"));
    }

    @Test
    public void saveExternalUserInviteWhenSaveInviteFails() throws Exception {

        when(inviteUserServiceMock.saveUserInvite(Mockito.any()))
                .thenReturn(ServiceResult.serviceFailure(CommonFailureKeys.USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED));

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/invite-external-user").
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@ktn-uk.test").
                param("role", "KNOWLEDGE_TRANSFER_ADVISER"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/invite-new-user"));
    }

    @Test
    public void saveExternalUserInviteSuccess() throws Exception {

        when(inviteUserServiceMock.saveUserInvite(Mockito.any())).thenReturn(ServiceResult.serviceSuccess());

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/invite-external-user").
                param("firstName", "First").
                param("lastName", "Last").
                param("emailAddress", "asdf@ktn-uk.test").
                param("role", "KNOWLEDGE_TRANSFER_ADVISER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/users/pending"));
    }

    @Override
    protected InviteUserController supplyControllerUnderTest() {
        return new InviteUserController();
    }
}

