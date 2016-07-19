package com.worth.ifs.application.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.transactional.AssessorFeedbackService;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.file.transactional.FileHeaderAttributes;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.worth.ifs.JsonTestUtil.toJson;
import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.asListOfPairs;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Collections.emptyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessorFeedbackControllerTest extends BaseControllerMockMVCTest<AssessorFeedbackController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setUpDocumentation() throws Exception {
        this.document = document("assessorfeedback/assessorFeedbackDocument_{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void testCreateAssessorFeedback() throws Exception {

        BiFunction<AssessorFeedbackService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCall =
                (service, fileToUpload) -> service.createAssessorFeedbackFileEntry(eq(123L), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/assessorfeedback/assessorFeedbackDocument", new Object[] {},
                asMap("applicationId", "123"),
                assessorFeedbackServiceMock,
                serviceCall).andDo(documentFileUploadMethod(document, asListOfPairs("applicationId", "123"), emptyList()));
    }

    @Test
    public void testUpdateAssessorFeedback() throws Exception {

        BiFunction<AssessorFeedbackService, FileEntryResource, ServiceResult<Void>> updateCall =
                (service, fileToUpdate) -> assessorFeedbackServiceMock.updateAssessorFeedbackFileEntry(eq(123L), eq(fileToUpdate), fileUploadInputStreamExpectations());

        assertFileUpdateProcess("/assessorfeedback/assessorFeedbackDocument", new Object[] {},
                asMap("applicationId", "123"),
                assessorFeedbackServiceMock, updateCall).
                andDo(documentFileUpdateMethod(document, asListOfPairs("applicationId", "123"), emptyList()));
    }

    @Test
    public void testDeleteAssessorFeedback() throws Exception {

        Function<AssessorFeedbackService, ServiceResult<Void>> deleteCall =
                service -> assessorFeedbackServiceMock.deleteAssessorFeedbackFileEntry(123L);

        assertDeleteFileDetails("/assessorfeedback/assessorFeedbackDocument", new Object[] {},
                asMap("applicationId", "123"),
                assessorFeedbackServiceMock, deleteCall).
                andDo(documentDeleteAssessorFeedbackDocument());

        verify(assessorFeedbackServiceMock).deleteAssessorFeedbackFileEntry(123L);
    }

    @Test
    public void testGetAssessorFeedbackFileContents() throws Exception {

        BiFunction<AssessorFeedbackService, FileEntryResource, ServiceResult<FileAndContents>> getFileAction =
                (service, fileToGet) -> service.getAssessorFeedbackFileEntryContents(123L);

        assertGetFileContents("/assessorfeedback/assessorFeedbackDocument", new Object[] {},
                asMap("applicationId", "123"),
                assessorFeedbackServiceMock, getFileAction).
                andDo(documentGetAssessorFeedbackDocumentationContents());
    }

    @Test
    public void testGetAssessorFeedbackFileEntry() throws Exception {

        BiFunction<AssessorFeedbackService, FileEntryResource, ServiceResult<FileEntryResource>> getFileAction =
                (service, fileToGet) -> service.getAssessorFeedbackFileEntryDetails(123L);

        assertGetFileDetails("/assessorfeedback/assessorFeedbackDocument/fileentry", new Object[] {},
                asMap("applicationId", "123"),
                assessorFeedbackServiceMock, getFileAction).
                andDo(documentGetAssessorFeedbackDocumentationContents());
    }
    
    @Test
    public void testAssessorFeedbackUploaded() throws Exception {

        when(assessorFeedbackServiceMock.assessorFeedbackUploaded(123L)).thenReturn(serviceSuccess(true));

        mockMvc.
                perform(
                        MockMvcRequestBuilders.get("/assessorfeedback/assessorFeedbackUploaded").
                                param("competitionId", "123").
                                header("IFS_AUTH_TOKEN", "123abc")
                ).
                andExpect(status().isOk()).
                andExpect(content().string("true")).
                andDo(documentAssessorFeedbackUploaded());

        verify(assessorFeedbackServiceMock).assessorFeedbackUploaded(123L);
    }
    
    @Test
    public void testSubmitAssessorFeedback() throws Exception {

        when(assessorFeedbackServiceMock.submitAssessorFeedback(123L)).thenReturn(serviceSuccess());
        when(assessorFeedbackServiceMock.notifyLeadApplicantsOfAssessorFeedback(123L)).thenReturn(serviceSuccess());

        mockMvc.
                perform(
                        MockMvcRequestBuilders.post("/assessorfeedback/submitAssessorFeedback/123").
                                header("IFS_AUTH_TOKEN", "123abc")
                ).
                andExpect(status().isOk()).
                andExpect(content().string("")).
                andDo(documentSubmitAssessorFeedback());

        verify(assessorFeedbackServiceMock).submitAssessorFeedback(123L);
        verify(assessorFeedbackServiceMock).notifyLeadApplicantsOfAssessorFeedback(123L);
    }

    @Test
    public void testSubmitAssessorFeedbackButSubmissionFailsSoNoEmailsSent() throws Exception {

        when(assessorFeedbackServiceMock.submitAssessorFeedback(123L)).thenReturn(serviceFailure(internalServerErrorError("Urgh!")));

        mockMvc.
                perform(
                        MockMvcRequestBuilders.post("/assessorfeedback/submitAssessorFeedback/123").
                                header("IFS_AUTH_TOKEN", "123abc")
                ).
                andExpect(status().isInternalServerError()).
                andExpect(content().json(toJson(new RestErrorResponse(internalServerErrorError("Urgh!")))));

        verify(assessorFeedbackServiceMock).submitAssessorFeedback(123L);
        verify(assessorFeedbackServiceMock, never()).notifyLeadApplicantsOfAssessorFeedback(123L);
    }

    private RestDocumentationResultHandler documentGetAssessorFeedbackDocumentationFileEntry() {

        return document("assessor-feedback/assessorFeedbackDocument_getFileEntry",
                requestParameters(
                        parameterWithName("applicationId").description("Id of the Application that the FormInputResponse is related to")
                ),
                requestHeaders(
                        headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                ),
                responseFields(
                        fieldWithPath("id").description("Id of the FileEntry that was looked up"),
                        fieldWithPath("name").description("Name of the FileEntry that was looked up"),
                        fieldWithPath("mediaType").description("Media type of the FileEntry that was looked up"),
                        fieldWithPath("filesizeBytes").description("File size in bytes of the FileEntry that was looked up")
                ));
    }

    private RestDocumentationResultHandler documentGetAssessorFeedbackDocumentationContents() {

        return document("assessor-feedback/assessorFeedbackDocument_getFileContents",
                requestParameters(
                        parameterWithName("applicationId").description("Id of the Application that the FormInputResponse is related to")
                ),
                requestHeaders(
                        headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                ));
    }

    private RestDocumentationResultHandler documentCreateAssessorFeedbackDocument() {

        return document("assessor-feedback/assessorFeedbackDocument_create",
                requestParameters(
                        parameterWithName("applicationId").description("Id of the Application that the Assessor Feedback document is being applied to"),
                        parameterWithName("filename").description("The filename of the file being uploaded")
                ),
                requestHeaders(
                        headerWithName("Content-Type").description("The Content Type of the file being uploaded e.g. application/pdf"),
                        headerWithName("Content-Length").description("The Content Length of the binary file data being uploaded in bytes"),
                        headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                ),
                requestFields(fieldWithPath("description").description("The body of the request should be the binary data of the file being uploaded (and NOT JSON as shown in example)")),
                responseFields(
                        fieldWithPath("id").description("Id of the FileEntry that was created"),
                        fieldWithPath("name").description("Name of the FileEntry that was created"),
                        fieldWithPath("mediaType").description("Media type of the FileEntry that was created"),
                        fieldWithPath("filesizeBytes").description("File size in bytes of the FileEntry that was created")
                ));
    }

    private RestDocumentationResultHandler documentUpdateAssessorFeedbackDocument() {

        return document("assessor-feedback/assessorFeedbackDocument_update",
                requestParameters(
                        parameterWithName("applicationId").description("Id of the Application that the Assessor Feedback document is being applied to"),
                        parameterWithName("filename").description("The filename of the file being uploaded")
                ),
                requestHeaders(
                        headerWithName("Content-Type").description("The Content Type of the file being uploaded e.g. application/pdf"),
                        headerWithName("Content-Length").description("The Content Length of the binary file data being uploaded in bytes"),
                        headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                ),
                requestFields(fieldWithPath("description").description("The body of the request should be the binary data of the file being uploaded (and NOT JSON as shown in example)")));
    }

    private RestDocumentationResultHandler documentDeleteAssessorFeedbackDocument() {

        return document("assessor-feedback/assessorFeedbackDocument_delete",
                requestParameters(
                        parameterWithName("applicationId").description("Id of the Application that the Assessor Feedback document is being deleted from")
                ),
                requestHeaders(
                        headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                ));
    }
    
    private RestDocumentationResultHandler documentAssessorFeedbackUploaded() {
    	return document("assessor-feedback/assessorFeedbackUploaded",
    			requestParameters(
                        parameterWithName("competitionId").description("Id of the competition that we are checking if feedback is uploaded for all submitted applications")
                ),
                requestHeaders(
                        headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                ));
    }
    
    private RestDocumentationResultHandler documentSubmitAssessorFeedback() {
    	return document("assessor-feedback/submitAssessorFeedback",
                requestHeaders(
                        headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                ));
    }

    private FileEntryResource createFileEntryResourceExpectations(FileHeaderAttributes expectedAttributes) {
        return eq(expectedAttributes.toFileEntryResource());
    }

    @Override
    protected AssessorFeedbackController supplyControllerUnderTest() {
        return new AssessorFeedbackController();
    }
}
