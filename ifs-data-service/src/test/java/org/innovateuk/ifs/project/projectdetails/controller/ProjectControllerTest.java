package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.transactional.ProjectService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectStatusResourceBuilder.newProjectStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static java.util.Collections.emptyMap;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectControllerTest extends BaseControllerMockMVCTest<ProjectController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setUpDocumentation() throws Exception {
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Override
    protected ProjectController supplyControllerUnderTest() {
        return new ProjectController();
    }

    @Test
    public void projectControllerShouldReturnProjectById() throws Exception {
        Long project1Id = 1L;
        Long project2Id = 2L;

        ProjectResource testProjectResource1 = newProjectResource().withId(project1Id).build();
        ProjectResource testProjectResource2 = newProjectResource().withId(project2Id).build();

        when(projectServiceMock.getProjectById(project1Id)).thenReturn(serviceSuccess(testProjectResource1));
        when(projectServiceMock.getProjectById(project2Id)).thenReturn(serviceSuccess(testProjectResource2));

        mockMvc.perform(get("/project/{id}", project1Id))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(testProjectResource1)));

        mockMvc.perform(get("/project/2"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(testProjectResource2)));
    }

    @Test
    public void projectControllerShouldReturnStatusByProjectId() throws Exception {
        Long projectId = 2L;

        ProjectStatusResource projectStatusResource = newProjectStatusResource().build();

        when(projectStatusServiceMock.getProjectStatusByProjectId(projectId)).thenReturn(serviceSuccess(projectStatusResource));

        mockMvc.perform(get("/project/{id}/status", projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectStatusResource)));
    }

    @Test
    public void projectControllerShouldReturnAllProjects() throws Exception {
        int projectNumber = 3;
        List<ProjectResource> projects = newProjectResource().build(projectNumber);
        when(projectServiceMock.findAll()).thenReturn(serviceSuccess(projects));

        mockMvc.perform(get("/project/").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(projectNumber)));
    }

    @Test
    public void projectControllerSetProjectManager() throws Exception {
    	when(projectServiceMock.setProjectManager(3L, 5L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/3/project-manager/5").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(projectServiceMock).setProjectManager(3L, 5L);
    }

    @Test
    public void updateFinanceContact() throws Exception {

        when(projectServiceMock.updateFinanceContact(new ProjectOrganisationCompositeId(123L, 456L), 789L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/finance-contact?financeContact=789", 123L, 456L))
                .andExpect(status().isOk());

        verify(projectServiceMock).updateFinanceContact(new ProjectOrganisationCompositeId(123L, 456L), 789L);
    }

    @Test
    public void getProjectUsers() throws Exception {

        List<ProjectUserResource> projectUsers = newProjectUserResource().build(3);

        when(projectServiceMock.getProjectUsers(123L)).thenReturn(serviceSuccess(projectUsers));

        mockMvc.perform(get("/project/{projectId}/project-users", 123L)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(projectUsers)));
    }

    @Test
    public void updateProjectAddress() throws Exception {
        AddressResource addressResource = newAddressResource().withId(1L).build();

        when(projectServiceMock.updateProjectAddress(123L, 456L, OrganisationAddressType.REGISTERED, addressResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/address", 456L)
                .param("leadOrganisationId", "123")
                .param("addressType", OrganisationAddressType.REGISTERED.name())
                .contentType(APPLICATION_JSON)
                .content(toJson(addressResource)))
            .andExpect(status().isOk())
            .andExpect(content().string(""));

        verify(projectServiceMock).updateProjectAddress(123L, 456L, OrganisationAddressType.REGISTERED, addressResource);
    }

    @Test
    public void isSubmitAllowed() throws Exception {
        when(projectServiceMock.isSubmitAllowed(123L)).thenReturn(serviceSuccess(true));

        mockMvc.perform(get("/project/{projectId}/isSubmitAllowed", 123L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andReturn();
    }

    @Test
    public void isSubmitAllowedFalse() throws Exception {
        when(projectServiceMock.isSubmitAllowed(123L)).thenReturn(serviceSuccess(false));

        mockMvc.perform(get("/project/{projectId}/isSubmitAllowed", 123L))
                .andExpect(status().isOk())
                .andExpect(content().string("false"))
                .andReturn();
    }

    @Test
    public void setApplicationDetailsSubmitted() throws Exception {
        when(projectServiceMock.submitProjectDetails(isA(Long.class), isA(ZonedDateTime.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/setApplicationDetailsSubmitted", 123L))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void addCollaborationAgreement() throws Exception {

        Long projectId = 123L;

        BiFunction<ProjectService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createCollaborationAgreementFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/collaboration-agreement", projectServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod(document));
    }

    @Test
    public void updateCollaborationAgreement() throws Exception {

        Long projectId = 123L;

        BiFunction<ProjectService, FileEntryResource, ServiceResult<Void>> serviceCallToUpload =
                (service, fileToUpload) -> service.updateCollaborationAgreementFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUpdateProcess("/project/" + projectId + "/collaboration-agreement", projectServiceMock, serviceCallToUpload).
                andDo(documentFileUpdateMethod(document));
    }

    @Test
    public void getCollaborationAgreementFileDetails() throws Exception {

        Long projectId = 123L;

        Function<ProjectService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getCollaborationAgreementFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/collaboration-agreement/details", new Object[] {projectId}, emptyMap(),
                projectServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod(document));
    }

    @Test
    public void getCollaborationAgreementFileContent() throws Exception {

        Long projectId = 123L;

        Function<ProjectService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getCollaborationAgreementFileContents(projectId);

        assertGetFileContents("/project/{projectId}/collaboration-agreement", new Object[] {projectId},
                emptyMap(), projectServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod(document));
    }

    @Test
    public void deleteCollaborationAgreement() throws Exception {

        Long projectId = 123L;

        Function<ProjectService, ServiceResult<Void>> serviceCallToDelete =
                service -> service.deleteCollaborationAgreementFile(projectId);

        assertDeleteFile("/project/{projectId}/collaboration-agreement", new Object[] {projectId},
                emptyMap(), projectServiceMock, serviceCallToDelete).
                andDo(documentFileDeleteMethod(document));
    }



    @Test
    public void addExploitationPlan() throws Exception {

        Long projectId = 123L;

        BiFunction<ProjectService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createExploitationPlanFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/exploitation-plan", projectServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod(document));
    }

    @Test
    public void updateExploitationPlan() throws Exception {

        Long projectId = 123L;

        BiFunction<ProjectService, FileEntryResource, ServiceResult<Void>> serviceCallToUpload =
                (service, fileToUpload) -> service.updateExploitationPlanFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUpdateProcess("/project/" + projectId + "/exploitation-plan", projectServiceMock, serviceCallToUpload).
                andDo(documentFileUpdateMethod(document));
    }

    @Test
    public void getExploitationPlanFileDetails() throws Exception {

        Long projectId = 123L;

        Function<ProjectService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getExploitationPlanFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/exploitation-plan/details", new Object[] {projectId}, emptyMap(),
                projectServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod(document));
    }

    @Test
    public void getExploitationPlanFileContent() throws Exception {

        Long projectId = 123L;

        Function<ProjectService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getExploitationPlanFileContents(projectId);

        assertGetFileContents("/project/{projectId}/exploitation-plan", new Object[] {projectId},
                emptyMap(), projectServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod(document));
    }

    @Test
    public void deleteExploitationPlan() throws Exception {

        Long projectId = 123L;

        Function<ProjectService, ServiceResult<Void>> serviceCallToDelete =
                service -> service.deleteExploitationPlanFile(projectId);

        assertDeleteFile("/project/{projectId}/exploitation-plan", new Object[] {projectId},
                emptyMap(), projectServiceMock, serviceCallToDelete).
                andDo(documentFileDeleteMethod(document));
    }

    @Test
    public void acceptOrRejectOtherDocuments() throws Exception {
        when(projectServiceMock.acceptOrRejectOtherDocuments(1L, true)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/1/partner/documents/approved/{approved}", true).
                contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(projectServiceMock).acceptOrRejectOtherDocuments(1L, true);
    }

    @Test
    public void isOtherDocumentsSubmitAllowed() throws Exception {

        UserResource userResource = newUserResource()
                .withId(1L)
                .withUID("123abc")
                .build();
        MockHttpServletRequestBuilder mainRequest = get("/project/{projectId}/partner/documents/ready", 123L)
                .header("IFS_AUTH_TOKEN", "123abc");

        when(projectServiceMock.isOtherDocumentsSubmitAllowed(123L, 1L)).thenReturn(serviceSuccess(true));
        when(userAuthenticationService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(userResource);

        mockMvc.perform(mainRequest)
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andReturn();
    }

  /*  @Test
    public void getGrantOfferLetterWorkflowState() throws Exception {

        Long projectId = 123L;

        when(projectGrantOfferServiceMock.getGrantOfferLetterWorkflowState(projectId)).thenReturn(serviceSuccess(GOLState.APPROVED));

        mockMvc.perform(get("/project/{projectId}/grant-offer-letter/state", 123L))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(GOLState.APPROVED)))
                .andReturn();

        verify(projectGrantOfferServiceMock).getGrantOfferLetterWorkflowState(projectId);
    }*/

    @Test
    public void tetsGetProjectManager() throws Exception {
        Long project1Id = 1L;

        ProjectUserResource projectManager = newProjectUserResource().withId(project1Id).build();

        when(projectServiceMock.getProjectManager(project1Id)).thenReturn(serviceSuccess(projectManager));

        mockMvc.perform(get("/project/{id}/project-manager", project1Id))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectManager)));
    }

    @Test
    public void tetsGetProjectManagerNotFound() throws Exception {
        Long project1Id = -1L;

        when(projectServiceMock.getProjectManager(project1Id)).thenReturn(serviceFailure(GENERAL_NOT_FOUND));

        mockMvc.perform(get("/project/{id}/project-manager", project1Id))
                .andExpect(status().isNotFound());
    }
}
