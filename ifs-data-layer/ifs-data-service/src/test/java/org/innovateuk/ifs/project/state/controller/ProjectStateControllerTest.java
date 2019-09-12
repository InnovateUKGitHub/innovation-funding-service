package org.innovateuk.ifs.project.state.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.innovateuk.ifs.project.state.transactional.ProjectStateService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_CANNOT_BE_WITHDRAWN;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectStateControllerTest extends BaseControllerMockMVCTest<ProjectStateController> {

    @Mock
    private ProjectStateService projectStateService;

    @Override
    protected ProjectStateController supplyControllerUnderTest() {
        return new ProjectStateController(projectStateService);
    }

    @Test
    public void testWithdrawProject() throws Exception {
        long projectId = 456L;
        when(projectStateService.withdrawProject(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/withdraw", projectId))
                .andExpect(status().isOk());

        verify(projectStateService).withdrawProject(projectId);
    }

    @Test
    public void testWithdrawProjectFails() throws Exception {
        long projectId = 789L;
        when(projectStateService.withdrawProject(projectId)).thenReturn(serviceFailure(PROJECT_CANNOT_BE_WITHDRAWN));

        mockMvc.perform(post("/project/{projectId}/withdraw", projectId))
                .andExpect(status().isBadRequest());

        verify(projectStateService, only()).withdrawProject(projectId);
    }

    @Test
    public void testWithdrawProjectWhenProjectDoesntExist() throws Exception {
        long projectId = 432L;
        when(projectStateService.withdrawProject(projectId)).thenReturn(serviceFailure(GENERAL_NOT_FOUND));

        mockMvc.perform(post("/project/{projectId}/withdraw", projectId))
                .andExpect(status().isNotFound());

        verify(projectStateService, only()).withdrawProject(projectId);
    }

    @Test
    public void handleProjectOffline() throws Exception {
        long projectId = 456L;
        when(projectStateService.handleProjectOffline(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/handle-offline", projectId))
                .andExpect(status().isOk());

        verify(projectStateService, only()).handleProjectOffline(projectId);
    }

    @Test
    public void completeProjectOffline() throws Exception {
        long projectId = 456L;
        when(projectStateService.completeProjectOffline(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/complete-offline", projectId))
                .andExpect(status().isOk());

        verify(projectStateService, only()).completeProjectOffline(projectId);
    }

    @Test
    public void putProjectOnHold() throws Exception {
        long projectId = 456L;
        OnHoldReasonResource reason =  new OnHoldReasonResource("Title", "Body");
        when(projectStateService.putProjectOnHold(projectId, reason)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/on-hold", projectId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reason)))
                .andExpect(status().isOk());

        verify(projectStateService).putProjectOnHold(projectId, reason);
    }

    @Test
    public void resumeProject() throws Exception {
        long projectId = 456L;
        when(projectStateService.resumeProject(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/resume", projectId))
                .andExpect(status().isOk());

        verify(projectStateService).resumeProject(projectId);
    }
    @Test
    public void markAsSuccessful() throws Exception {
        long projectId = 456L;
        when(projectStateService.markAsSuccessful(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/successful", projectId))
                .andExpect(status().isOk());

        verify(projectStateService).markAsSuccessful(projectId);
    }

    @Test
    public void markAsUnsuccessful() throws Exception {
        long projectId = 456L;
        when(projectStateService.markAsUnsuccessful(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/unsuccessful", projectId))
                .andExpect(status().isOk());

        verify(projectStateService).markAsUnsuccessful(projectId);
    }
}

