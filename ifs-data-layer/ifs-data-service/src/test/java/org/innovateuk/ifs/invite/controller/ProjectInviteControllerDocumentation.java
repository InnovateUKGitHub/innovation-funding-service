package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.invite.transactional.ProjectInviteService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ProjectInviteDocs.PROJECT_USER_INVITE_RESOURCE_BUILDER;
import static org.innovateuk.ifs.documentation.ProjectInviteDocs.projectInviteFieldsList;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
                andExpect(status().isOk());

    }

    @Test
    public void getInvitesByProject() throws Exception {

        Long projectId = 123L;

        List<ProjectUserInviteResource> projectInvites = PROJECT_USER_INVITE_RESOURCE_BUILDER.build(2);

        when(projectInviteServiceMock.getInvitesByProject(projectId)).thenReturn(serviceSuccess(projectInvites));

        mockMvc.perform(get("/project-invite/get-invites-by-project-id/{projectId}", projectId)).
                andExpect(status().isOk());
    }

    @Test
    public void acceptInvite() throws Exception {

        String hash = "has545967h";
        Long userId = 123L;

        when(projectInviteServiceMock.acceptProjectInvite(hash, userId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/project-invite/accept-invite/{hash}/{userId}", hash,userId)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkExistingUser() throws Exception {

        String hash = "has545967h";

        when(projectInviteServiceMock.checkUserExistsForInvite(hash)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/project-invite/check-existing-user/{hash}", hash)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserByInviteHash() throws Exception {

        when(projectInviteServiceMock.getUserByInviteHash("asdf1234")).thenReturn(serviceSuccess(newUserResource().build()));

        mockMvc.perform(get("/project-invite/get-user/{hash}", "asdf1234")).
                andExpect(status().isOk());

    }
}
