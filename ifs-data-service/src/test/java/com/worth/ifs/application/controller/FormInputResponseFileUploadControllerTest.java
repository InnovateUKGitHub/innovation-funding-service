package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.application.transactional.FormInputResponseFileAndContents;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.transactional.FileHeaderAttributes;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static com.worth.ifs.InputStreamTestUtil.assertInputStreamContents;
import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.LambdaMatcher.lambdaMatches;
import static com.worth.ifs.commons.error.CommonErrors.*;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the FormInputResponseFileUploadController, for uploading and downloading files related to FormInputResponses
 */
public class FormInputResponseFileUploadControllerTest extends BaseControllerMockMVCTest<FormInputResponseFileUploadController> {

    private static final Error generalError = new Error("No files today!", INTERNAL_SERVER_ERROR);

    @Override
    protected FormInputResponseFileUploadController supplyControllerUnderTest() {
        return new FormInputResponseFileUploadController();
    }

    @Test
    public void testCreateFile() throws Exception {

        // having to "fake" the request body as JSON because Spring Restdocs does not support other content types other
        // than JSON and XML
        String dummyContent = "{\"description\":\"The request body is the binary content of the file being uploaded - it is NOT JSON as seen here!\"}";

        FormInputResponseFileEntryResource createdResource = new FormInputResponseFileEntryResource(newFileEntryResource().with(id(1111L)).build(), 123L, 456L, 789L);
        ServiceResult<FormInputResponseFileEntryResource> successResponse = serviceSuccess(createdResource);

        FileHeaderAttributes fileAttributesAfterValidation = new FileHeaderAttributes(MediaType.valueOf("application/pdf"), 1000L, "original.pdf");
        when(fileValidatorMock.validateFileHeaders("application/pdf", "1000", "original.pdf")).thenReturn(serviceSuccess(fileAttributesAfterValidation));
        when(applicationServiceMock.createFormInputResponseFileUpload(createFileEntryResourceExpectations("original.pdf"), createInputStreamExpectations(dummyContent))).thenReturn(successResponse);

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Type", "application/pdf").
                                header("Content-Length", "1000").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(dummyContent)
                        ).
                andExpect(status().isCreated()).
                andDo(document("forminputresponsefileupload/file_fileUpload",
                        requestParameters(
                                parameterWithName("formInputId").description("Id of the FormInput that the user is responding to"),
                                parameterWithName("applicationId").description("Id of the Application that the FormInputResponse is related to"),
                                parameterWithName("processRoleId").description("Id of the ProcessRole that is responding to the FormInput"),
                                parameterWithName("filename").description("The filename of the file being uploaded")
                        ),
                        requestHeaders(
                                headerWithName("Content-Type").description("The Content Type of the file being uploaded e.g. application/pdf"),
                                headerWithName("Content-Length").description("The Content Length of the binary file data being uploaded in bytes"),
                                headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                        ),
                        requestFields(fieldWithPath("description").description("The body of the request should be the binary data of the file being uploaded (and NOT JSON as shown in example)")),
                        responseFields(
                                fieldWithPath("fileEntryId").description("Id of the FileEntry that was created")
                        ))
                ).
                andReturn();

        String content = response.getResponse().getContentAsString();
        FormInputResponseFileEntryCreatedResponse createdResponse = new ObjectMapper().readValue(content, FormInputResponseFileEntryCreatedResponse.class);
        assertEquals(1111L, createdResponse.getFileEntryId());

