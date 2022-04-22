package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.transactional.InviteUserService;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.commons.service.BaseRestService.buildPaginationUri;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.RoleInviteResourceBuilder.newRoleInviteResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InviteUserControllerDocumentation extends BaseControllerMockMVCTest<InviteUserController> {

    private InviteUserResource inviteUserResource;

    @Mock
    private InviteUserService inviteUserServiceMock;

    @Override
    protected InviteUserController supplyControllerUnderTest() {
        return new InviteUserController();
    }

    @Before
    public void setup() {
        UserResource invitedUser = UserResourceBuilder.newUserResource()
                .withFirstName("A")
                .withLastName("D")
                .withEmail("A.D@gmail.com")
                .build();

        inviteUserResource = new InviteUserResource(invitedUser, Role.IFS_ADMINISTRATOR);
    }

    @Test
    public void saveUserInvite() throws Exception {

        when(inviteUserServiceMock.saveUserInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getRole(), inviteUserResource.getOrganisation(), inviteUserResource.getInnovationAreaId())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/invite-user/save-invite")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteUserResource)))
                .andExpect(status().isOk());

        verify(inviteUserServiceMock).saveUserInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getRole(), inviteUserResource.getOrganisation(), inviteUserResource.getInnovationAreaId());
    }

    @Test
    public void getInvite() throws Exception {

        when(inviteUserServiceMock.getInvite("SomeHashString")).thenReturn(
                serviceSuccess(
                        newRoleInviteResource()
                                .withName("Arden Pimenta")
                                .withEmail("example@test.com")
                                .withHash("SomeHashString")
                                .withRole(Role.IFS_ADMINISTRATOR)
                                .build()));

        mockMvc.perform(get("/invite-user/get-invite/{inviteHash}", "SomeHashString")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(inviteUserServiceMock).getInvite("SomeHashString");
    }

    @Test
    public void checkExistingUser() throws Exception {

        when(inviteUserServiceMock.checkExistingUser("SomeHashString")).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/invite-user/check-existing-user/{inviteHash}", "SomeHashString")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(inviteUserServiceMock).checkExistingUser("SomeHashString");
    }

    @Test
    public void findPendingInternalUserInvites() throws Exception {
        RoleInvitePageResource roleInvitePageResource = buildRoleInvitePageResource();
        when(inviteUserServiceMock.findPendingInternalUserInvites(anyString(), any(PageRequest.class))).thenReturn(serviceSuccess(roleInvitePageResource));
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", "");
        mockMvc.perform(get(buildPaginationUri("/invite-user/internal/pending", 0, 5, null, params))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void findExternalInvites() throws Exception {

        String searchString = "a";
        SearchCategory searchCategory = SearchCategory.NAME;

        List<ExternalInviteResource> externalInviteResources = Collections.singletonList(new ExternalInviteResource());

        when(inviteUserServiceMock.findExternalInvites(searchString, searchCategory)).thenReturn(serviceSuccess(externalInviteResources));

        mockMvc.perform(get("/invite-user/find-external-invites?searchString=" + searchString + "&searchCategory=" + searchCategory.name())
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(externalInviteResources)));

        verify(inviteUserServiceMock).findExternalInvites(searchString, searchCategory);
    }

    @Test
    public void resendInternalUserInvite() throws Exception {

        when(inviteUserServiceMock.resendInvite(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/invite-user/internal/pending/{inviteId}/resend", 123L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(inviteUserServiceMock).resendInvite(123L);
    }

    private RoleInvitePageResource buildRoleInvitePageResource() {

        RoleInvitePageResource pageResource = new RoleInvitePageResource();
        pageResource.setNumber(5);
        pageResource.setSize(5);
        pageResource.setTotalElements(10);
        pageResource.setTotalPages(2);
        pageResource.setContent(newRoleInviteResource().withEmail("example@innovateuk.test").build(5));
        return pageResource;
    }
}

