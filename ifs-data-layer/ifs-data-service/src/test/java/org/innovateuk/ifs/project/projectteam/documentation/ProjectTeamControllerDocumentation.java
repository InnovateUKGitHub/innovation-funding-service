package org.innovateuk.ifs.project.projectteam.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.project.projectteam.controller.ProjectTeamController;
import org.innovateuk.ifs.project.projectteam.transactional.ProjectTeamService;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ProjectTeamControllerDocumentation extends BaseControllerMockMVCTest<ProjectTeamController> {

    @Mock
    private ProjectTeamService projectTeamService;

    @Override
    protected ProjectTeamController supplyControllerUnderTest() {
        return new ProjectTeamController(projectTeamService);
    }

    @Test
    public void removeUser() throws Exception {
        long projectId = 123L;
        long userId = 456L;
        ProjectUserCompositeId composite = new ProjectUserCompositeId(projectId, userId);

        when(projectTeamService.removeUser(composite)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/team/remove-user/{userId}", projectId, userId))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                                pathParameters(
                                        parameterWithName("projectId").description("Id of project the user will be removed from"),
                                        parameterWithName("userId").description("Id of the user to be removed from the project"))));

        verify(projectTeamService).removeUser(composite);
    }

    @Test
    public void removeInvite() throws Exception {
        long projectId = 456L;
        long inviteId = 789L;
        ProjectUserCompositeId composite = new ProjectUserCompositeId(projectId, inviteId);

        when(projectTeamService.removeUser(composite)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/team/remove-invite/{inviteId}", projectId, inviteId))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                                pathParameters(
                                        parameterWithName("projectId").description("Id of project the user was invited to"),
                                        parameterWithName("userId").description("Id of the invite to be removed"))));

        verify(projectTeamService).removeInvite(composite);
    }

    @Test
    public void inviteTeamMember() throws Exception {
        Long projectId = 123L;
        ProjectUserInviteResource invite = newProjectUserInviteResource().build();
        when(projectTeamService.inviteTeamMember(projectId, invite)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/team/invite", projectId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(toJson(invite)))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project that the project member is being invited to")
                        )
                ));
    }
}
