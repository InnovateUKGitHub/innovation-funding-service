package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceSuccess;
import com.worth.ifs.util.Either;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;

import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.InputStreamTestUtil.assertInputStreamContents;
import static com.worth.ifs.LambdaMatcher.lambdaMatches;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.transactional.ServiceFailure.error;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        FormInputResponseFileEntryResource resourceExpectations = argThat(lambdaMatches(resource -> {
            assertEquals(123L, resource.getCompoundId().getFormInputId());
            assertEquals(456L, resource.getCompoundId().getApplicationId());
            assertEquals(789L, resource.getCompoundId().getProcessRoleId());

            assertNull(resource.getFileEntryResource().getId());
            assertEquals(1000, resource.getFileEntryResource().getFilesizeBytes());
            assertEquals(MediaType.parseMediaType("application/pdf"), resource.getFileEntryResource().getMediaType());
            assertEquals("original.pdf", resource.getFileEntryResource().getName());
            return true;
        }));

        Supplier<InputStream> inputStreamExpectations = argThat(lambdaMatches(inputStreamSupplier ->
                assertInputStreamContents(inputStreamSupplier.get(), "My PDF content")));

        Either<ServiceFailure, ServiceSuccess<Pair<File, FormInputResponseFileEntryResource>>> successResponse =
                right(new ServiceSuccess(Pair.of(new File(""), new FormInputResponseFileEntryResource(newFileEntryResource().with(id(1111L)).build(), 123L, 456L, 789L))));

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
                                content("My PDF content")).
                andExpect(status().isOk()).
                andReturn();

        String content = response.getResponse().getContentAsString();
        FormInputResponseFileEntryJsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, FormInputResponseFileEntryJsonStatusResponse.class);
        assertEquals(1111L, jsonResponse.getFileEntryId());
    }

    @Test
    public void testCreateFileButApplicationServiceCallFails() throws Exception {

        Either<ServiceFailure, ServiceSuccess<Pair<File, FormInputResponseFileEntryResource>>> failureResponse =
                left(error("No files today!"));

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
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Please supply a valid Content-Type HTTP header.  Valid types are application/pdf, application/json", jsonResponse.getMessage());
    }

    @Test
    public void testGetFile() throws Exception {

        FormInputResponseFileEntryId fileEntryIdExpectations = argThat(lambdaMatches(fileEntryId -> {
            assertEquals(123L, fileEntryId.getFormInputId());
            assertEquals(456L, fileEntryId.getApplicationId());
            assertEquals(789L, fileEntryId.getProcessRoleId());
            return true;
        }));

        FormInputResponseFileEntryResource fileEntryResource = new FormInputResponseFileEntryResource(newFileEntryResource().build(), 123L, 456L, 789L);
        Supplier<InputStream> inputStreamSupplier = () -> new ByteArrayInputStream("The returned InputStream".getBytes());

        when(applicationService.getFormInputResponseFileUpload(fileEntryIdExpectations)).thenReturn(right(new ServiceSuccess(Pair.of(fileEntryResource, inputStreamSupplier))));

        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/file").
                                param("formInputId", "123").
                                param("applicationId", "456").
                                param("processRoleId", "789")).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("The returned InputStream", response.getResponse().getContentAsString());
    }

    @Test
    public void testGetFileButApplicationServiceCallFails() throws Exception {

        when(applicationService.getFormInputResponseFileUpload(isA(FormInputResponseFileEntryId.class))).thenReturn(left(error("No files today!")));

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
    public void testGetFileButApplicationServiceCallThrowsException() throws Exception {

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
}
