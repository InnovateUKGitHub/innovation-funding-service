package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.documentation.InviteUserResourceDocs;
import org.innovateuk.ifs.documentation.PageResourceDocs;
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
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.commons.service.BaseRestService.buildPaginationUri;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.RoleInviteResourceBuilder.newRoleInviteResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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

        when(inviteUserServiceMock.saveUserInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getAdminRoleType())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/inviteUser/saveInvite")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteUserResource)))
                .andExpect(status().isOk())
                .andDo(document("inviteUser/saveInvite/{method-name}",
                        requestFields(InviteUserResourceDocs.inviteUserResourceFields)
                ));

        verify(inviteUserServiceMock).saveUserInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getAdminRoleType());
    }

    @Test
    public void getInvite() throws Exception {

        when(inviteUserServiceMock.getInvite("SomeHashString")).thenReturn(
                serviceSuccess(
                        newRoleInviteResource()
                                .withName("Arden Pimenta")
                                .withRoleId(1L)
                                .withEmail("example@test.com").withHash("SomeHashString")
                                .withRoleName("Project Finance").build()));

        mockMvc.perform(get("/inviteUser/getInvite/{inviteHash}", "SomeHashString"))
                .andExpect(status().isOk())
                .andDo(document("inviteUser/getInvite/{method-name}",
                        pathParameters(
                                parameterWithName("inviteHash").description("hash of the invite being requested")
                        ),
                        responseFields(InviteUserResourceDocs.roleInviteResourceFields)
                ));

        verify(inviteUserServiceMock).getInvite("SomeHashString");

    }

    @Test
    public void checkExistingUser() throws Exception {

        when(inviteUserServiceMock.checkExistingUser("SomeHashString")).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/inviteUser/checkExistingUser/{inviteHash}", "SomeHashString"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document("inviteUser/checkExistingUser/{method-name}",
                        pathParameters(
                                parameterWithName("inviteHash").description("hash of the invite being checked")
                        )
                ));

        verify(inviteUserServiceMock).checkExistingUser("SomeHashString");
    }

    @Test
    public void findPendingInternalUserInvites() throws Exception {
        RoleInvitePageResource roleInvitePageResource = buildRoleInvitePageResource();
        when(inviteUserServiceMock.findPendingInternalUserInvites(Mockito.any(PageRequest.class))).thenReturn(serviceSuccess(roleInvitePageResource));
        mockMvc.perform(get(buildPaginationUri("/inviteUser/internal/pending", 0, 5, null, new LinkedMultiValueMap<>()))).andExpect(status().isOk())
                .andDo(document("inviteUser/internal/pending/{method-name}",
                        responseFields(PageResourceDocs.pageResourceFields)
                ));
    }

    @Test
    public void findExternalInvites() throws Exception {

        String searchString = "a";
        SearchCategory searchCategory = SearchCategory.NAME;

        List<ExternalInviteResource> externalInviteResources = Collections.singletonList(new ExternalInviteResource());

        when(inviteUserServiceMock.findExternalInvites(searchString, searchCategory)).thenReturn(serviceSuccess(externalInviteResources));

        mockMvc.perform(get("/inviteUser/findExternalInvites?searchString=" + searchString + "&searchCategory=" + searchCategory.name()))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(externalInviteResources)))
                .andDo(document(
                        "inviteUser/findExternalInvites/{method-name}",
                        requestParameters(
                                parameterWithName("searchString").description("The string to search"),
                                parameterWithName("searchCategory").description("The category to search")
                        )
                        ,
                        responseFields(
                                fieldWithPath("[]").description("List of external pending invites with associated organisations, which contain the search string and match the search category")
                        )
                ));

        verify(inviteUserServiceMock).findExternalInvites(searchString, searchCategory);
    }

    @Test
    public void resendInternalUserInvite() throws Exception {

        when(inviteUserServiceMock.resendInternalUserInvite(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/inviteUser/internal/pending/{inviteId}/resend", 123L))
                .andExpect(status().isOk())
                .andDo(document("inviteUser/internal/pending/inviteId/{method-name}",
                        pathParameters(
                                parameterWithName("inviteId").description("The id of the pre-existing invite to resend")
                        )));

        verify(inviteUserServiceMock).resendInternalUserInvite(123L);
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

