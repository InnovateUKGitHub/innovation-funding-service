package org.innovateuk.ifs.project.core.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.documentation.AddressDocs;
import org.innovateuk.ifs.documentation.ProjectUserResourceDocs;
import org.innovateuk.ifs.project.core.controller.ProjectController;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ProjectDocs.projectResourceBuilder;
import static org.innovateuk.ifs.documentation.ProjectDocs.projectResourceFields;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_USER_ROLES;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectControllerDocumentation extends BaseControllerMockMVCTest<ProjectController> {

    @Mock
    private ProjectService projectServiceMock;

    @Override
    protected ProjectController supplyControllerUnderTest() {
        return new ProjectController();
    }

    @Test
    public void getProjectById() throws Exception {
        Long project1Id = 1L;
        ProjectResource testProjectResource1 = projectResourceBuilder.build();

        when(projectServiceMock.getProjectById(project1Id)).thenReturn(serviceSuccess(testProjectResource1));

        mockMvc.perform(get("/project/{id}", project1Id)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the project that is being requested")
                        ),
                        responseFields(projectResourceFields)
                        .andWithPrefix("address.", AddressDocs.addressResourceFields)
                ));
    }

    @Test
    public void projectFindAll() throws Exception {
        int projectNumber = 3;
        List<ProjectResource> projects = projectResourceBuilder.build(projectNumber);
        when(projectServiceMock.findAll()).thenReturn(serviceSuccess(projects));

        mockMvc.perform(get("/project/").contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(
                        document("project/{method-name}",
                                responseFields(
                                        fieldWithPath("[]").description("List of projects the user is allowed to see")
                                ).andWithPrefix("[].", projectResourceFields)
                                .andWithPrefix("[].address.", AddressDocs.addressResourceFields)
                        ));
    }

    @Test
    public void getProjectUsers() throws Exception {

        List<ProjectUserResource> projectUsers = newProjectUserResource().build(3);

        when(projectServiceMock.getProjectUsersByProjectIdAndRoleIn(123L, PROJECT_USER_ROLES.stream().collect(Collectors.toList()))).thenReturn(serviceSuccess(projectUsers));

        mockMvc.perform(get("/project/{projectId}/project-users", 123L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectUsers)))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project that the Project Users are being requested from")
                        ),
                        responseFields(fieldWithPath("[]").description("List of Project Users the user is allowed to see"))
                        .andWithPrefix("[].", ProjectUserResourceDocs.projectUserResourceFields)
                ));
    }

    @Test
    public void createProjectFromApplication() throws Exception {
        Long applicationId = 1L;
        ProjectResource expectedProject = projectResourceBuilder.build();

        when(projectServiceMock.createProjectFromApplication(applicationId)).thenReturn(serviceSuccess(expectedProject));

        mockMvc.perform(post("/project/create-project/application/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to turn into a project")
                        ),
                                responseFields(projectResourceFields)
                                        .andWithPrefix("address.", AddressDocs.addressResourceFields)
                ));
    }

    @Test
    public void existsOnApplication() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        when(projectServiceMock.existsOnApplication(projectId, organisationId)).thenReturn(serviceSuccess(Boolean.TRUE));

        mockMvc.perform(get("/project/{projectId}/user/{organisationId}/application-exists", projectId, organisationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(true)))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project"),
                                parameterWithName("organisationId").description("Id of the organisation on the project")
                        )
                ));
    }
}