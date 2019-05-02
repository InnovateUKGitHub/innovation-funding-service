package org.innovateuk.ifs.project.projectteam.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.projectteam.transactional.ProjectTeamService;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectTeamControllerTest extends BaseControllerMockMVCTest<ProjectTeamController> {


    @Override
    protected ProjectTeamController supplyControllerUnderTest() {
        return new ProjectTeamController();
    }

    @Mock
    private ProjectTeamService projectTeamService;

    @Test
    public void removeUser() throws Exception {
        long projectId = 123L;
        long userId = 456L;
        ProjectUserCompositeId composite = new ProjectUserCompositeId(projectId, userId);

        when(projectTeamService.removeUser(composite)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/remove-user/{userId}", projectId, userId))
                .andExpect(status().isOk());

        verify(projectTeamService).removeUser(composite);
    }
}
