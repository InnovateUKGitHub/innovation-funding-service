package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceSuccess;
import com.worth.ifs.util.Either;
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
import static com.worth.ifs.util.Either.right;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.argThat;
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
        controller.setValidMediaTypes(asList("application/pdf"));
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
}
