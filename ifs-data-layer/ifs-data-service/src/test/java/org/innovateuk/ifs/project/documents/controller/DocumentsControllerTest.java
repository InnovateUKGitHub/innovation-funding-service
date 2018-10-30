package org.innovateuk.ifs.project.documents.controller;

import org.innovateuk.ifs.BaseFileControllerMockMVCTest;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.innovateuk.ifs.project.documents.transactional.DocumentsService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DocumentsControllerTest extends BaseFileControllerMockMVCTest<DocumentsController> {

    private static final long projectId = 123L;
    private static final long documentConfigId = 456L;
    private static final long maxFilesize = 1234L;
    private static final List<String> mediaTypes = singletonList("application/pdf");

    @Mock
    private DocumentsService documentsServiceMock;

    @Mock
    private UserAuthenticationService userAuthenticationServiceMock;

    @Mock(name = "fileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidatorMock;

    @Override
    protected DocumentsController supplyControllerUnderTest() {
        DocumentsController controller = new DocumentsController();
        ReflectionTestUtils.setField(controller, "maxFileSizeBytesForProjectSetupDocuments", maxFilesize);
        return controller;
    }

    @Test
    public void uploadDocument() throws Exception {

        when(documentsServiceMock.getValidMediaTypesForDocument(documentConfigId)).thenReturn(serviceSuccess(mediaTypes));

        BiFunction<DocumentsService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createDocumentFileEntry(eq(projectId), eq(documentConfigId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/document/config/" + documentConfigId + "/upload",
                fileValidatorMock, mediaTypes, documentsServiceMock, serviceCallToUpload);
    }

    @Test
    public void getFileContents() throws Exception {

        Function<DocumentsService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getFileContents(projectId, documentConfigId);

        assertGetFileContents("/project/" + projectId + "/document/config/" + documentConfigId + "/file-contents",
                new Object[] {}, emptyMap(), documentsServiceMock, serviceCallToUpload);
    }

    @Test
    public void getFileEntryDetails() throws Exception {

        Function<DocumentsService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getFileEntryDetails(projectId, documentConfigId);

        assertGetFileDetails("/project/" + projectId + "/document/config/" + documentConfigId + "/file-entry-details", new Object[] {}, emptyMap(),
                documentsServiceMock, serviceCallToUpload);
    }

    @Test
    public void deleteDocument() throws Exception {

        Function<DocumentsService, ServiceResult<Void>> serviceCallToDelete =
                service -> service.deleteDocument(projectId, documentConfigId);

        assertDeleteFile("/project/" + projectId + "/document/config/" + documentConfigId + "/delete", new Object[] {},
                emptyMap(), documentsServiceMock, serviceCallToDelete);
    }

    @Test
    public void submitDocument() throws Exception {
        when(documentsServiceMock.submitDocument(projectId, documentConfigId)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/project/" + projectId + "/document/config/" + documentConfigId + "/submit")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(documentsServiceMock).submitDocument(projectId, documentConfigId);
    }

    @Test
    public void documentDecision() throws Exception {
        ProjectDocumentDecision decision = new ProjectDocumentDecision(true, null);

        when(documentsServiceMock.documentDecision(projectId, documentConfigId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/project/" + projectId + "/document/config/" + documentConfigId + "/decision")
                .contentType(APPLICATION_JSON)
                .content(toJson(decision)))
                .andExpect(status().isOk());

        verify(documentsServiceMock).documentDecision(projectId, documentConfigId, decision);
    }
}

