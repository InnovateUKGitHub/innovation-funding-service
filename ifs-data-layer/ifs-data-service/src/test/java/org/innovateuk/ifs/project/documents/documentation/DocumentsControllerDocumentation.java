package org.innovateuk.ifs.project.documents.documentation;

import org.innovateuk.ifs.BaseFileControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.innovateuk.ifs.project.documents.controller.DocumentsController;
import org.innovateuk.ifs.project.documents.transactional.DocumentsService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DocumentsControllerDocumentation extends BaseFileControllerMockMVCTest<DocumentsController> {

    private static final long projectId = 123L;
    private static final long documentConfigId = 456L;
    private static final long maxFilesize = 1234L;
    private static final List<String> mediaTypes = singletonList("application/pdf");

    @Mock
    private DocumentsService documentsServiceMock;

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
                              new Object[] {}, emptyMap(), documentsServiceMock, serviceCallToUpload)
                .andDo(documentFileGetContentsMethod("project/{method-name}"));
    }

    @Test
    public void getFileEntryDetails() throws Exception {

        Function<DocumentsService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getFileEntryDetails(projectId, documentConfigId);

        assertGetFileDetails("/project/" + projectId + "/document/config/" + documentConfigId + "/file-entry-details", new Object[] {}, emptyMap(),
                             documentsServiceMock, serviceCallToUpload)
                .andDo(documentFileGetDetailsMethod("project/{method-name}"));
    }

    @Test
    public void deleteDocument() throws Exception {

        Function<DocumentsService, ServiceResult<Void>> serviceCallToDelete =
                service -> service.deleteDocument(projectId, documentConfigId);

        assertDeleteFile("/project/" + projectId + "/document/config/" + documentConfigId + "/delete", new Object[] {},
                         emptyMap(), documentsServiceMock, serviceCallToDelete)
                .andDo(documentFileDeleteMethod("project/{method-name}"));
    }

    @Test
    public void submitDocument() throws Exception {
        when(documentsServiceMock.submitDocument(projectId, documentConfigId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/document/config/{documentConfigId}/submit", projectId, documentConfigId)
                                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                                pathParameters(
                                        parameterWithName("projectId").description("Id of the project the documents are associated with"),
                                        parameterWithName("documentConfigId").description("Id of the competition document being submitted")
                                )
                ));
    }

    @Test
    public void documentDecision() throws Exception {
        ProjectDocumentDecision decision = new ProjectDocumentDecision(true, null);

        when(documentsServiceMock.documentDecision(projectId, documentConfigId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/document/config/{documentConfigId}/decision", projectId, documentConfigId)
                                .contentType(APPLICATION_JSON)
                                .content(toJson(decision)))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                                pathParameters(
                                        parameterWithName("projectId").description("Id of the project the documents are associated with"),
                                        parameterWithName("documentConfigId").description("Id of the competition document for this decision")
                                )
                ));
    }
}