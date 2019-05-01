package org.innovateuk.ifs.project.projectteam.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.project.projectteam.transactional.ProjectTeamService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectTeamControllerTest extends BaseControllerMockMVCTest<ProjectTeamController> {

    @Mock
    private ProjectTeamService projectTeamService;

    @Override
    protected ProjectTeamController supplyControllerUnderTest() {
        return new ProjectTeamController();
    }

    @Test
    public void inviteTeamMember() throws Exception {
        long projectId = 1L;
        ProjectUserInviteResource invite = newProjectUserInviteResource().build();
        when(projectTeamService.inviteTeamMember(projectId, invite)).thenReturn(serviceSuccess());

        mockMvc.perform(post(String.format("/project/%d/team/invite", projectId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invite)))
                .andExpect(status().isOk());
    }

}
