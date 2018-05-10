package org.innovateuk.ifs.project.otherdocuments.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.project.otherdocuments.transactional.OtherDocumentsService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OtherDocumentsControllerTest extends BaseControllerMockMVCTest<OtherDocumentsController> {

    private static final long projectId = 123L;
    private static final long maxFilesize = 1234L;
    private static final List<String> mediaTypes = singletonList("application/pdf");

    @Mock(name = "fileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidatorMock;

    @Override
    protected OtherDocumentsController supplyControllerUnderTest() {
        OtherDocumentsController controller = new OtherDocumentsController();
        ReflectionTestUtils.setField(controller, "maxFilesizeBytesForProjectSetupOtherDocuments", maxFilesize);
        ReflectionTestUtils.setField(controller, "validMediaTypesForProjectSetupOtherDocuments", mediaTypes);
        return controller;
    }

    @Test
    public void addCollaborationAgreement() throws Exception {

        BiFunction<OtherDocumentsService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createCollaborationAgreementFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/collaboration-agreement", fileValidatorMock, mediaTypes, otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod("project/{method-name}"));
    }

    @Test
    public void updateCollaborationAgreement() throws Exception {

        BiFunction<OtherDocumentsService, FileEntryResource, ServiceResult<Void>> serviceCallToUpload =
                (service, fileToUpload) -> service.updateCollaborationAgreementFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUpdateProcess("/project/" + projectId + "/collaboration-agreement", fileValidatorMock, mediaTypes, otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileUpdateMethod("project/{method-name}"));
    }

    @Test
    public void getCollaborationAgreementFileDetails() throws Exception {

        Function<OtherDocumentsService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getCollaborationAgreementFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/collaboration-agreement/details", new Object[] {projectId}, emptyMap(),
                otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod("project/{method-name}"));
    }

    @Test
    public void getCollaborationAgreementFileContent() throws Exception {

        Function<OtherDocumentsService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getCollaborationAgreementFileContents(projectId);

        assertGetFileContents("/project/{projectId}/collaboration-agreement", new Object[] {projectId},
                emptyMap(), otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod("project/{method-name}"));
    }

    @Test
    public void deleteCollaborationAgreement() throws Exception {

        Function<OtherDocumentsService, ServiceResult<Void>> serviceCallToDelete =
                service -> service.deleteCollaborationAgreementFile(projectId);

        assertDeleteFile("/project/{projectId}/collaboration-agreement", new Object[] {projectId},
                emptyMap(), otherDocumentsServiceMock, serviceCallToDelete).
                andDo(documentFileDeleteMethod("project/{method-name}"));
    }

    @Test
    public void addExploitationPlan() throws Exception {

        BiFunction<OtherDocumentsService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createExploitationPlanFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/exploitation-plan", fileValidatorMock, mediaTypes, otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod("project/{method-name}"));
    }

    @Test
    public void updateExploitationPlan() throws Exception {

        BiFunction<OtherDocumentsService, FileEntryResource, ServiceResult<Void>> serviceCallToUpload =
                (service, fileToUpload) -> service.updateExploitationPlanFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUpdateProcess("/project/" + projectId + "/exploitation-plan", fileValidatorMock, mediaTypes, otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileUpdateMethod("project/{method-name}"));
    }

    @Test
    public void getExploitationPlanFileDetails() throws Exception {

        Function<OtherDocumentsService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getExploitationPlanFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/exploitation-plan/details", new Object[] {projectId}, emptyMap(),
                otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod("project/{method-name}"));
    }

    @Test
    public void getExploitationPlanFileContent() throws Exception {

        Function<OtherDocumentsService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getExploitationPlanFileContents(projectId);

        assertGetFileContents("/project/{projectId}/exploitation-plan", new Object[] {projectId},
                emptyMap(), otherDocumentsServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod("project/{method-name}"));
    }

    @Test
    public void deleteExploitationPlan() throws Exception {

        Function<OtherDocumentsService, ServiceResult<Void>> serviceCallToDelete =
                service -> service.deleteExploitationPlanFile(projectId);

        assertDeleteFile("/project/{projectId}/exploitation-plan", new Object[] {projectId},
                emptyMap(), otherDocumentsServiceMock, serviceCallToDelete).
                andDo(documentFileDeleteMethod("project/{method-name}"));
    }

    @Test
    public void acceptOrRejectOtherDocuments() throws Exception {
        when(otherDocumentsServiceMock.acceptOrRejectOtherDocuments(123L, true)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/project/{projectId}/partner/documents/approved/{approved}", 123L, true).
                contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
        .andDo(document("project/{method-name}",
                pathParameters(
                    parameterWithName("projectId").description("Id of the project for which the Other Documents are being approved or rejected"),
                    parameterWithName("approved").description("Whether the Other Documents are being approved or rejected")
                )
        ));

        verify(otherDocumentsServiceMock).acceptOrRejectOtherDocuments(123L, true);
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
                .andDo(document("project/{method-name}"))
                .andReturn();
    }
}
