package org.innovateuk.ifs;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileHeaderAttributes;
import org.junit.Before;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.InputStreamTestUtil.assertInputStreamContents;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseFileControllerMockMVCTest<ControllerType> extends BaseControllerMockMVCTest {

    private InputStream stream = null;

    @Before
    public void resetStreamCache() {
        stream = null;
    }

    // having to "fake" the request body as JSON because Spring Restdocs does not support other content types other
    // than JSON and XML
    private final String dummyFileContent = "{\"description\":\"The request body is the binary content of the file being uploaded - it is NOT JSON as seen here!\"}";

    /**
     * A useful shorthand way to test file upload endpoints that take a file, call a service to create the file on the filesystem,
     * and return the new FileEntryResource as a JSON POST response.  Essentially this method:
     *
     * 1) Constructs a new POST request with some file details on it
     * 2) Sets expectations on a fileValidator validating the file being uploaded
     * 3) Lets the calling code supply a function that sets expectations on the "create file" service method that will be
     *    invoked by the Controller
     * 4) Invokes the POST request
     * 5) Verifies the fileValidator was called as expected
     * 6) Verifies the "create file" service method was called as expected
     */
    protected <T, MediaTypesContext> ResultActions assertFileUploadProcess(
            String url,
            FilesizeAndTypeFileValidator<MediaTypesContext> fileValidator,
            MediaTypesContext mediaTypesContext,
            T serviceToCall,
            BiFunction<T, FileEntryResource, ServiceResult<FileEntryResource>> createFileServiceCall)
            throws Exception {

        return assertFileUploadProcess(url, new Object[] {}, emptyMap(), fileValidator, mediaTypesContext, serviceToCall, createFileServiceCall);
    }

    protected <T, MediaTypesContext> ResultActions assertFileUpdateProcess(
            String url,
            FilesizeAndTypeFileValidator<MediaTypesContext> fileValidator,
            MediaTypesContext mediaTypesContext,
            T serviceToCall,
            BiFunction<T, FileEntryResource, ServiceResult<Void>> createFileServiceCall)
            throws Exception {

        return assertFileUpdateProcess(url, new Object[] {}, emptyMap(), fileValidator, mediaTypesContext, serviceToCall, createFileServiceCall);
    }

    /**
     * A useful shorthand way to test file upload endpoints that take a file, call a service to create the file on the filesystem,
     * and return the new FileEntryResource as a JSON POST response.  Essentially this method:
     *
     * 1) Constructs a new POST request with some file details on it
     * 2) Sets expectations on a fileValidator validating the file being uploaded
     * 3) Lets the calling code supply a function that sets expectations on the "create file" service method that will be
     *    invoked by the Controller
     * 4) Invokes the POST request
     * 5) Verifies the fileValidator was called as expected
     * 6) Verifies the "create file" service method was called as expected
     */
    protected <T, MediaTypesContext> ResultActions assertFileUpdateProcess(
            String url,
            Object[] urlParams,
            Map<String, String> requestParams,
            FilesizeAndTypeFileValidator<MediaTypesContext> fileValidator,
            MediaTypesContext mediaTypesContext,
            T serviceToCall,
            BiFunction<T, FileEntryResource, ServiceResult<Void>> updateFileServiceCall) throws Exception {

        FileEntryResource expectedTemporaryFile = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(dummyFileContent.length()).
                withName("filename.txt").
                withMediaType("text/plain").
                build();

        FileHeaderAttributes fileAttributes = new FileHeaderAttributes(TEXT_PLAIN, dummyFileContent.length(), "filename.txt");
        when(fileValidator.validateFileHeaders("text/plain", dummyFileContent.length() + "", "filename.txt", mediaTypesContext, 1234L)).thenReturn(serviceSuccess(fileAttributes));

        when(updateFileServiceCall.apply(serviceToCall, expectedTemporaryFile)).
                thenReturn(serviceSuccess());

        MockHttpServletRequestBuilder mainRequest = put(url, urlParams).
                content(dummyFileContent).
                param("filename", "filename.txt").
                header("Content-Type", "text/plain").
                header("Content-Length", dummyFileContent.length() + "").
                header("IFS_AUTH_TOKEN", "123abc");

        requestParams.forEach(mainRequest::param);

        ResultActions resultActions = mockMvc.perform(mainRequest).
                andExpect(content().string("")).
                andExpect(status().isOk());

        verify(fileValidator).validateFileHeaders("text/plain", dummyFileContent.length() + "", "filename.txt", mediaTypesContext, 1234L);
        updateFileServiceCall.apply(verify(serviceToCall), expectedTemporaryFile);

        return resultActions;
    }

    /**
     * A useful shorthand way to test file upload endpoints that take a file, call a service to create the file on the filesystem,
     * and return the new FileEntryResource as a JSON POST response.  Essentially this method:
     *
     * 1) Constructs a new POST request with some file details on it
     * 2) Sets expectations on a fileValidator validating the file being uploaded
     * 3) Lets the calling code supply a function that sets expectations on the "create file" service method that will be
     *    invoked by the Controller
     * 4) Invokes the POST request
     * 5) Verifies the fileValidator was called as expected
     * 6) Verifies the "create file" service method was called as expected
     */
    protected <T, MediaTypesContext> ResultActions assertFileUploadProcess(
            String url,
            Object[] urlParams,
            Map<String, String> requestParams,
            FilesizeAndTypeFileValidator<MediaTypesContext> fileValidator,
            MediaTypesContext mediaTypesContext,
            T serviceToCall,
            BiFunction<T, FileEntryResource, ServiceResult<FileEntryResource>> createFileServiceCall) throws Exception {

        FileEntryResource expectedTemporaryFile = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(dummyFileContent.length()).
                withName("filename.txt").
                withMediaType("text/plain").
                build();

        FileHeaderAttributes fileAttributes = new FileHeaderAttributes(TEXT_PLAIN, dummyFileContent.length(), "filename.txt");
        when(fileValidator.validateFileHeaders("text/plain", dummyFileContent.length() + "", "filename.txt", mediaTypesContext, 1234L)).thenReturn(serviceSuccess(fileAttributes));

        FileEntryResource savedFile = newFileEntryResource().
                with(id(456L)).
                withFilesizeBytes(dummyFileContent.length()).
                withName("filename.txt").
                withMediaType("text/plain").
                build();

        when(createFileServiceCall.apply(serviceToCall, expectedTemporaryFile)).
                thenReturn(serviceSuccess(savedFile));

        MockHttpServletRequestBuilder mainRequest = post(url, urlParams).
                content(dummyFileContent).
                param("filename", "filename.txt").
                header("Content-Type", "text/plain").
                header("Content-Length", dummyFileContent.length() + "").
                header("IFS_AUTH_TOKEN", "123abc");

        requestParams.forEach(mainRequest::param);

        ResultActions resultActions = mockMvc.perform(mainRequest).
                andExpect(content().json(toJson(savedFile))).
                andExpect(status().isCreated());

        verify(fileValidator).validateFileHeaders("text/plain", dummyFileContent.length() + "", "filename.txt", mediaTypesContext, 1234L);
        createFileServiceCall.apply(verify(serviceToCall), expectedTemporaryFile);

        return resultActions;
    }

    /**
     * A useful shorthand way of testing a Controller method for simple getting of a file's details (a FileEntryResource)
     */
    protected <T> ResultActions assertGetFileDetails(String url, Object[] urlParams, Map<String, String> requestParams, T serviceToCall, Function<T, ServiceResult<FileEntryResource>> getFileFn) throws Exception {

        FileEntryResource fileToReturn = newFileEntryResource().
                with(id(456L)).
                withFilesizeBytes(dummyFileContent.length()).
                withName("filename.txt").
                withMediaType("text/plain").
                build();

        when(getFileFn.apply(serviceToCall)).thenReturn(serviceSuccess(fileToReturn));

        MockHttpServletRequestBuilder mainRequest = get(url, urlParams).
                header("IFS_AUTH_TOKEN", "123abc");
        requestParams.forEach(mainRequest::param);

        ResultActions resultActions = mockMvc.perform(mainRequest).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(fileToReturn)));

        getFileFn.apply(verify(serviceToCall));

        return resultActions;
    }

    /**
     * A useful shorthand way of testing a Controller method for simple deleting of a file
     */
    protected <T> ResultActions assertDeleteFile(String url, Object[] urlParams, Map<String, String> requestParams, T serviceToCall, Function<T, ServiceResult<Void>> deleteFileFn) throws Exception {

        when(deleteFileFn.apply(serviceToCall)).thenReturn(serviceSuccess());

        MockHttpServletRequestBuilder mainRequest = delete(url, urlParams).
                header("IFS_AUTH_TOKEN", "123abc");

        requestParams.forEach(mainRequest::param);

        ResultActions resultActions = mockMvc.perform(mainRequest).
                andExpect(status().isNoContent()).
                andExpect(content().string(""));

        deleteFileFn.apply(verify(serviceToCall));

        return resultActions;
    }

    /**
     * A useful shorthand way of testing a Controller method for simple getting of a file's contents (binary data)
     */
    protected <T> ResultActions assertGetFileContents(String url, Object[] urlParams, Map<String, String> requestParams, T serviceToCall, Function<T, ServiceResult<FileAndContents>> getFileFn) throws Exception {

        FileEntryResource expectedFileEntryResource = newFileEntryResource().build();

        Supplier<InputStream> inputStreamSupplier = () -> new ByteArrayInputStream("The returned binary file data".getBytes());

        FileAndContents getResult = new BasicFileAndContents(expectedFileEntryResource, inputStreamSupplier);
        when(getFileFn.apply(serviceToCall)).thenReturn(serviceSuccess(getResult));

        MockHttpServletRequestBuilder mainRequest = get(url, urlParams).
                header("IFS_AUTH_TOKEN", "123abc");
        requestParams.forEach(mainRequest::param);

        ResultActions resultActions = mockMvc.perform(mainRequest).
                andExpect(status().isOk());

        MvcResult result = resultActions.andReturn();

        assertEquals("The returned binary file data", result.getResponse().getContentAsString());

        return resultActions;
    }

    protected Supplier<InputStream> fileUploadInputStreamExpectations(String expectedContent) {
        return createLambdaMatcher(is -> {
            if (stream == null) {
                stream = is.get();
            }
            assertInputStreamContents(stream, expectedContent);
        });
    }

    protected Supplier<InputStream> fileUploadInputStreamExpectations() {
        return fileUploadInputStreamExpectations(dummyFileContent);
    }
}
