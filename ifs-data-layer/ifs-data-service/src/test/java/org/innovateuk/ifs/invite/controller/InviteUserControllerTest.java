package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.invite.transactional.InviteUserService;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InviteUserControllerTest  extends BaseControllerMockMVCTest<InviteUserController> {

    private InviteUserResource inviteUserResource;


    @Mock
    private InviteUserService inviteUserServiceMock;
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

        inviteUserResource = new InviteUserResource(invitedUser, IFS_ADMINISTRATOR);
    }

    @Test
    public void saveUserInvite() throws Exception {
        when(inviteUserServiceMock.saveUserInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getRole())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/invite-user/save-invite")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteUserResource)))
                .andExpect(status().isOk());

        verify(inviteUserServiceMock).saveUserInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getRole());
    }

    @Test
    public void getInvite() throws Exception {
        when(inviteUserServiceMock.getInvite("SomeHashString")).thenReturn(serviceSuccess(new RoleInviteResource()));

        mockMvc.perform(get("/invite-user/get-invite/SomeHashString")).andExpect(status().isOk());

        verify(inviteUserServiceMock).getInvite("SomeHashString");

    }

    @Test
    public void checkExistingUser() throws Exception {
        when(inviteUserServiceMock.checkExistingUser("SomeHashString")).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/invite-user/check-existing-user/SomeHashString")).andExpect(status().isOk());

        verify(inviteUserServiceMock).checkExistingUser("SomeHashString");

    }

//    @Test
//    public void findPendingInternalUserInvites() throws Exception {
//        when(inviteUserServiceMock.findPendingInternalUserInvites(Mockito.any(PageRequest.class))).thenReturn(serviceSuccess(new RoleInvitePageResource()));
//
//        mockMvc.perform(get("/invite-user/internal/pending")).andExpect(status().isOk());
//
//        verify(inviteUserServiceMock).findPendingInternalUserInvites(Mockito.any(PageRequest.class));
//
//    }

    @Test
    public void findExternalInvites() throws Exception {

        String searchString = "%a%";
        SearchCategory searchCategory = SearchCategory.NAME;

        List<ExternalInviteResource> externalInviteResources = Collections.singletonList(new ExternalInviteResource());

        when(inviteUserServiceMock.findExternalInvites(searchString, searchCategory)).thenReturn(serviceSuccess(externalInviteResources));

        mockMvc.perform(get("/invite-user/find-external-invites?searchString=" + searchString + "&searchCategory=" + searchCategory.name()))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(externalInviteResources)));

        verify(inviteUserServiceMock).findExternalInvites(searchString, searchCategory);
    }
}

