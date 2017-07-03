package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.AdminRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InviteUserControllerTest  extends BaseControllerMockMVCTest<InviteUserController> {

    private InviteUserResource inviteUserResource;

    @Override
    protected InviteUserController supplyControllerUnderTest() {
        return new InviteUserController();
    }

    @Before
    public void setUp() {
        UserResource invitedUser = UserResourceBuilder.newUserResource()
                .withFirstName("A")
                .withLastName("D")
                .withEmail("A.D@gmail.com")
                .build();

        inviteUserResource = new InviteUserResource(invitedUser, AdminRoleType.IFS_ADMINISTRATOR);
    }

    @Test
    public void saveUserInvite() throws Exception {

        when(inviteUserServiceMock.saveUserInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getAdminRoleType())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/inviteUser/saveInvite")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteUserResource)))
                .andExpect(status().isOk());

        verify(inviteUserServiceMock).saveUserInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getAdminRoleType());
    }

    @Test
    public void getInvite() throws Exception {

        when(inviteUserServiceMock.getInvite("SomeHashString")).thenReturn(serviceSuccess(new RoleInviteResource()));

        mockMvc.perform(get("/inviteUser/getInvite/SomeHashString")).andExpect(status().isOk());

        verify(inviteUserServiceMock).getInvite("SomeHashString");

    }

    @Test
    public void checkExistingUser() throws Exception {

        when(inviteUserServiceMock.checkExistingUser("SomeHashString")).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/inviteUser/checkExistingUser/SomeHashString")).andExpect(status().isOk());

        verify(inviteUserServiceMock).checkExistingUser("SomeHashString");

    }
}

