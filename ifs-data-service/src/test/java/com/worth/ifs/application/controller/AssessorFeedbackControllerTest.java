package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.transactional.FileHeaderAttributes;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.Supplier;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.InputStreamTestUtil.assertInputStreamContents;
import static com.worth.ifs.JsonTestUtil.toJson;
import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessorFeedbackControllerTest extends BaseControllerMockMVCTest<AssessorFeedbackController> {

    @Test
    public void testCreateAssessorFeedback() throws Exception {

        // having to "fake" the request body as JSON because Spring Restdocs does not support other content types other
        // than JSON and XML
        String dummyContent = "{\"description\":\"The request body is the binary content of the file being uploaded - it is NOT JSON as seen here!\"}";

        FileEntryResource createdResource = newFileEntryResource().with(id(1111L)).build();
        ServiceResult<FileEntryResource> successResponse = serviceSuccess(createdResource);

        FileHeaderAttributes fileAttributesAfterValidation = new FileHeaderAttributes(MediaType.valueOf("application/pdf"), 1000L, "original.pdf");
        when(fileValidatorMock.validateFileHeaders("application/pdf", "1000", "original.pdf")).thenReturn(serviceSuccess(fileAttributesAfterValidation));
        when(assessorFeedbackServiceMock.createAssessorFeedbackFileEntry(eq(123L), createFileEntryResourceExpectations(fileAttributesAfterValidation), createInputStreamExpectations(dummyContent))).thenReturn(successResponse);

        MvcResult response = mockMvc.
                perform(
                        post("/assessorfeedback/assessorFeedbackDocument").
                                param("applicationId", "123").
                                param("filename", "original.pdf").
                                header("Content-Type", "application/pdf").
                                header("Content-Length", "1000").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(dummyContent)
                ).
                andExpect(status().isCreated()).
                andDo(documentCreateAssessorFeedbackDocument()).
                andReturn();

        String content = response.getResponse().getContentAsString();
        FileEntryResource expectedCreatedResponse = new ObjectMapper().readValue(content, FileEntryResource.class);
        assertEquals(createdResource, expectedCreatedResponse);

        verify(fileValidatorMock).validateFileHeaders("application/pdf", "1000", "original.pdf");
        verify(assessorFeedbackServiceMock).createAssessorFeedbackFileEntry(eq(123L), createFileEntryResourceExpectations(fileAttributesAfterValidation), createInputStreamExpectations(dummyContent));
    }

    @Test
    public void testUpdateAssessorFeedback() throws Exception {

        // having to "fake" the request body as JSON because Spring Restdocs does not support other content types other
        // than JSON and XML
        String dummyContent = "{\"description\":\"The request body is the binary content of the file being uploaded - it is NOT JSON as seen here!\"}";

        FileHeaderAttributes fileAttributesAfterValidation = new FileHeaderAttributes(MediaType.valueOf("application/pdf"), 1000L, "updated.pdf");
        when(fileValidatorMock.validateFileHeaders("application/pdf", "1000", "updated.pdf")).thenReturn(serviceSuccess(fileAttributesAfterValidation));

        FileEntryResource updatedResource = newFileEntryResource().with(id(1111L)).build();
        ServiceResult<FileEntryResource> successResponse = serviceSuccess(updatedResource);

        when(assessorFeedbackServiceMock.updateAssessorFeedbackFileEntry(eq(123L),
                createFileEntryResourceExpectations(fileAttributesAfterValidation),
                createInputStreamExpectations(dummyContent))).thenReturn(successResponse);

        mockMvc.perform(
                        put("/assessorfeedback/assessorFeedbackDocument").
                                param("applicationId", "123").
                                param("filename", "updated.pdf").
                                header("Content-Type", "application/pdf").
                                header("Content-Length", "1000").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(dummyContent)
                ).
                andExpect(status().isOk()).
                andExpect(content().string("")).
                andDo(documentUpdateAssessorFeedbackDocument());

        verify(fileValidatorMock).validateFileHeaders("application/pdf", "1000", "updated.pdf");
        verify(assessorFeedbackServiceMock).updateAssessorFeedbackFileEntry(eq(123L), createFileEntryResourceExpectations(fileAttributesAfterValidation), createInputStreamExpectations(dummyContent));
    }

    @Test
    public void testDeleteAssessorFeedback() throws Exception {

        when(assessorFeedbackServiceMock.deleteAssessorFeedbackFileEntry(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(
                delete("/assessorfeedback/assessorFeedbackDocument").
                        param("applicationId", "123").
                        header("IFS_AUTH_TOKEN", "123abc")
        ).
                andExpect(status().isNoContent()).
                andExpect(content().string("")).
                andDo(documentDeleteAssessorFeedbackDocument());

        verify(assessorFeedbackServiceMock).deleteAssessorFeedbackFileEntry(123L);
    }

    @Test
    public void testGetAssessorFeedbackFileContents() throws Exception {

        FileEntryResource returnedFileEntry = newFileEntryResource().build();

        Supplier<InputStream> inputStreamSupplier = () -> new ByteArrayInputStream("The returned binary file data".getBytes());

        when(assessorFeedbackServiceMock.getAssessorFeedbackFileEntry(123L)).thenReturn(serviceSuccess(Pair.of(returnedFileEntry, inputStreamSupplier)));

        MvcResult response = mockMvc.
                perform(
                        MockMvcRequestBuilders.get("/assessorfeedback/assessorFeedbackDocument").
                                param("applicationId", "123").
                                header("IFS_AUTH_TOKEN", "123abc")
                ).
                andExpect(status().isOk()).
                andDo(documentGetAssessorFeedbackDocumentationContents()).
                andReturn();

        assertEquals("The returned binary file data", response.getResponse().getContentAsString());
    }

    @Test
    public void testGetAssessorFeedbackFileEntry() throws Exception {

        FileEntryResource returnedFileEntry = newFileEntryResource().
                withName("lookedup.pdf").
                withMediaType("application/pdf").
                withFilesizeBytes(1000).build();

        Supplier<InputStream> inputStreamSupplier = () -> new ByteArrayInputStream("The returned binary file data".getBytes());

        when(assessorFeedbackServiceMock.getAssessorFeedbackFileEntry(123L)).thenReturn(serviceSuccess(Pair.of(returnedFileEntry, inputStreamSupplier)));

        mockMvc.
                perform(
                        MockMvcRequestBuilders.get("/assessorfeedback/assessorFeedbackDocument/fileentry").
                                param("applicationId", "123").
                                header("IFS_AUTH_TOKEN", "123abc")
                ).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(returnedFileEntry))).
                andDo(documentGetAssessorFeedbackDocumentationFileEntry());

        verify(assessorFeedbackServiceMock).getAssessorFeedbackFileEntry(123L);

    }

    private RestDocumentationResultHandler documentGetAssessorFeedbackDocumentationFileEntry() {

        return document("assessor-feedback/assessorFeedbackDocument_getFileContents",
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

    private Supplier<InputStream> createInputStreamExpectations(String dummyContent) {
        return createLambdaMatcher(is -> assertInputStreamContents(is.get(), dummyContent));
    }

    private FileEntryResource createFileEntryResourceExpectations(FileHeaderAttributes expectedAttributes) {
        return eq(expectedAttributes.toFileEntryResource());
    }

    @Override
    protected AssessorFeedbackController supplyControllerUnderTest() {
        return new AssessorFeedbackController();
    }
}
