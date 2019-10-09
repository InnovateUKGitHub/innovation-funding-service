package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.invite.transactional.ProjectInviteService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ProjectInviteDocs.projectInviteFields;
import static org.innovateuk.ifs.documentation.ProjectInviteDocs.projectInviteFieldsList;
import static org.innovateuk.ifs.documentation.ProjectInviteDocs.PROJECT_USER_INVITE_RESOURCE_BUILDER;
import static org.innovateuk.ifs.documentation.UserDocs.userResourceFields;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectInviteControllerDocumentation extends BaseControllerMockMVCTest<ProjectInviteController> {

    @Mock
    private ProjectInviteService projectInviteServiceMock;

    @Override
    protected ProjectInviteController supplyControllerUnderTest() {
        return new ProjectInviteController();
    }

    @Test
    public void getByHash() throws Exception {

        ProjectUserInviteResource projectInvite = PROJECT_USER_INVITE_RESOURCE_BUILDER.build();

        when(projectInviteServiceMock.getInviteByHash(projectInvite.getHash())).thenReturn(serviceSuccess(projectInvite));

        mockMvc.perform(get("/project-invite/get-project-invite-by-hash/{hash}", projectInvite.getHash())).
                andExpect(status().isOk()).
                andDo(document("project-invite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("Hash of the Project Invite that is being retrieved")
                        ),
                        responseFields(projectInviteFields)
                ));

    }

    @Test
    public void getInvitesByProject() throws Exception {

        Long projectId = 123L;

        List<ProjectUserInviteResource> projectInvites = PROJECT_USER_INVITE_RESOURCE_BUILDER.build(2);

        when(projectInviteServiceMock.getInvitesByProject(projectId)).thenReturn(serviceSuccess(projectInvites));

        mockMvc.perform(get("/project-invite/get-invites-by-project-id/{projectId}", projectId)).
                andExpect(status().isOk()).
                andDo(document("project-invite/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("The id of the Project for which we are retrieving Project Invites")
                        ),
                        responseFields(projectInviteFieldsList)
                ));
    }

    @Test
    public void acceptInvite() throws Exception {

        String hash = "has545967h";
        Long userId = 123L;

        when(projectInviteServiceMock.acceptProjectInvite(hash, userId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/project-invite/accept-invite/{hash}/{userId}", hash,userId)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("project-invite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("The hash of the Project Invite being accepted"),
                                parameterWithName("userId").description("The id of the User accepting the Project Invite")
                        )
                ));
    }

    @Test
    public void checkExistingUser() throws Exception {

        String hash = "has545967h";

        when(projectInviteServiceMock.checkUserExistsForInvite(hash)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/project-invite/check-existing-user/{hash}", hash)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("project-invite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("The hash of the Project Invite being inspected for the presence of an existing User")
                        )
                ));
    }

    @Test
    public void getUserByInviteHash() throws Exception {

        when(projectInviteServiceMock.getUserByInviteHash("asdf1234")).thenReturn(serviceSuccess(newUserResource().build()));

        mockMvc.perform(get("/project-invite/get-user/{hash}", "asdf1234")).
                andExpect(status().isOk()).
                andDo(document("project-invite/{method-name}",
                        pathParameters(
                                parameterWithName("hash").description("Hash of the Project Invite that the User is being retrieved from")
                        ),
                        responseFields(userResourceFields)
                ));

    }
}
