package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.documentation.InviteUserResourceDocs;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.AdminRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InviteUserControllerDocumentation extends BaseControllerMockMVCTest<InviteUserController> {
    private RestDocumentationResultHandler document;

    private InviteUserResource inviteUserResource;

    @Override
    protected InviteUserController supplyControllerUnderTest() {
        return new InviteUserController();
    }

    @Before
    public void setup() {
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));

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

        mockMvc.perform(MockMvcRequestBuilders.post("/inviteUser/saveInvite")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteUserResource)))
                .andExpect(status().isOk())
                .andDo(document("inviteUser/saveInvite/{method-name}",
                        requestFields(InviteUserResourceDocs.inviteUserResourceFields)
                ));

        verify(inviteUserServiceMock).saveUserInvite(inviteUserResource.getInvitedUser(), inviteUserResource.getAdminRoleType());
    }
}

