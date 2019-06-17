package org.innovateuk.ifs.project.core.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.core.controller.ProjectStateController;
import org.innovateuk.ifs.project.core.transactional.ProjectStateService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class ProjectStateControllerDocumentation extends BaseControllerMockMVCTest<ProjectStateController> {

    @Mock
    private ProjectStateService projectStateService;

    @Override
    protected ProjectStateController supplyControllerUnderTest() {
        return new ProjectStateController();
    }

    @Test
    public void withdrawProject() throws Exception {
        Long projectId = 456L;
        when(projectStateService.withdrawProject(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/withdraw", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to withdraw")
                        )
                ));
    }

    @Test
    public void handleProjectOffline() throws Exception {
        Long projectId = 456L;
        when(projectStateService.handleProjectOffline(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/handle-offline", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to handle offline")
                        )
                ));
    }

    @Test
    public void completeProjectOffline() throws Exception {
        Long projectId = 456L;
        when(projectStateService.completeProjectOffline(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/complete-offline", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to complete offline")
                        )
                ));
    }
}