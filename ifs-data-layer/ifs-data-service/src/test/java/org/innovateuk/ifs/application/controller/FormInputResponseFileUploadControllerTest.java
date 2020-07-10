package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryId;
import org.innovateuk.ifs.application.resource.FormInputResponseFileEntryResource;
import org.innovateuk.ifs.application.transactional.ApplicationFormInputUploadService;
import org.innovateuk.ifs.application.transactional.FormInputResponseFileAndContents;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileHeaderAttributes;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.InputStreamTestUtil.assertInputStreamContents;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.*;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.isA;
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

    private static final long maxFilesize = 1234L;
    private static final long formInputId = 4324L;
    private static final long fileEntryId = 111L;

    @Mock
    private ApplicationFormInputUploadService applicationFormInputUploadService;

    @Mock(name = "fileValidator")
    private FilesizeAndTypeFileValidator<Long> fileValidatorMock;

    @Override
    protected FormInputResponseFileUploadController supplyControllerUnderTest() {
        FormInputResponseFileUploadController controller = new FormInputResponseFileUploadController();
        ReflectionTestUtils.setField(controller, "maxFilesizeBytesForFormInputResponses", maxFilesize);
        return controller;
    }

    @Test
    public void testCreateFile() throws Exception {

        // having to "fake" the request body as JSON because Spring Restdocs does not support other content types other
        // than JSON and XML
        String dummyContent = "{\"description\":\"The request body is the binary content of the file being uploaded - it is NOT JSON as seen here!\"}";

        FormInputResponseFileEntryResource createdResource = new FormInputResponseFileEntryResource(newFileEntryResource().with(id(1111L)).build(), formInputId, 456L, 789L, Optional.empty());
        ServiceResult<FormInputResponseFileEntryResource> successResponse = serviceSuccess(createdResource);

        FileHeaderAttributes fileAttributesAfterValidation = new FileHeaderAttributes(MediaType.valueOf("application/pdf"), 1000L, "original.pdf");

        when(fileValidatorMock.validateFileHeaders("application/pdf", "1000", "original.pdf", formInputId, 1234L)).thenReturn(serviceSuccess(fileAttributesAfterValidation));
        when(applicationFormInputUploadService.uploadResponse(createFileEntryResourceExpectations("original.pdf"), createInputStreamExpectations(dummyContent))).thenReturn(successResponse);

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", formInputId + "").
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
        FormInputResponseFileEntryCreatedResponse createdResponse = objectMapper.readValue(content, FormInputResponseFileEntryCreatedResponse.class);
        assertEquals(1111L, createdResponse.getFileEntryId());

        verify(fileValidatorMock).validateFileHeaders("application/pdf", "1000", "original.pdf", formInputId, 1234L);
        verify(applicationFormInputUploadService).uploadResponse(createFileEntryResourceExpectations("original.pdf"), createInputStreamExpectations(dummyContent));
    }

    private FormInputResponseFileEntryResource createFileEntryResourceExpectations(String expectedFilename) {
        return createLambdaMatcher(resource -> {
            assertEquals(formInputId, resource.getCompoundId().getFormInputId());
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

        when(fileValidatorMock.validateFileHeaders(isA(String.class), isA(String.class), isA(String.class), isA(Long.class), isA(Long.class))).thenReturn(serviceSuccess(new FileHeaderAttributes(MediaType.valueOf("application/pdf"), 1000, "original.pdf")));
        when(applicationFormInputUploadService.uploadResponse(isA(FormInputResponseFileEntryResource.class), isSupplierMatcher())).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", formInputId + "").
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
        RestErrorResponse restErrorResponse = objectMapper.readValue(content, RestErrorResponse.class);
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

        when(fileValidatorMock.validateFileHeaders("application/pdf", "99999999", "original.pdf", formInputId, maxFilesize)).thenReturn(serviceFailure(payloadTooLargeError(5000)));

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", formInputId + "").
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

        when(fileValidatorMock.validateFileHeaders("application/pdf", null, "original.pdf", formInputId, maxFilesize)).thenReturn(serviceFailure(lengthRequiredError(5000L)));

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", formInputId + "").
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

        when(fileValidatorMock.validateFileHeaders("text/plain", "1000", "original.pdf", formInputId, maxFilesize)).thenReturn(serviceFailure(unsupportedMediaTypeByNameError(asList("application/pdf", "application/json"))));

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", formInputId + "").
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

        when(fileValidatorMock.validateFileHeaders(null, "1000", "original.pdf", formInputId, maxFilesize)).thenReturn(serviceFailure(unsupportedMediaTypeByNameError(asList("application/pdf", "application/json"))));

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", formInputId + "").
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
    public void testDeleteFile() throws Exception {

        FormInputResponseFileEntryId formInputResponseFileEntryId = new FormInputResponseFileEntryId(formInputId, 456L, 789L, Optional.of(fileEntryId));
        FormInputResponse unlinkedFormInputResponse = newFormInputResponse().build();

        when(applicationFormInputUploadService.deleteFormInputResponseFileUpload(formInputResponseFileEntryId)).thenReturn(serviceSuccess(unlinkedFormInputResponse));

        MvcResult response = mockMvc.
                perform(
                        delete("/forminputresponse/file").
                                param("formInputId", formInputId + "").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("fileEntryId", String.valueOf(fileEntryId)).
                                header("IFS_AUTH_TOKEN", "123abc")).
                andExpect(status().isNoContent()).
                andDo(document("forminputresponsefileupload/file_fileDelete",
                        requestParameters(
                                parameterWithName("formInputId").description("Id of the FormInput that the user is responding to"),
                                parameterWithName("applicationId").description("Id of the Application that the FormInputResponse is related to"),
                                parameterWithName("processRoleId").description("Id of the ProcessRole that is responding to the FormInput"),
                                parameterWithName("fileEntryId").description("Id file entry to delete")
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

        when(applicationFormInputUploadService.deleteFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        delete("/forminputresponse/file").
                                param("formInputId", formInputId + "").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("fileEntryId", String.valueOf(fileEntryId))).
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

        FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(newFileEntryResource().build(), formInputId, 456L, 789L, Optional.of(fileEntryId));
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(applicationFormInputUploadService.getFormInputResponseFileUpload(fileEntryIdExpectations)).thenReturn(serviceSuccess(new FormInputResponseFileAndContents(fileEntryResource, inputStreamSupplier)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/fileentry").
                                param("formInputId", formInputId + "").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("fileEntryId", String.valueOf(fileEntryId)).
                                header("IFS_AUTH_TOKEN", "123abc")
                ).
                andExpect(status().isOk()).
                andDo(document("forminputresponsefileupload/file_fileEntry",
                        requestParameters(
                                parameterWithName("formInputId").description("Id of the FormInput that the user is requesting the file details for"),
                                parameterWithName("applicationId").description("Id of the Application that the FormInputResponse is related to"),
                                parameterWithName("processRoleId").description("Id of the ProcessRole that owns the FormInputResponse"),
                                parameterWithName("fileEntryId").description("Id of the file entry to get")
                        ),
                        requestHeaders(
                                headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                        ))
                ).
                andReturn();

        String content = response.getResponse().getContentAsString();
        FormInputResponseFileEntryResource successfulResponse = objectMapper.readValue(content, FormInputResponseFileEntryResource.class);
        assertEquals(fileEntryResource, successfulResponse);
    }

    @Test
    public void testGetFileDetailsButApplicationServiceCallFails() throws Exception {

        when(applicationFormInputUploadService.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(serviceFailure(internalServerErrorError()));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/fileentry").
                                param("formInputId", formInputId + "").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                header("IFS_AUTH_TOKEN", "123abc")).
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

        FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(newFileEntryResource().build(), formInputId, 456L, 789L, Optional.of(fileEntryId));
        Supplier<InputStream> inputStreamSupplier = () -> new ByteArrayInputStream("The returned binary file data".getBytes());

        when(applicationFormInputUploadService.getFormInputResponseFileUpload(fileEntryIdExpectations)).thenReturn(serviceSuccess(new FormInputResponseFileAndContents(fileEntryResource, inputStreamSupplier)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/file").
                                param("formInputId", formInputId + "").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("fileEntryId", String.valueOf(fileEntryId)).
                                header("IFS_AUTH_TOKEN", "123abc")
                ).
                andExpect(status().isOk()).
                andDo(document("forminputresponsefileupload/file_fileDownload",
                        requestParameters(
                                parameterWithName("formInputId").description("Id of the FormInput that the user is requesting the file for"),
                                parameterWithName("applicationId").description("Id of the Application that the FormInputResponse is related to"),
                                parameterWithName("processRoleId").description("Id of the ProcessRole that owns the FormInputResponse"),
                                parameterWithName("fileEntryId").description("Id of the file entry to get")
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

        when(applicationFormInputUploadService.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(serviceFailure(internalServerErrorError()));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/file").
                                param("formInputId", formInputId + "").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                header("IFS_AUTH_TOKEN", "123abc")).
                andExpect(status().isInternalServerError()).
                andDo(document("forminputresponsefileupload/file_fileDownload_internalServerError")).
                andReturn();

        assertResponseErrorKeyEqual(GENERAL_UNEXPECTED_ERROR.name(), internalServerErrorError(), response);
    }

    @Test
    public void testGetFileContentsButApplicationServiceCallThrowsException() throws Exception {

        when(applicationFormInputUploadService.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenThrow(new RuntimeException("No files today!"));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/file").
                                param("formInputId", formInputId + "").
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
                withName("original filename.pdf").
                withMediaType("application/pdf").
                build();

        FormInputResponseFileEntryResource formInputFileEntryResource = new FormInputResponseFileEntryResource(fileEntryResource, formInputId, 456L, 789L, Optional.of(fileEntryId));

        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(applicationFormInputUploadService.getFormInputResponseFileUpload(fileEntryIdExpectations)).thenReturn(serviceSuccess(new FormInputResponseFileAndContents(formInputFileEntryResource, inputStreamSupplier)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/fileentry").
                                param("formInputId", formInputId + "").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("fileEntryId", String.valueOf(fileEntryId)).
                                header("IFS_AUTH_TOKEN", "123abc")
                ).
                andExpect(status().isOk()).
                andDo(document("forminputresponsefileupload/file_fileEntry",
                        requestParameters(
                                parameterWithName("formInputId").description("Id of the FormInput that the user is requesting the file entry for"),
                                parameterWithName("applicationId").description("Id of the Application that the FormInputResponse is related to"),
                                parameterWithName("processRoleId").description("Id of the ProcessRole that owns the FormInputResponse"),
                                parameterWithName("fileEntryId").description("Id of the file entry to get")
                        ),
                        requestHeaders(
                                headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                        ))
                ).
                andReturn();

        FormInputResponseFileEntryResource returnedFileEntryDetails = objectMapper.readValue(response.getResponse().getContentAsString(), FormInputResponseFileEntryResource.class);
        assertEquals(formInputFileEntryResource, returnedFileEntryDetails);
    }

    private void assertGetFileButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix) throws Exception {

        when(applicationFormInputUploadService.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(serviceFailure(notFoundError(entityTypeNotFound)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/file").
                                param("formInputId", formInputId + "").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                header("IFS_AUTH_TOKEN", "123abc")).
                andExpect(status().isNotFound()).
                andDo(document("forminputresponsefileupload/file_fileDownload_" + documentationSuffix)).
                andReturn();

        assertResponseErrorKeyEqual(GENERAL_NOT_FOUND.name(), notFoundError(entityTypeNotFound), response);
    }

    private void assertGetFileDetailsButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix) throws Exception {
        when(applicationFormInputUploadService.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(serviceFailure(notFoundError(entityTypeNotFound)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/fileentry").
                                param("formInputId", formInputId + "").
                                param("applicationId", "456").
                                param("processRoleId", "789").
                                param("fileEntryId", String.valueOf(fileEntryId)).
                                header("IFS_AUTH_TOKEN", "123abc")).
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

        when(fileValidatorMock.validateFileHeaders(isA(String.class), isA(String.class), isA(String.class), isA(Long.class), isA(Long.class))).thenReturn(serviceSuccess(new FileHeaderAttributes(MediaType.valueOf("application/pdf"), 1000, "original.pdf")));
        when(applicationFormInputUploadService.uploadResponse(isA(FormInputResponseFileEntryResource.class), isSupplierMatcher())).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        post("/forminputresponse/file").
                                param("formInputId", formInputId + "").
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

    private void assertDeleteFileButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix) throws Exception {
        assertDeleteFileButErrorOccurs(notFoundError(entityTypeNotFound), documentationSuffix, NOT_FOUND, GENERAL_NOT_FOUND.name());
    }

    private <T> Supplier<T> isSupplierMatcher() {
        return isA(Supplier.class);
    }

    private void assertDeleteFileButErrorOccurs(Error errorToReturn, String documentationSuffix, HttpStatus expectedStatus, String expectedMessage) throws Exception {

        ServiceResult<FormInputResponse> failureResponse = serviceFailure(errorToReturn);

        when(applicationFormInputUploadService.deleteFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        delete("/forminputresponse/file").
                                param("formInputId", formInputId + "").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andDo(document("forminputresponsefileupload/file_fileDelete_" + documentationSuffix)).
                andReturn();

        assertEquals(expectedStatus.value(), response.getResponse().getStatus());
        assertResponseErrorKeyEqual(expectedMessage, errorToReturn, response);
    }

    private FormInputResponseFileEntryId fileEntryExpectations() {
        return argThat(lambdaMatches(fileEntryId -> {
            assertEquals(formInputId, fileEntryId.getFormInputId());
            assertEquals(456L, fileEntryId.getApplicationId());
            assertEquals(789L, fileEntryId.getProcessRoleId());
            return true;
        }));
    }
}