        verify(fileValidatorMock).validateFileHeaders("application/pdf", "1000", "original.pdf");
        verify(applicationServiceMock).createFormInputResponseFileUpload(createFileEntryResourceExpectations("original.pdf"), createInputStreamExpectations(dummyContent));
    }

    private FormInputResponseFileEntryResource createFileEntryResourceExpectations(String expectedFilename) {
        return createLambdaMatcher(resource -> {
            assertEquals(123L, resource.getCompoundId().getFormInputId());
            assertEquals(456L, resource.getCompoundId().getApplicationId());
            assertEquals(789L, resource.getCompoundId().getProcessRoleId());

            assertNull(resource.getFileEntryResource().getId());
            assertEquals(1000, resource.getFileEntryResource().getFilesizeBytes());
            assertEquals("application/pdf", resource.getFileEntryResource().getMediaType());
            assertEquals(expectedFilename, resource.getFileEntryResource().getName());
        });
    }

    private Supplier<InputStream> createInputStreamExpectations(String dummyContent) {
        return createLambdaMatcher(is -> {
            assertInputStreamContents(is.get(), dummyContent);
        });
    }

    @Test
    public void testCreateFileButApplicationServiceCallFails() throws Exception {

        ServiceResult<FormInputResponseFileEntryResource> failureResponse = serviceFailure(generalError);

        when(fileValidatorMock.validateFileHeaders(isA(String.class), isA(String.class), isA(String.class))).thenReturn(serviceSuccess(new FileHeaderAttributes(MediaType.valueOf("application/pdf"), 1000, "original.pdf")));
        when(applicationServiceMock.createFormInputResponseFileUpload(isA(FormInputResponseFileEntryResource.class), isSupplierMatcher())).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Type", "application/pdf").
                                header("Content-Length", "1000").
                                content("My PDF content")).
                andExpect(status().isInternalServerError()).
                andDo(document("forminputresponsefileupload/file_fileUpload_internalServerError")).
                andReturn();

        String content = response.getResponse().getContentAsString();
        RestErrorResponse restErrorResponse = new ObjectMapper().readValue(content, RestErrorResponse.class);
        assertEqualsUpNoIncludingStatusCode(restErrorResponse, generalError);
    }

    @Test
    public void testCreateFileButFormInputNotFound() throws Exception {
        assertCreateFileButEntityNotFound(FormInput.class, "formInputNotFound");
    }


    @Test
    public void testCreateFileButApplicationNotFound() throws Exception {
        assertCreateFileButEntityNotFound(Application.class, "applicationNotFound");
    }


    @Test
    public void testCreateFileButProcessRoleNotFound() throws Exception {
        assertCreateFileButEntityNotFound(ProcessRole.class, "processRoleNotFound");
    }

    @Test
    public void testCreateFileButContentLengthHeaderTooLarge() throws Exception {

        when(fileValidatorMock.validateFileHeaders("application/pdf", "99999999", "original.pdf")).thenReturn(serviceFailure(payloadTooLargeError(5000)));

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Type", "application/pdf").
                                header("Content-Length", "99999999").
                                content("My PDF content")).
                andExpect(status().isPayloadTooLarge()).
                andDo(document("forminputresponsefileupload/file_fileUpload_payloadTooLarge")).
                andReturn();

        assertResponseErrorKeyEqual(PAYLOAD_TOO_LARGE.name(), payloadTooLargeError(5000), response);
    }

    @Test
    public void testCreateFileButContentLengthHeaderMissing() throws Exception {

        when(fileValidatorMock.validateFileHeaders("application/pdf", null, "original.pdf")).thenReturn(serviceFailure(lengthRequiredError(5000L)));

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Type", "application/pdf").
                                content("My PDF content")).
                andExpect(status().isLengthRequired()).
                andDo(document("forminputresponsefileupload/file_fileUpload_missingContentLength")).
                andReturn();

        assertResponseErrorKeyEqual(LENGTH_REQUIRED.name(), lengthRequiredError(5000), response);
    }

    @Test
    public void testCreateFileButContentTypeHeaderInvalid() throws Exception {

        when(fileValidatorMock.validateFileHeaders("text/plain", "1000", "original.pdf")).thenReturn(serviceFailure(unsupportedMediaTypeByNameError(asList("application/pdf", "application/json"))));

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Type", "text/plain").
                                header("Content-Length", "1000").
                                content("My PDF content")).
                andExpect(status().isUnsupportedMediaType()).
                andDo(document("forminputresponsefileupload/file_fileUpload_unsupportedContentType")).
                andReturn();

        assertResponseErrorKeyEqual(UNSUPPORTED_MEDIA_TYPE.name(), unsupportedMediaTypeByNameError(asList("application/pdf", "application/json")), response);
    }

    @Test
    public void testCreateFileButContentTypeHeaderMissing() throws Exception {

        when(fileValidatorMock.validateFileHeaders(null, "1000", "original.pdf")).thenReturn(serviceFailure(unsupportedMediaTypeByNameError(asList("application/pdf", "application/json"))));

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Length", "1000").
                                content("My PDF content")).
                andExpect(status().isUnsupportedMediaType()).
                andDo(document("forminputresponsefileupload/file_fileUpload_missingContentType")).
                andReturn();

        assertResponseErrorKeyEqual(UNSUPPORTED_MEDIA_TYPE.name(), unsupportedMediaTypeByNameError(asList("application/pdf", "application/json")), response);
    }

    @Test
    public void testCreateFileButContentLengthHeaderMisreported() throws Exception {
        assertCreateFileButErrorOccurs(new Error(FILES_INCORRECTLY_REPORTED_FILESIZE), "incorrectlyReportedContentLength", BAD_REQUEST);
    }

    @Test
    public void testCreateFileButContentTypeHeaderMisreported() throws Exception {
        assertCreateFileButErrorOccurs(new Error(FILES_INCORRECTLY_REPORTED_MEDIA_TYPE), "incorrectlyReportedContentType", UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    public void testCreateFileButDuplicateFileEncountered() throws Exception {
        assertCreateFileButErrorOccurs(new Error(FILES_DUPLICATE_FILE_CREATED), "duplicateFile", CONFLICT);
    }

    @Test
    public void testCreateFileButFormInputResponseAlreadyHasFileLinked() throws Exception {
        assertCreateFileButErrorOccurs(new Error(FILES_FILE_ALREADY_LINKED_TO_FORM_INPUT_RESPONSE), "fileAlreadyLinked", CONFLICT);
    }

    @Test
    public void testUpdateFile() throws Exception {

        // having to "fake" the request body as JSON because Spring Restdocs does not support other content types other
        // than JSON and XML
        String dummyContent = "{\"description\":\"The request body is the binary content of the file being uploaded - it is NOT JSON as seen here!\"}";

        ServiceResult<Void> successResponse = serviceSuccess();

        FileHeaderAttributes fileAttributesAfterValidation = new FileHeaderAttributes(MediaType.valueOf("application/pdf"), 1000L, "updated.pdf");
        when(fileValidatorMock.validateFileHeaders("application/pdf", "1000", "updated.pdf")).thenReturn(serviceSuccess(fileAttributesAfterValidation));
        when(applicationServiceMock.updateFormInputResponseFileUpload(createFileEntryResourceExpectations("updated.pdf"), createInputStreamExpectations(dummyContent))).thenReturn(successResponse);

        MvcResult response = mockMvc.
                perform(
                        put("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "updated.pdf").
                                header("Content-Type", "application/pdf").
                                header("Content-Length", "1000").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(dummyContent)
                ).
                andExpect(status().isOk()).
                andDo(document("forminputresponsefileupload/file_fileUpdate",
                        requestParameters(
                                parameterWithName("formInputId").description("Id of the FormInput that the user is responding to"),
                                parameterWithName("applicationId").description("Id of the Application that the FormInputResponse is related to"),
                                parameterWithName("processRoleId").description("Id of the ProcessRole that is responding to the FormInput"),
                                parameterWithName("filename").description("The filename of the file being uploaded")
                        ),
                        requestHeaders(
                                headerWithName("Content-Type").description("The Content Type of the file being uploaded e.g. application/pdf"),
                                headerWithName("Content-Length").description("The Content Length of the binary file data being uploaded in bytes"),
                                headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                        ),
                        requestFields(fieldWithPath("description").description("The body of the request should be the binary data of the file being uploaded (and NOT JSON as shown in example)"))
                )).
                andReturn();

        assertTrue(response.getResponse().getContentAsString().isEmpty());

        verify(fileValidatorMock).validateFileHeaders("application/pdf", "1000", "updated.pdf");
        verify(applicationServiceMock).updateFormInputResponseFileUpload(createFileEntryResourceExpectations("updated.pdf"), createInputStreamExpectations(dummyContent));
    }

    @Test
    public void testUpdateFileButApplicationServiceCallFails() throws Exception {

        ServiceResult<Void> failureResponse = serviceFailure(internalServerErrorError());

        FileHeaderAttributes fileAttributesAfterValidation = new FileHeaderAttributes(MediaType.valueOf("application/pdf"), 1000L, "original.pdf");
        when(fileValidatorMock.validateFileHeaders("application/pdf", "1000", "original.pdf")).thenReturn(serviceSuccess(fileAttributesAfterValidation));
        when(applicationServiceMock.updateFormInputResponseFileUpload(isA(FormInputResponseFileEntryResource.class), isSupplierMatcher())).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        put("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Type", "application/pdf").
                                header("Content-Length", "1000").
                                content("My PDF content")).
                andExpect(status().isInternalServerError()).
                andDo(document("forminputresponsefileupload/file_fileUpdate_internalServerError")).
                andReturn();

        assertResponseErrorKeyEqual(GENERAL_UNEXPECTED_ERROR.name(), internalServerErrorError(), response);
    }

    @Test
    public void testUpdateFileButFormInputNotFound() throws Exception {
        assertUpdateFileButEntityNotFound(FormInput.class, "formInputNotFound");
    }


    @Test
    public void testUpdateFileButApplicationNotFound() throws Exception {
        assertUpdateFileButEntityNotFound(Application.class, "applicationNotFound");
    }


    @Test
    public void testUpdateFileButProcessRoleNotFound() throws Exception {
        assertUpdateFileButEntityNotFound(ProcessRole.class, "processRoleNotFound");
    }

    @Test
    public void testUpdateFileButContentLengthHeaderTooLarge() throws Exception {

        when(fileValidatorMock.validateFileHeaders("application/pdf", "99999999", "original.pdf")).thenReturn(serviceFailure(payloadTooLargeError(5000)));

        MvcResult response = mockMvc.
                perform(
                        put("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Type", "application/pdf").
                                header("Content-Length", "99999999").
                                content("My PDF content")).
                andExpect(status().isPayloadTooLarge()).
                andDo(document("forminputresponsefileupload/file_fileUpdate_payloadTooLarge")).
                andReturn();

        assertResponseErrorKeyEqual(PAYLOAD_TOO_LARGE.name(), payloadTooLargeError(5000), response);
    }

    @Test
    public void testUpdateFileButContentLengthHeaderMissing() throws Exception {

        when(fileValidatorMock.validateFileHeaders("application/pdf", null, "original.pdf")).thenReturn(serviceFailure(lengthRequiredError(5000)));

        MvcResult response = mockMvc.
                perform(
                        put("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Type", "application/pdf").
                                content("My PDF content")).
                andExpect(status().isLengthRequired()).
                andDo(document("forminputresponsefileupload/file_fileUpdate_missingContentLength")).
                andReturn();

        assertResponseErrorKeyEqual(LENGTH_REQUIRED.name(), lengthRequiredError(5000), response);
    }

    @Test
    public void testUpdateFileButContentTypeHeaderInvalid() throws Exception {

        when(fileValidatorMock.validateFileHeaders("text/plain", "1000", "original.pdf")).thenReturn(serviceFailure(unsupportedMediaTypeByNameError(asList("application/pdf", "application/json"))));

        MvcResult response = mockMvc.
                perform(
                        put("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Type", "text/plain").
                                header("Content-Length", "1000").
                                content("My PDF content")).
                andExpect(status().isUnsupportedMediaType()).
                andDo(document("forminputresponsefileupload/file_fileUpdate_unsupportedContentType")).
                andReturn();

        assertResponseErrorKeyEqual(UNSUPPORTED_MEDIA_TYPE.name(), unsupportedMediaTypeByNameError(asList("application/pdf", "application/json")), response);
    }

    @Test
    public void testUpdateFileButContentTypeHeaderMissing() throws Exception {

        when(fileValidatorMock.validateFileHeaders(null, "1000", "original.pdf")).thenReturn(serviceFailure(unsupportedMediaTypeByNameError(asList("application/pdf", "application/json"))));

        MvcResult response = mockMvc.
                perform(
                        put("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Length", "1000").
                                content("My PDF content")).
                andExpect(status().isUnsupportedMediaType()).
                andDo(document("forminputresponsefileupload/file_fileUpdate_missingContentType")).
                andReturn();

        assertResponseErrorKeyEqual(UNSUPPORTED_MEDIA_TYPE.name(), unsupportedMediaTypeByNameError(asList("application/pdf", "application/json")), response);
    }

    @Test
    public void testUpdateFileButContentLengthHeaderMisreported() throws Exception {
        assertUpdateFileButErrorOccurs(new Error(FILES_INCORRECTLY_REPORTED_FILESIZE), "incorrectlyReportedContentLength", BAD_REQUEST);
    }

    @Test
    public void testUpdateFileButContentTypeHeaderMisreported() throws Exception {
        assertUpdateFileButErrorOccurs(new Error(FILES_INCORRECTLY_REPORTED_MEDIA_TYPE), "incorrectlyReportedContentType", UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    public void testUpdateFileButFormInputResponseNotFound() throws Exception {
        assertUpdateFileButErrorOccurs(new Error(GENERAL_NOT_FOUND), "formInputResponseNotFound", NOT_FOUND);
    }

    @Test
    public void testUpdateFileButFileNotFoundToUpdate() throws Exception {
        assertUpdateFileButErrorOccurs(new Error(GENERAL_NOT_FOUND), "noFileFoundToUpdate", NOT_FOUND);
    }


    @Test
    public void testDeleteFile() throws Exception {

        FormInputResponseFileEntryId formInputResponseFileEntryId = new FormInputResponseFileEntryId(123L, 456L, 789L);
        FormInputResponse unlinkedFormInputResponse = newFormInputResponse().build();

        when(applicationServiceMock.deleteFormInputResponseFileUpload(formInputResponseFileEntryId)).thenReturn(serviceSuccess(unlinkedFormInputResponse));

        MvcResult response = mockMvc.
                perform(
                        delete("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                header("IFS_AUTH_TOKEN", "123abc")).
                andExpect(status().isNoContent()).
                andDo(document("forminputresponsefileupload/file_fileDelete",
                        requestParameters(
                                parameterWithName("formInputId").description("Id of the FormInput that the user is responding to"),
                                parameterWithName("applicationId").description("Id of the Application that the FormInputResponse is related to"),
                                parameterWithName("processRoleId").description("Id of the ProcessRole that is responding to the FormInput")
                        ),
                        requestHeaders(
                                headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                        ))
                ).
                andReturn();

        String content = response.getResponse().getContentAsString();
        assertTrue(content.isEmpty());
    }

    @Test
    public void testDeleteFileButApplicationServiceCallFails() throws Exception {

        ServiceResult<FormInputResponse> failureResponse = serviceFailure(internalServerErrorError());

        when(applicationServiceMock.deleteFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        delete("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isInternalServerError()).
                andDo(document("forminputresponsefileupload/file_fileDelete_internalServerError")).
                andReturn();

        assertResponseErrorKeyEqual(GENERAL_UNEXPECTED_ERROR.name(), internalServerErrorError(), response);
    }

    @Test
    public void testDeleteFileButFormInputNotFound() throws Exception {
        assertDeleteFileButEntityNotFound(FormInput.class, "formInputNotFound");
    }

    @Test
    public void testDeleteFileButFormInputResponseNotFound() throws Exception {
        assertDeleteFileButEntityNotFound(FormInputResponse.class, "formInputResponseNotFound");
    }

    @Test
    public void testDeleteFileButApplicationNotFound() throws Exception {
        assertDeleteFileButEntityNotFound(Application.class, "applicationNotFound");
    }


    @Test
    public void testDeleteFileButProcessRoleNotFound() throws Exception {
        assertDeleteFileButEntityNotFound(ProcessRole.class, "processRoleNotFound");
    }

    @Test
    public void testDeleteFileButFileNotFoundToDelete() throws Exception {
        assertDeleteFileButEntityNotFound(File.class, "noFileFoundToDelete");
    }

    @Test
    public void testGetFileDetails() throws Exception {

        FormInputResponseFileEntryId fileEntryIdExpectations = fileEntryExpectations();

        FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(newFileEntryResource().build(), 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(applicationServiceMock.getFormInputResponseFileUpload(fileEntryIdExpectations)).thenReturn(serviceSuccess(new FormInputResponseFileAndContents(fileEntryResource, inputStreamSupplier)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/fileentry").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                header("IFS_AUTH_TOKEN", "123abc")
                ).
                andExpect(status().isOk()).
                andDo(document("forminputresponsefileupload/file_fileEntry",
                        requestParameters(
                                parameterWithName("formInputId").description("Id of the FormInput that the user is requesting the file details for"),
                                parameterWithName("applicationId").description("Id of the Application that the FormInputResponse is related to"),
                                parameterWithName("processRoleId").description("Id of the ProcessRole that owns the FormInputResponse")
                        ),
                        requestHeaders(
                                headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                        ))
                ).
                andReturn();

        String content = response.getResponse().getContentAsString();
        FormInputResponseFileEntryResource successfulResponse = new ObjectMapper().readValue(content, FormInputResponseFileEntryResource.class);
        assertEquals(fileEntryResource, successfulResponse);
    }

    @Test
    public void testGetFileDetailsButApplicationServiceCallFails() throws Exception {

        when(applicationServiceMock.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(serviceFailure(internalServerErrorError()));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/fileentry").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isInternalServerError()).
                andDo(document("forminputresponsefileupload/file_fileEntry_internalServerError")).
                andReturn();

        assertResponseErrorKeyEqual(GENERAL_UNEXPECTED_ERROR.name(), internalServerErrorError(), response);
    }

    @Test
    public void testGetFileDetailsButFileNotFound() throws Exception {
        assertGetFileDetailsButEntityNotFound(File.class, "fileNotFound");
    }

    @Test
    public void testGetFileDetailsButFormInputNotFound() throws Exception {
        assertGetFileDetailsButEntityNotFound(FormInput.class, "formInputNotFound");
    }

    @Test
    public void testGetFileDetailsButApplicationNotFound() throws Exception {
        assertGetFileDetailsButEntityNotFound(Application.class, "applicationNotFound");
    }

    @Test
    public void testGetFileDetailsButProcessRoleNotFound() throws Exception {
        assertGetFileDetailsButEntityNotFound(ProcessRole.class, "processRoleNotFound");
    }

    @Test
    public void testGetFileDetailsButformInputResponseNotFound() throws Exception {
        assertGetFileDetailsButEntityNotFound(FormInputResponse.class, "formInputResponseNotFound");
    }

    @Test
    public void testGetFileContents() throws Exception {

        FormInputResponseFileEntryId fileEntryIdExpectations = fileEntryExpectations();

        FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(newFileEntryResource().build(), 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> new ByteArrayInputStream("The returned binary file data".getBytes());

        when(applicationServiceMock.getFormInputResponseFileUpload(fileEntryIdExpectations)).thenReturn(serviceSuccess(new FormInputResponseFileAndContents(fileEntryResource, inputStreamSupplier)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                header("IFS_AUTH_TOKEN", "123abc")
                ).
                andExpect(status().isOk()).
                andDo(document("forminputresponsefileupload/file_fileDownload",
                        requestParameters(
                                parameterWithName("formInputId").description("Id of the FormInput that the user is requesting the file for"),
                                parameterWithName("applicationId").description("Id of the Application that the FormInputResponse is related to"),
                                parameterWithName("processRoleId").description("Id of the ProcessRole that owns the FormInputResponse")
                        ),
                        requestHeaders(
                                headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                        ))
                ).
                andReturn();

        assertEquals("The returned binary file data", response.getResponse().getContentAsString());
    }

    @Test
    public void testGetFileContentsButApplicationServiceCallFails() throws Exception {

        when(applicationServiceMock.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(serviceFailure(internalServerErrorError()));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isInternalServerError()).
                andDo(document("forminputresponsefileupload/file_fileDownload_internalServerError")).
                andReturn();

        assertResponseErrorKeyEqual(GENERAL_UNEXPECTED_ERROR.name(), internalServerErrorError(), response);
    }

    @Test
    public void testGetFileContentsButApplicationServiceCallThrowsException() throws Exception {

        when(applicationServiceMock.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenThrow(new RuntimeException("No files today!"));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isInternalServerError()).
                andReturn();

        assertResponseErrorKeyEqual(FILES_EXCEPTION_WHILE_RETRIEVING_FILE.name(), new Error(FILES_EXCEPTION_WHILE_RETRIEVING_FILE), response);
    }

    @Test
    public void testGetFileContentsButFileNotFound() throws Exception {
        assertGetFileButEntityNotFound(File.class, "fileNotFound");
    }

    @Test
    public void testGetFileContentsButFormInputNotFound() throws Exception {
        assertGetFileButEntityNotFound(FormInput.class, "formInputNotFound");
    }

    @Test
    public void testGetFileContentsButApplicationNotFound() throws Exception {
        assertGetFileButEntityNotFound(Application.class, "applicationNotFound");
    }

    @Test
    public void testGetFileContentsButProcessRoleNotFound() throws Exception {
        assertGetFileButEntityNotFound(ProcessRole.class, "processRoleNotFound");
    }

    @Test
    public void testGetFileContentsButformInputResponseNotFound() throws Exception {
        assertGetFileButEntityNotFound(FormInputResponse.class, "formInputResponseNotFound");
    }

    @Test
    public void testGetFileEntryDetails() throws Exception {

        FormInputResponseFileEntryId fileEntryIdExpectations = fileEntryExpectations();

        FileEntryResource fileEntryResource = newFileEntryResource().
                with(id(5678L)).
                withFilesizeBytes(1234L).
                with(name("original filename.pdf")).
                withMediaType("application/pdf").
                build();

        FormInputResponseFileEntryResource formInputFileEntryResource = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);

        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(applicationServiceMock.getFormInputResponseFileUpload(fileEntryIdExpectations)).thenReturn(serviceSuccess(new FormInputResponseFileAndContents(formInputFileEntryResource, inputStreamSupplier)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/fileentry").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                header("IFS_AUTH_TOKEN", "123abc")
                ).
                andExpect(status().isOk()).
                andDo(document("forminputresponsefileupload/file_fileEntry",
                        requestParameters(
                                parameterWithName("formInputId").description("Id of the FormInput that the user is requesting the file entry for"),
                                parameterWithName("applicationId").description("Id of the Application that the FormInputResponse is related to"),
                                parameterWithName("processRoleId").description("Id of the ProcessRole that owns the FormInputResponse")
                        ),
                        requestHeaders(
                                headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                        ))
                ).
                andReturn();

        FormInputResponseFileEntryResource returnedFileEntryDetails = new ObjectMapper().readValue(response.getResponse().getContentAsString(), FormInputResponseFileEntryResource.class);
        assertEquals(formInputFileEntryResource, returnedFileEntryDetails);
    }

    private void assertGetFileButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix) throws Exception {

        when(applicationServiceMock.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(serviceFailure(notFoundError(entityTypeNotFound)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isNotFound()).
                andDo(document("forminputresponsefileupload/file_fileDownload_" + documentationSuffix)).
                andReturn();

        assertResponseErrorKeyEqual(GENERAL_NOT_FOUND.name(), notFoundError(entityTypeNotFound), response);
    }

    private void assertGetFileDetailsButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix) throws Exception {
        when(applicationServiceMock.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(serviceFailure(notFoundError(entityTypeNotFound)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/fileentry").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isNotFound()).
                andDo(document("forminputresponsefileupload/file_fileEntry_" + documentationSuffix)).
                andReturn();

        assertResponseErrorKeyEqual(GENERAL_NOT_FOUND.name(), notFoundError(entityTypeNotFound), response);
    }

    private void assertCreateFileButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix) throws Exception {
        assertCreateFileButErrorOccurs(notFoundError(entityTypeNotFound), documentationSuffix, NOT_FOUND);
    }

    private void assertCreateFileButErrorOccurs(Error errorToReturn, String documentationSuffix, HttpStatus expectedStatus) throws Exception {

        ServiceResult<FormInputResponseFileEntryResource> failureResponse = serviceFailure(errorToReturn);

        when(fileValidatorMock.validateFileHeaders(isA(String.class), isA(String.class), isA(String.class))).thenReturn(serviceSuccess(new FileHeaderAttributes(MediaType.valueOf("application/pdf"), 1000, "original.pdf")));
        when(applicationServiceMock.createFormInputResponseFileUpload(isA(FormInputResponseFileEntryResource.class), isSupplierMatcher())).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Type", "application/pdf").
                                header("Content-Length", "1000").
                                content("My PDF content")).
                andDo(document("forminputresponsefileupload/file_fileUpload_" + documentationSuffix)).
                andReturn();

        assertEquals(expectedStatus.value(), response.getResponse().getStatus());
        assertResponseErrorKeyEqual(errorToReturn.getErrorKey(), errorToReturn, response);
    }

    private void assertUpdateFileButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix) throws Exception {
        assertUpdateFileButErrorOccurs(notFoundError(entityTypeNotFound), documentationSuffix, NOT_FOUND);
    }


    private void assertDeleteFileButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix) throws Exception {
        assertDeleteFileButErrorOccurs(notFoundError(entityTypeNotFound), documentationSuffix, NOT_FOUND, GENERAL_NOT_FOUND.name());
    }

    private void assertUpdateFileButErrorOccurs(Error errorToReturn, String documentationSuffix, HttpStatus expectedStatus) throws Exception {

        ServiceResult<Void> failureResponse = serviceFailure(errorToReturn);

        when(fileValidatorMock.validateFileHeaders(isA(String.class), isA(String.class), isA(String.class))).thenReturn(serviceSuccess(new FileHeaderAttributes(MediaType.valueOf("application/pdf"), 1000, "original.pdf")));
        when(applicationServiceMock.updateFormInputResponseFileUpload(isA(FormInputResponseFileEntryResource.class), isSupplierMatcher())).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        put("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("filename", "original.pdf").
                                header("Content-Type", "application/pdf").
                                header("Content-Length", "1000").
                                content("My PDF content")).
                andDo(document("forminputresponsefileupload/file_fileUpdate_" + documentationSuffix)).
                andReturn();

        assertEquals(expectedStatus.value(), response.getResponse().getStatus());
        assertResponseErrorKeyEqual(errorToReturn.getErrorKey(), errorToReturn, response);
    }

    private <T> Supplier<T> isSupplierMatcher() {
        return isA(Supplier.class);
    }

    private void assertDeleteFileButErrorOccurs(Error errorToReturn, String documentationSuffix, HttpStatus expectedStatus, String expectedMessage) throws Exception {

        ServiceResult<FormInputResponse> failureResponse = serviceFailure(errorToReturn);

        when(applicationServiceMock.deleteFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        delete("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andDo(document("forminputresponsefileupload/file_fileDelete_" + documentationSuffix)).
                andReturn();

        assertEquals(expectedStatus.value(), response.getResponse().getStatus());
        assertResponseErrorKeyEqual(expectedMessage, errorToReturn, response);
    }

    private FormInputResponseFileEntryId fileEntryExpectations() {
        return argThat(lambdaMatches(fileEntryId -> {
            assertEquals(123L, fileEntryId.getFormInputId());
            assertEquals(456L, fileEntryId.getApplicationId());
            assertEquals(789L, fileEntryId.getProcessRoleId());
            return true;
        }));
    }
}
