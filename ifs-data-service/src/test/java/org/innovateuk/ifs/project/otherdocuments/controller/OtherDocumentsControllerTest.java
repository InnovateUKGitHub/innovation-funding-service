package org.innovateuk.ifs.project.otherdocuments.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.project.otherdocuments.transactional.OtherDocumentsService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OtherDocumentsControllerTest extends BaseControllerMockMVCTest<OtherDocumentsController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setUpDocumentation() throws Exception {
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Override
    protected OtherDocumentsController supplyControllerUnderTest() {
        return new OtherDocumentsController();
    }

    @Test
    public void addCollaborationAgreement() throws Exception {

        Long projectId = 123L;

        BiFunction<OtherDocumentsService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createCollaborationAgreementFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/collaboration-agreement", otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod(document));
    }

    @Test
    public void updateCollaborationAgreement() throws Exception {

        Long projectId = 123L;

        BiFunction<OtherDocumentsService, FileEntryResource, ServiceResult<Void>> serviceCallToUpload =
                (service, fileToUpload) -> service.updateCollaborationAgreementFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUpdateProcess("/project/" + projectId + "/collaboration-agreement", otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileUpdateMethod(document));
    }

    @Test
    public void getCollaborationAgreementFileDetails() throws Exception {

        Long projectId = 123L;

        Function<OtherDocumentsService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getCollaborationAgreementFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/collaboration-agreement/details", new Object[] {projectId}, emptyMap(),
                otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod(document));
    }

    @Test
    public void getCollaborationAgreementFileContent() throws Exception {

        Long projectId = 123L;

        Function<OtherDocumentsService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getCollaborationAgreementFileContents(projectId);

        assertGetFileContents("/project/{projectId}/collaboration-agreement", new Object[] {projectId},
                emptyMap(), otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod(document));
    }

    @Test
    public void deleteCollaborationAgreement() throws Exception {

        Long projectId = 123L;

        Function<OtherDocumentsService, ServiceResult<Void>> serviceCallToDelete =
                service -> service.deleteCollaborationAgreementFile(projectId);

        assertDeleteFile("/project/{projectId}/collaboration-agreement", new Object[] {projectId},
                emptyMap(), otherDocumentsServiceMock, serviceCallToDelete).
                andDo(documentFileDeleteMethod(document));
    }

    @Test
    public void addExploitationPlan() throws Exception {

        Long projectId = 123L;

        BiFunction<OtherDocumentsService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createExploitationPlanFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/exploitation-plan", otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod(document));
    }

    @Test
    public void updateExploitationPlan() throws Exception {

        Long projectId = 123L;

        BiFunction<OtherDocumentsService, FileEntryResource, ServiceResult<Void>> serviceCallToUpload =
                (service, fileToUpload) -> service.updateExploitationPlanFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUpdateProcess("/project/" + projectId + "/exploitation-plan", otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileUpdateMethod(document));
    }

    @Test
    public void getExploitationPlanFileDetails() throws Exception {

        Long projectId = 123L;

        Function<OtherDocumentsService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getExploitationPlanFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/exploitation-plan/details", new Object[] {projectId}, emptyMap(),
                otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod(document));
    }

    @Test
    public void getExploitationPlanFileContent() throws Exception {

        Long projectId = 123L;

        Function<OtherDocumentsService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getExploitationPlanFileContents(projectId);

        assertGetFileContents("/project/{projectId}/exploitation-plan", new Object[] {projectId},
                emptyMap(), otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod(document));
    }

    @Test
    public void deleteExploitationPlan() throws Exception {

        Long projectId = 123L;

        Function<OtherDocumentsService, ServiceResult<Void>> serviceCallToDelete =
                service -> service.deleteExploitationPlanFile(projectId);

        assertDeleteFile("/project/{projectId}/exploitation-plan", new Object[] {projectId},
                emptyMap(), otherDocumentsServiceMock, serviceCallToDelete).
                andDo(documentFileDeleteMethod(document));
    }

    @Test
    public void acceptOrRejectOtherDocuments() throws Exception {
        when(otherDocumentsServiceMock.acceptOrRejectOtherDocuments(1L, true)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/1/partner/documents/approved/{approved}", true).
                contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(otherDocumentsServiceMock).acceptOrRejectOtherDocuments(1L, true);
    }

    @Test
    public void isOtherDocumentsSubmitAllowed() throws Exception {

        UserResource userResource = newUserResource()
                .withId(1L)
                .withUID("123abc")
                .build();
        MockHttpServletRequestBuilder mainRequest = get("/project/{projectId}/partner/documents/ready", 123L)
                .header("IFS_AUTH_TOKEN", "123abc");

        when(otherDocumentsServiceMock.isOtherDocumentsSubmitAllowed(123L, 1L)).thenReturn(serviceSuccess(true));
        when(userAuthenticationService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(userResource);

        mockMvc.perform(mainRequest)
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andReturn();
    }
}
