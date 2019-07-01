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
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class ProjectStateControllerDocumentation extends BaseControllerMockMVCTest<ProjectStateController> {

    @Mock
    private ProjectStateService projectStateService;

    @Override
    protected ProjectStateController supplyControllerUnderTest() {
        return new ProjectStateController(projectStateService);
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

    @Test
    public void putProjectOnHold() throws Exception {
        Long projectId = 456L;
        OnHoldReasonResource reason = new OnHoldReasonResource("Title", "Body");
        when(projectStateService.putProjectOnHold(projectId, reason)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/on-hold", projectId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reason))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to put on hold")
                        ),
                        requestFields(
                                fieldWithPath("title").description("Title of the reason the project is being put on hold"),
                                fieldWithPath("body").description("Body of the reason the project is being put on hold")
                        )
                ));
    }

    @Test
    public void resumeProject() throws Exception {
        Long projectId = 456L;
        when(projectStateService.resumeProject(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/resume", projectId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to resume from on hold")
                        )
                ));
    }
}