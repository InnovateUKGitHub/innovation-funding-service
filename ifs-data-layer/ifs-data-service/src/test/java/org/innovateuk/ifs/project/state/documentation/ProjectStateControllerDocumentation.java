package org.innovateuk.ifs.project.state.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.innovateuk.ifs.project.state.controller.ProjectStateController;
import org.innovateuk.ifs.project.state.transactional.ProjectStateService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ProjectStateControllerDocumentation extends BaseControllerMockMVCTest<ProjectStateController> {

    @Mock
    private ProjectStateService projectStateService;

    @Override
    protected ProjectStateController supplyControllerUnderTest() {
        return new ProjectStateController(projectStateService);
    }

    @Test
    public void withdrawProject() throws Exception {
        long projectId = 456L;
        when(projectStateService.withdrawProject(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/withdraw", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void handleProjectOffline() throws Exception {
        long projectId = 456L;
        when(projectStateService.handleProjectOffline(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/handle-offline", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void completeProjectOffline() throws Exception {
        long projectId = 456L;
        when(projectStateService.completeProjectOffline(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/complete-offline", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void putProjectOnHold() throws Exception {
        long projectId = 456L;
        OnHoldReasonResource reason = new OnHoldReasonResource("Title", "Body");
        when(projectStateService.putProjectOnHold(projectId, reason)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/on-hold", projectId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reason))
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void resumeProject() throws Exception {
        long projectId = 456L;
        when(projectStateService.resumeProject(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/resume", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }
    @Test
    public void markAsSuccessful() throws Exception {
        long projectId = 456L;
        when(projectStateService.markAsSuccessful(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/successful", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void markAsUnsuccessful() throws Exception {
        long projectId = 456L;
        when(projectStateService.markAsUnsuccessful(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/unsuccessful", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }
}