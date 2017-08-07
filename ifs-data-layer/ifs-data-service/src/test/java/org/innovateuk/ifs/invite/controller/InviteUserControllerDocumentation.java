package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.documentation.InviteUserResourceDocs;
import org.innovateuk.ifs.documentation.PageResourceDocs;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.LinkedMultiValueMap;

import static org.innovateuk.ifs.commons.service.BaseRestService.buildPaginationUri;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.RoleInviteResourceBuilder.newRoleInviteResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InviteUserControllerDocumentation extends BaseControllerMockMVCTest<InviteUserController> {

    private InviteUserResource inviteUserResource;

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

        inviteUserResource = new InviteUserResource(invitedUser, UserRoleType.IFS_ADMINISTRATOR);
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
                ));;

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
                ));;
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

