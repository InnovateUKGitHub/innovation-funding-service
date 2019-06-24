package org.innovateuk.ifs.project.core.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.core.transactional.ProjectStateServiceImpl;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_CANNOT_BE_WITHDRAWN;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectStateControllerTest extends BaseControllerMockMVCTest<ProjectStateController> {

    @Mock
    private ProjectStateServiceImpl projectStateService;

    @Override
    protected ProjectStateController supplyControllerUnderTest() {
        return new ProjectStateController(projectStateService);
    }

    @Test
    public void testWithdrawProject() throws Exception {
        Long projectId = 456L;
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
}

