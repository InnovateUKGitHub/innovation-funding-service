package com.worth.ifs.project.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.controller.ProjectController;
import com.worth.ifs.project.resource.ProjectResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.time.LocalDate;
import java.util.List;

import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_IN_LEAD_ORGANISATION;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.ProjectDocs.projectResourceBuilder;
import static com.worth.ifs.documentation.ProjectDocs.projectResourceFields;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectControllerDocumentation extends BaseControllerMockMVCTest<ProjectController> {

    private RestDocumentationResultHandler document;

    @Override
    protected ProjectController supplyControllerUnderTest() {
        return new ProjectController();
    }

    @Before
    public void setup(){
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void getProjectById() throws Exception {
        Long project1Id = 1L;
        ProjectResource testProjectResource1 = projectResourceBuilder.build();

        when(projectServiceMock.getProjectById(project1Id)).thenReturn(serviceSuccess(testProjectResource1));

        mockMvc.perform(get("/project/{id}", project1Id))
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("Id of the project that is being requested")
                        ),
                        responseFields(projectResourceFields)
                ));
    }

    @Test
    public void projectFindAll() throws Exception {
        int projectNumber = 3;
        List<ProjectResource> projects = projectResourceBuilder.build(projectNumber);
        when(projectServiceMock.findAll()).thenReturn(serviceSuccess(projects));

        mockMvc.perform(get("/project/").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(
                        this.document.snippets(
                        responseFields(
                                fieldWithPath("[]").description("List of projects the user is allowed to see")
                        )
                ));
    }

    @Test
    public void updateStartDate() throws Exception {

        when(projectServiceMock.updateProjectStartDate(123L, LocalDate.of(2017, 2, 1))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/startdate", 123L).
                param("projectStartDate", "2017-02-01"))
                .andExpect(status().isOk())
                .andDo(this.document);

        verify(projectServiceMock).updateProjectStartDate(123L, LocalDate.of(2017, 2, 1));
    }

    @Test
    public void updateStartDateButDateInPast() throws Exception {

        when(projectServiceMock.updateProjectStartDate(123L, LocalDate.of(2015, 1, 1))).thenReturn(serviceFailure(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE));

        mockMvc.perform(post("/project/{id}/startdate", 123L).
                param("projectStartDate", "2015-01-01"))
                .andExpect(status().isBadRequest())
                .andDo(this.document);
    }

    @Test
    public void updateStartDateButDateNotFirstOfMonth() throws Exception {

        when(projectServiceMock.updateProjectStartDate(123L, LocalDate.of(2015, 1, 5))).thenReturn(serviceFailure(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH));

        mockMvc.perform(post("/project/{id}/startdate", 123L).
                param("projectStartDate", "2015-01-05"))
                .andExpect(status().isBadRequest())
                .andDo(this.document);
    }
    
    @Test
    public void setProjectManager() throws Exception {
        Long project1Id = 1L;
        Long projectManagerId = 8L;

        when(projectServiceMock.setProjectManager(project1Id, projectManagerId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/project-manager/{projectManagerId}", project1Id, projectManagerId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("Id of the project"),
                                parameterWithName("projectManagerId").description("User id of the project manager being assigned")
                        )
                ));
    }

    @Test
    public void setProjectManagerButInvalidProjectManager() throws Exception {
        Long project1Id = 1L;
        Long projectManagerId = 8L;

        when(projectServiceMock.setProjectManager(project1Id, projectManagerId)).thenReturn(serviceFailure(PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_IN_LEAD_ORGANISATION));

        mockMvc.perform(post("/project/{id}/project-manager/{projectManagerId}", project1Id, projectManagerId))
                .andExpect(status().isBadRequest())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("Id of the project"),
                                parameterWithName("projectManagerId").description("User id of the project manager being assigned")
                        )
                ));
    }
}
