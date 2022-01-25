package org.innovateuk.ifs.project.state.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.innovateuk.ifs.project.state.controller.ProjectStateController;
import org.innovateuk.ifs.project.state.transactional.ProjectStateService;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class ProjectStateControllerDocumentation extends BaseControllerMockMVCTest<ProjectStateController> {

    private long projectId = 456L;

    @Mock
    private ProjectStateService projectStateService;

    @Override
    protected ProjectStateController supplyControllerUnderTest() {
        return new ProjectStateController(projectStateService);
    }

    @Test
    public void withdrawProject() throws Exception {
        when(projectStateService.withdrawProject(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/withdraw", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void handleProjectOffline() throws Exception {
        when(projectStateService.handleProjectOffline(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/handle-offline", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void completeProjectOffline() throws Exception {
        when(projectStateService.completeProjectOffline(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/complete-offline", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void putProjectOnHold() throws Exception {
        OnHoldReasonResource reason = new OnHoldReasonResource("Title", "Body");
        when(projectStateService.putProjectOnHold(projectId, reason)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/on-hold", projectId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reason))
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void resumeProject() throws Exception {
        when(projectStateService.resumeProject(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/resume", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }
    @Test
    public void markAsSuccessful() throws Exception {
        when(projectStateService.markAsSuccessful(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/successful", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void markAsUnsuccessful() throws Exception {
        when(projectStateService.markAsUnsuccessful(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/unsuccessful", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void markLoansProjectAsSuccessful() throws Exception {
        LocalDate now = LocalDate.now();
        LocalDate projectStartDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1).plusMonths(1);

        when(projectStateService.markAsSuccessful(projectId, projectStartDate)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/loans-successful", projectId)
                .param("projectStartDate", String.valueOf(projectStartDate))
                .header("IFS_AUTH_TOKEN", "123abc"));

    }
}