package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.invite.transactional.InviteUserService;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;
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
    private InviteUserService inviteUserService;
    
    @Mock
    private UserService userService;
    @Override
    protected InviteUserController supplyControllerUnderTest() {
        return new InviteUserController();
    }

    @Before
    public void setUp() {
        UserResource invitedUser = newUserResource()
                .withFirstName("A")
                .withLastName("D")
                .withEmail("A.D@gmail.com")
                .build();

        inviteUserResource = new InviteUserResource(invitedUser, IFS_ADMINISTRATOR);
    }

    @Test
    public void saveUserInvite() throws Exception {
        when(inviteUserService.saveUserInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getRole(), inviteUserResource.getOrganisation())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/invite-user/save-invite")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteUserResource)))
                .andExpect(status().isOk());

        verify(inviteUserService).saveUserInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getRole(), inviteUserResource.getOrganisation());
    }

    @Test
    public void getInvite() throws Exception {
        when(inviteUserService.getInvite("SomeHashString")).thenReturn(serviceSuccess(new RoleInviteResource()));

        mockMvc.perform(get("/invite-user/get-invite/SomeHashString")).andExpect(status().isOk());

        verify(inviteUserService).getInvite("SomeHashString");

    }

    @Test
    public void checkExistingUser() throws Exception {
        when(inviteUserService.checkExistingUser("SomeHashString")).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/invite-user/check-existing-user/SomeHashString")).andExpect(status().isOk());

        verify(inviteUserService).checkExistingUser("SomeHashString");

    }

    @Test
    public void findExternalInvites() throws Exception {

        String searchString = "%a%";
        SearchCategory searchCategory = SearchCategory.NAME;

        List<ExternalInviteResource> externalInviteResources = singletonList(new ExternalInviteResource());

        when(inviteUserService.findExternalInvites(searchString, searchCategory)).thenReturn(serviceSuccess(externalInviteResources));

        mockMvc.perform(get("/invite-user/find-external-invites?searchString=" + searchString + "&searchCategory=" + searchCategory.name()))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(externalInviteResources)));

        verify(inviteUserService).findExternalInvites(searchString, searchCategory);
    }

    @Test
    public void saveAssessorInvite() throws Exception {

        InnovationArea innovationArea = newInnovationArea().withName("innovation area").build();
        inviteUserResource.setRole(ASSESSOR);
        inviteUserResource.setInnovationAreaId(innovationArea.getId());

        when(inviteUserService.saveAssessorInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getRole(), inviteUserResource.getInnovationAreaId())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/invite-user/save-invite")
                        .contentType(APPLICATION_JSON)
                        .content(toJson(inviteUserResource)))
                .andExpect(status().isOk());

        verify(inviteUserService).saveAssessorInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getRole(), inviteUserResource.getInnovationAreaId());
    }

    @Test
    public void findExternalInvitesByEmail() throws Exception {

        String email = "bob@email.com%";
        SearchCategory searchCategory = SearchCategory.NAME;
        UserResource user = newUserResource().withEmail(email).build();

        List<RoleInviteResource> roleInviteResources = singletonList(new RoleInviteResource());

        when(userService.findByEmail(email)).thenReturn(serviceSuccess(user));
        when(inviteUserService.findExternalInvitesByUser(user)).thenReturn(serviceSuccess(roleInviteResources));

        mockMvc.perform(get("/invite-user/get-by-email/" + email))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(roleInviteResources)));

        verify(inviteUserService).findExternalInvitesByUser(user);
    }
}

