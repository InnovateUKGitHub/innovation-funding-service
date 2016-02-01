package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.domain.FormInput;
import com.worth.ifs.form.domain.FormInputResponse;
import com.worth.ifs.transactional.Error;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.InputStreamTestUtil.assertInputStreamContents;
import static com.worth.ifs.LambdaMatcher.lambdaMatches;
import static com.worth.ifs.application.transactional.ApplicationServiceImpl.ServiceFailures.FILE_ALREADY_LINKED_TO_FORM_INPUT_RESPONSE;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.file.transactional.FileServiceImpl.ServiceFailures.*;
import static com.worth.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static com.worth.ifs.transactional.BaseTransactionalService.Failures.NOT_FOUND_ENTITY;
import static com.worth.ifs.transactional.ServiceResult.serviceFailure;
import static com.worth.ifs.transactional.ServiceResult.serviceSuccess;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
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

    @Override
    protected FormInputResponseFileUploadController supplyControllerUnderTest() {

        FormInputResponseFileUploadController controller = new FormInputResponseFileUploadController();
        controller.setMaxFilesizeBytes(5000L);
        controller.setValidMediaTypes(asList("application/pdf", "application/json"));
        return controller;
    }

    @Test
    public void testCreateFile() throws Exception {

        // having to "fake" the request body as JSON because Spring Restdocs does not support other content types other
        // than JSON and XML
        String dummyContent = "{\"description\":\"The request body is the binary content of the file being uploaded - it is NOT JSON as seen here!\"}";

        FormInputResponseFileEntryResource resourceExpectations = argThat(lambdaMatches(resource -> {
            assertEquals(123L, resource.getCompoundId().getFormInputId());
            assertEquals(456L, resource.getCompoundId().getApplicationId());
            assertEquals(789L, resource.getCompoundId().getProcessRoleId());

            assertNull(resource.getFileEntryResource().getId());
            assertEquals(1000, resource.getFileEntryResource().getFilesizeBytes());
            assertEquals("application/pdf", resource.getFileEntryResource().getMediaType());
            assertEquals("original.pdf", resource.getFileEntryResource().getName());
            return true;
        }));

        Supplier<InputStream> inputStreamExpectations = argThat(lambdaMatches(inputStreamSupplier ->
                assertInputStreamContents(inputStreamSupplier.get(), dummyContent)));

        FormInputResponseFileEntryResource createdResource = new FormInputResponseFileEntryResource(newFileEntryResource().with(id(1111L)).build(), 123L, 456L, 789L);
        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> successResponse = serviceSuccess(Pair.of(new File(""), createdResource));

        when(applicationService.createFormInputResponseFileUpload(resourceExpectations, inputStreamExpectations)).thenReturn(successResponse);

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
                                fieldWithPath("fileEntryId").description("Id of the FileEntry that was created"),
                                fieldWithPath("message").description("A plain text descriptive message of the action that was performed e.g. \"File created successfully\"")
                        ))
                ).
                andReturn();

        String content = response.getResponse().getContentAsString();
        FormInputResponseFileEntryJsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, FormInputResponseFileEntryJsonStatusResponse.class);
        assertEquals(1111L, jsonResponse.getFileEntryId());
    }

    @Test
    public void testCreateFileButApplicationServiceCallFails() throws Exception {

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> failureResponse =
                serviceFailure(new Error("No files today!", INTERNAL_SERVER_ERROR));

        when(applicationService.createFormInputResponseFileUpload(isA(FormInputResponseFileEntryResource.class), isA(Supplier.class))).thenReturn(failureResponse);

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
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Error creating file", jsonResponse.getMessage());
    }

    @Test
    public void testCreateFileButApplicationServiceCallFailsThrowsException() throws Exception {

        when(applicationService.createFormInputResponseFileUpload(isA(FormInputResponseFileEntryResource.class), isA(Supplier.class))).thenThrow(new RuntimeException("No files today!"));

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
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Error creating file", jsonResponse.getMessage());
    }

    @Test
    public void testCreateFileButFormInputNotFound() throws Exception {
        assertCreateFileButEntityNotFound(FormInput.class, "formInputNotFound", "Unable to find Form Input");
    }


    @Test
    public void testCreateFileButApplicationNotFound() throws Exception {
        assertCreateFileButEntityNotFound(Application.class, "applicationNotFound", "Unable to find Application");
    }


    @Test
    public void testCreateFileButProcessRoleNotFound() throws Exception {
        assertCreateFileButEntityNotFound(ProcessRole.class, "processRoleNotFound", "Unable to find Process Role");
    }

    @Test
    public void testCreateFileButContentLengthHeaderTooLarge() throws Exception {

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

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("File upload was too large for FormInputResponse.  Max filesize in bytes is 5000", jsonResponse.getMessage());
    }

    @Test
    public void testCreateFileButContentLengthHeaderMissing() throws Exception {

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

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Please supply a valid Content-Length HTTP header.  Maximum 5000", jsonResponse.getMessage());
    }

    @Test
    public void testCreateFileButContentTypeHeaderInvalid() throws Exception {

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

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Please supply a valid Content-Type HTTP header.  Valid types are application/pdf, application/json", jsonResponse.getMessage());
    }

    @Test
    public void testCreateFileButContentTypeHeaderMissing() throws Exception {

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

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Please supply a valid Content-Type HTTP header.  Valid types are application/pdf, application/json", jsonResponse.getMessage());
    }

    @Test
    public void testCreateFileButContentLengthHeaderMisreported() throws Exception {
        assertCreateFileButErrorOccurs(new Error(INCORRECTLY_REPORTED_FILESIZE), "incorrectlyReportedContentLength", BAD_REQUEST, "Incorrectly reported filesize");
    }

    @Test
    public void testCreateFileButContentTypeHeaderMisreported() throws Exception {
        assertCreateFileButErrorOccurs(new Error(INCORRECTLY_REPORTED_MEDIA_TYPE), "incorrectlyReportedContentType", UNSUPPORTED_MEDIA_TYPE, "Incorrectly reported Content Type");
    }

    @Test
    public void testCreateFileButDuplicateFileEncountered() throws Exception {
        assertCreateFileButErrorOccurs(new Error(DUPLICATE_FILE_CREATED), "duplicateFile", CONFLICT, "File already exists");
    }

    @Test
    public void testCreateFileButFormInputResponseAlreadyHasFileLinked() throws Exception {
        assertCreateFileButErrorOccurs(new Error(FILE_ALREADY_LINKED_TO_FORM_INPUT_RESPONSE), "fileAlreadyLinked", CONFLICT, "File already linked to Form Input Response");
    }

    @Test
    public void testUpdateFile() throws Exception {

        // having to "fake" the request body as JSON because Spring Restdocs does not support other content types other
        // than JSON and XML
        String dummyContent = "{\"description\":\"The request body is the binary content of the file being uploaded - it is NOT JSON as seen here!\"}";

        FormInputResponseFileEntryResource resourceExpectations = argThat(lambdaMatches(resource -> {
            assertEquals(123L, resource.getCompoundId().getFormInputId());
            assertEquals(456L, resource.getCompoundId().getApplicationId());
            assertEquals(789L, resource.getCompoundId().getProcessRoleId());

            assertNull(resource.getFileEntryResource().getId());
            assertEquals(1000, resource.getFileEntryResource().getFilesizeBytes());
            assertEquals("application/pdf", resource.getFileEntryResource().getMediaType());
            assertEquals("updated.pdf", resource.getFileEntryResource().getName());
            return true;
        }));

        Supplier<InputStream> inputStreamExpectations = argThat(lambdaMatches(inputStreamSupplier ->
                assertInputStreamContents(inputStreamSupplier.get(), dummyContent)));

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> successResponse =
                serviceSuccess(Pair.of(new File(""), new FormInputResponseFileEntryResource(newFileEntryResource().with(id(1111L)).build(), 123L, 456L, 789L)));

        when(applicationService.updateFormInputResponseFileUpload(resourceExpectations, inputStreamExpectations)).thenReturn(successResponse);

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
                        requestFields(fieldWithPath("description").description("The body of the request should be the binary data of the file being uploaded (and NOT JSON as shown in example)")),
                        responseFields(
                                fieldWithPath("fileEntryId").description("Id of the FileEntry that was created"),
                                fieldWithPath("message").description("A plain text descriptive message of the action that was performed e.g. \"File created successfully\"")
                        ))
                ).
                andReturn();

        String content = response.getResponse().getContentAsString();
        FormInputResponseFileEntryJsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, FormInputResponseFileEntryJsonStatusResponse.class);
        assertEquals(1111L, jsonResponse.getFileEntryId());
    }

    @Test
    public void testUpdateFileButApplicationServiceCallFails() throws Exception {

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> failureResponse =
                serviceFailure(new Error("No files today!", INTERNAL_SERVER_ERROR));

        when(applicationService.updateFormInputResponseFileUpload(isA(FormInputResponseFileEntryResource.class), isA(Supplier.class))).thenReturn(failureResponse);

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

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Error updating file", jsonResponse.getMessage());
    }

    @Test
    public void testUpdateFileButApplicationServiceCallFailsThrowsException() throws Exception {

        when(applicationService.updateFormInputResponseFileUpload(isA(FormInputResponseFileEntryResource.class), isA(Supplier.class))).thenThrow(new RuntimeException("No files today!"));

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
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Error updating file", jsonResponse.getMessage());
    }

    @Test
    public void testUpdateFileButFormInputNotFound() throws Exception {
        assertUpdateFileButEntityNotFound(FormInput.class, "formInputNotFound", "Unable to find Form Input");
    }


    @Test
    public void testUpdateFileButApplicationNotFound() throws Exception {
        assertUpdateFileButEntityNotFound(Application.class, "applicationNotFound", "Unable to find Application");
    }


    @Test
    public void testUpdateFileButProcessRoleNotFound() throws Exception {
        assertUpdateFileButEntityNotFound(ProcessRole.class, "processRoleNotFound", "Unable to find Process Role");
    }

    @Test
    public void testUpdateFileButContentLengthHeaderTooLarge() throws Exception {

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

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("File upload was too large for FormInputResponse.  Max filesize in bytes is 5000", jsonResponse.getMessage());
    }

    @Test
    public void testUpdateFileButContentLengthHeaderMissing() throws Exception {

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

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Please supply a valid Content-Length HTTP header.  Maximum 5000", jsonResponse.getMessage());
    }

    @Test
    public void testUpdateFileButContentTypeHeaderInvalid() throws Exception {

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

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Please supply a valid Content-Type HTTP header.  Valid types are application/pdf, application/json", jsonResponse.getMessage());
    }

    @Test
    public void testUpdateFileButContentTypeHeaderMissing() throws Exception {

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

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Please supply a valid Content-Type HTTP header.  Valid types are application/pdf, application/json", jsonResponse.getMessage());
    }

    @Test
    public void testUpdateFileButContentLengthHeaderMisreported() throws Exception {
        assertUpdateFileButErrorOccurs(new Error(INCORRECTLY_REPORTED_FILESIZE), "incorrectlyReportedContentLength", BAD_REQUEST, "Incorrectly reported filesize");
    }

    @Test
    public void testUpdateFileButContentTypeHeaderMisreported() throws Exception {
        assertUpdateFileButErrorOccurs(new Error(INCORRECTLY_REPORTED_MEDIA_TYPE), "incorrectlyReportedContentType", UNSUPPORTED_MEDIA_TYPE, "Incorrectly reported Content Type");
    }

    @Test
    public void testUpdateFileButFormInputResponseNotFound() throws Exception {
        assertUpdateFileButErrorOccurs(new Error(NOT_FOUND_ENTITY), "formInputResponseNotFound", NOT_FOUND, "Unable to find Form Input Response");
    }

    @Test
    public void testUpdateFileButFileNotFoundToUpdate() throws Exception {
        assertUpdateFileButErrorOccurs(new Error(NOT_FOUND_ENTITY), "noFileFoundToUpdate", NOT_FOUND, "Unable to find file");
    }


    @Test
    public void testDeleteFile() throws Exception {

        FormInputResponseFileEntryId formInputResponseFileEntryId = new FormInputResponseFileEntryId(123L, 456L, 789L);
        FormInputResponse unlinkedFormInputResponse = newFormInputResponse().build();

        when(applicationService.deleteFormInputResponseFileUpload(formInputResponseFileEntryId)).thenReturn(serviceSuccess(unlinkedFormInputResponse));

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
                        ),
                        responseFields(
                                fieldWithPath("message").description("A plain text descriptive message of the action that was performed e.g. \"File deleted successfully\"")
                        ))
                ).
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("File deleted successfully", jsonResponse.getMessage());
    }

    @Test
    public void testDeleteFileButApplicationServiceCallFails() throws Exception {

        ServiceResult<FormInputResponse> failureResponse = serviceFailure(new Error("No files today!", INTERNAL_SERVER_ERROR));

        when(applicationService.deleteFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        delete("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isInternalServerError()).
                andDo(document("forminputresponsefileupload/file_fileDelete_internalServerError")).
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Error deleting file", jsonResponse.getMessage());
    }

    @Test
    public void testDeleteFileButApplicationServiceCallFailsThrowsException() throws Exception {

        when(applicationService.deleteFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenThrow(new RuntimeException("No files today!"));

        MvcResult response = mockMvc.
                perform(
                        delete("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isInternalServerError()).
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Error deleting file", jsonResponse.getMessage());
    }

    @Test
    public void testDeleteFileButFormInputNotFound() throws Exception {
        assertDeleteFileButEntityNotFound(FormInput.class, "formInputNotFound", "Unable to find Form Input");
    }

    @Test
    public void testDeleteFileButFormInputResponseNotFound() throws Exception {
        assertDeleteFileButEntityNotFound(FormInputResponse.class, "formInputResponseNotFound", "Unable to find Form Input Response");
    }

    @Test
    public void testDeleteFileButApplicationNotFound() throws Exception {
        assertDeleteFileButEntityNotFound(Application.class, "applicationNotFound", "Unable to find Application");
    }


    @Test
    public void testDeleteFileButProcessRoleNotFound() throws Exception {
        assertDeleteFileButEntityNotFound(ProcessRole.class, "processRoleNotFound", "Unable to find Process Role");
    }

    @Test
    public void testDeleteFileButFileNotFoundToDelete() throws Exception {
        assertDeleteFileButEntityNotFound(File.class, "noFileFoundToDelete", "Unable to find file");
    }

    @Test
    public void testGetFileDetails() throws Exception {

        FormInputResponseFileEntryId fileEntryIdExpectations = argThat(lambdaMatches(fileEntryId -> {
            assertEquals(123L, fileEntryId.getFormInputId());
            assertEquals(456L, fileEntryId.getApplicationId());
            assertEquals(789L, fileEntryId.getProcessRoleId());
            return true;
        }));

        FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(newFileEntryResource().build(), 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(applicationService.getFormInputResponseFileUpload(fileEntryIdExpectations)).thenReturn(serviceSuccess(Pair.of(fileEntryResource, inputStreamSupplier)));

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
        FormInputResponseFileEntryResource jsonResponse = new ObjectMapper().readValue(content, FormInputResponseFileEntryResource.class);
        assertEquals(fileEntryResource, jsonResponse);
    }

    @Test
    public void testGetFileDetailsButApplicationServiceCallFails() throws Exception {

        when(applicationService.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(serviceFailure(new Error("No files today!", INTERNAL_SERVER_ERROR)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/fileentry").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isInternalServerError()).
                andDo(document("forminputresponsefileupload/file_fileEntry_internalServerError")).
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Error retrieving file", jsonResponse.getMessage());
    }

    @Test
    public void testGetFileDetailsButApplicationServiceCallThrowsException() throws Exception {

        when(applicationService.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenThrow(new RuntimeException("No files today!"));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/fileentry").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isInternalServerError()).
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Error retrieving file", jsonResponse.getMessage());
    }

    @Test
    public void testGetFileDetailsButFileNotFound() throws Exception {
        assertGetFileDetailsButEntityNotFound(File.class, "fileNotFound", "Unable to find file");
    }

    @Test
    public void testGetFileDetailsButFormInputNotFound() throws Exception {
        assertGetFileDetailsButEntityNotFound(FormInput.class, "formInputNotFound", "Unable to find Form Input");
    }

    @Test
    public void testGetFileDetailsButApplicationNotFound() throws Exception {
        assertGetFileDetailsButEntityNotFound(Application.class, "applicationNotFound", "Unable to find Application");
    }

    @Test
    public void testGetFileDetailsButProcessRoleNotFound() throws Exception {
        assertGetFileDetailsButEntityNotFound(ProcessRole.class, "processRoleNotFound", "Unable to find Process Role");
    }

    @Test
    public void testGetFileDetailsButformInputResponseNotFound() throws Exception {
        assertGetFileDetailsButEntityNotFound(FormInputResponse.class, "formInputResponseNotFound", "Unable to find Form Input Response");
    }

    @Test
    public void testGetFileContents() throws Exception {

        FormInputResponseFileEntryId fileEntryIdExpectations = argThat(lambdaMatches(fileEntryId -> {
            assertEquals(123L, fileEntryId.getFormInputId());
            assertEquals(456L, fileEntryId.getApplicationId());
            assertEquals(789L, fileEntryId.getProcessRoleId());
            return true;
        }));

        FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(newFileEntryResource().build(), 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> new ByteArrayInputStream("The returned binary file data".getBytes());

        when(applicationService.getFormInputResponseFileUpload(fileEntryIdExpectations)).thenReturn(serviceSuccess(Pair.of(fileEntryResource, inputStreamSupplier)));

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

        // TODO DW - INFUND-854 - replace serviceFailure(new Error(...)) with ServiceFailures.<error name>()?
        when(applicationService.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(serviceFailure(new Error("No files today!", INTERNAL_SERVER_ERROR)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isInternalServerError()).
                andDo(document("forminputresponsefileupload/file_fileDownload_internalServerError")).
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Error retrieving file", jsonResponse.getMessage());
    }

    @Test
    public void testGetFileContentsButApplicationServiceCallThrowsException() throws Exception {

        when(applicationService.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenThrow(new RuntimeException("No files today!"));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isInternalServerError()).
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Error retrieving file", jsonResponse.getMessage());
    }

    @Test
    public void testGetFileContentsButFileNotFound() throws Exception {
        assertGetFileButEntityNotFound(File.class, "fileNotFound", "Unable to find file");
    }

    @Test
    public void testGetFileContentsButFormInputNotFound() throws Exception {
        assertGetFileButEntityNotFound(FormInput.class, "formInputNotFound", "Unable to find Form Input");
    }

    @Test
    public void testGetFileContentsButApplicationNotFound() throws Exception {
        assertGetFileButEntityNotFound(Application.class, "applicationNotFound", "Unable to find Application");
    }

    @Test
    public void testGetFileContentsButProcessRoleNotFound() throws Exception {
        assertGetFileButEntityNotFound(ProcessRole.class, "processRoleNotFound", "Unable to find Process Role");
    }

    @Test
    public void testGetFileContentsButformInputResponseNotFound() throws Exception {
        assertGetFileButEntityNotFound(FormInputResponse.class, "formInputResponseNotFound", "Unable to find Form Input Response");
    }

    @Test
    public void testGetFileEntryDetails() throws Exception {

        FormInputResponseFileEntryId fileEntryIdExpectations = argThat(lambdaMatches(fileEntryId -> {
            assertEquals(123L, fileEntryId.getFormInputId());
            assertEquals(456L, fileEntryId.getApplicationId());
            assertEquals(789L, fileEntryId.getProcessRoleId());
            return true;
        }));

        FileEntryResource fileEntryResource = newFileEntryResource().
                with(id(5678L)).
                withFilesizeBytes(1234L).
                with(name("original filename.pdf")).
                withMediaType("application/pdf").
                build();

        FormInputResponseFileEntryResource formInputFileEntryResource = new FormInputResponseFileEntryResource(fileEntryResource, 123L, 456L, 789L);

        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(applicationService.getFormInputResponseFileUpload(fileEntryIdExpectations)).thenReturn(serviceSuccess(Pair.of(formInputFileEntryResource, inputStreamSupplier)));

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

    private void assertGetFileButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix, String expectedMessage) throws Exception {
        when(applicationService.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(serviceFailure(new Error(NOT_FOUND_ENTITY, entityTypeNotFound)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isNotFound()).
                andDo(document("forminputresponsefileupload/file_fileDownload_" + documentationSuffix)).
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals(expectedMessage, jsonResponse.getMessage());
    }

    private void assertGetFileDetailsButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix, String expectedMessage) throws Exception {
        when(applicationService.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(serviceFailure(new Error(NOT_FOUND_ENTITY, entityTypeNotFound)));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/fileentry").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isNotFound()).
                andDo(document("forminputresponsefileupload/file_fileEntry_" + documentationSuffix)).
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals(expectedMessage, jsonResponse.getMessage());
    }

    private void assertCreateFileButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix, String expectedMessage) throws Exception {
        assertCreateFileButErrorOccurs(new Error(NOT_FOUND_ENTITY, singletonList(entityTypeNotFound), NOT_FOUND), documentationSuffix, NOT_FOUND, expectedMessage);
    }

    private void assertCreateFileButErrorOccurs(Error errorToReturn, String documentationSuffix, HttpStatus expectedStatus, String expectedMessage) throws Exception {

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> failureResponse = serviceFailure(errorToReturn);

        when(applicationService.createFormInputResponseFileUpload(isA(FormInputResponseFileEntryResource.class), isA(Supplier.class))).thenReturn(failureResponse);

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
        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals(expectedMessage, jsonResponse.getMessage());
    }

    private void assertUpdateFileButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix, String expectedMessage) throws Exception {
        assertUpdateFileButErrorOccurs(new Error(NOT_FOUND_ENTITY, entityTypeNotFound), documentationSuffix, NOT_FOUND, expectedMessage);
    }


    private void assertDeleteFileButEntityNotFound(Class<?> entityTypeNotFound, String documentationSuffix, String expectedMessage) throws Exception {
        assertDeleteFileButErrorOccurs(new Error(NOT_FOUND_ENTITY, entityTypeNotFound), documentationSuffix, NOT_FOUND, expectedMessage);
    }

    private void assertUpdateFileButErrorOccurs(Error errorToReturn, String documentationSuffix, HttpStatus expectedStatus, String expectedMessage) throws Exception {

        ServiceResult<Pair<File, FormInputResponseFileEntryResource>> failureResponse = serviceFailure(errorToReturn);

        when(applicationService.updateFormInputResponseFileUpload(isA(FormInputResponseFileEntryResource.class), isA(Supplier.class))).thenReturn(failureResponse);

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
        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals(expectedMessage, jsonResponse.getMessage());
    }

    private void assertDeleteFileButErrorOccurs(Error errorToReturn, String documentationSuffix, HttpStatus expectedStatus, String expectedMessage) throws Exception {

        ServiceResult<FormInputResponse> failureResponse = serviceFailure(errorToReturn);

        when(applicationService.deleteFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        delete("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andDo(document("forminputresponsefileupload/file_fileDelete_" + documentationSuffix)).
                andReturn();

        assertEquals(expectedStatus.value(), response.getResponse().getStatus());
        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals(expectedMessage, jsonResponse.getMessage());
    }
}
