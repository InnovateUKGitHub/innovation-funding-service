package org.innovateuk.ifs;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileHeaderAttributes;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
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
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseFileControllerMockMVCTest<ControllerType> extends BaseControllerMockMVCTest {

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

    /**
     * A useful shorthand way of documenting a Controller method for simple uploading of a file (binary data and metadata)
     */
    protected RestDocumentationResultHandler documentFileUploadMethod(String docPath) {
        return documentFileUploadMethod(docPath, emptyList(), emptyList());
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple uploading of a file (binary data and metadata)
     */
    protected RestDocumentationResultHandler documentFileUploadMethod(String docPath,
                                                                      List<Pair<String, String>> additionalRequestParameters,
                                                                      List<Pair<String, String>> additionalHeaders) {

        List<ParameterDescriptor> requestParameters = new ArrayList<>();
        requestParameters.add(parameterWithName("filename").description("The filename of the file being uploaded"));
        additionalRequestParameters.forEach(nvp -> requestParameters.add(parameterWithName(nvp.getKey()).description(nvp.getValue())));

        List<HeaderDescriptor> headers = new ArrayList<>();
        headers.add(headerWithName("Content-Type").description("The Content Type of the file being uploaded e.g. application/pdf"));
        headers.add(headerWithName("Content-Length").description("The Content Length of the binary file data being uploaded in bytes"));
        headers.add(headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user"));
        additionalHeaders.forEach(nvp -> headers.add(headerWithName(nvp.getKey()).description(nvp.getValue())));

        return document(docPath,
                requestParameters(
                        requestParameters.toArray(new ParameterDescriptor[requestParameters.size()])
                ),
                requestHeaders(
                        additionalHeaders.toArray(new HeaderDescriptor[additionalHeaders.size()])
                ),
                requestFields(fieldWithPath("description").description("The body of the request should be the binary data of the file being uploaded (and NOT JSON as shown in example)")),
                responseFields(
                        fieldWithPath("id").description("Id of the FileEntry that was created"),
                        fieldWithPath("name").description("Name of the FileEntry that was created"),
                        fieldWithPath("mediaType").description("Media type of the FileEntry that was created"),
                        fieldWithPath("filesizeBytes").description("File size in bytes of the FileEntry that was created")
                ));
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple updating of a file (binary data and metadata)
     */
    protected RestDocumentationResultHandler documentFileUpdateMethod(String docPath) {
        return documentFileUpdateMethod(docPath, emptyList(), emptyList());
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple updating of a file (binary data and metadata)
     */
    protected RestDocumentationResultHandler documentFileUpdateMethod(String docPath,
                                                                      List<Pair<String, String>> additionalRequestParameters,
                                                                      List<Pair<String, String>> additionalHeaders) {

        List<ParameterDescriptor> requestParameters = new ArrayList<>();
        requestParameters.add(parameterWithName("filename").description("The filename of the file being uploaded"));
        additionalRequestParameters.forEach(nvp -> requestParameters.add(parameterWithName(nvp.getKey()).description(nvp.getValue())));

        List<HeaderDescriptor> headers = new ArrayList<>();
        headers.add(headerWithName("Content-Type").description("The Content Type of the file being uploaded e.g. application/pdf"));
        headers.add(headerWithName("Content-Length").description("The Content Length of the binary file data being uploaded in bytes"));
        headers.add(headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user"));
        additionalHeaders.forEach(nvp -> headers.add(headerWithName(nvp.getKey()).description(nvp.getValue())));

        return document(docPath,
                requestParameters(
                        requestParameters.toArray(new ParameterDescriptor[requestParameters.size()])
                ),
                requestHeaders(
                        additionalHeaders.toArray(new HeaderDescriptor[additionalHeaders.size()])
                ),
                requestFields(fieldWithPath("description").description("The body of the request should be the binary data of the file being uploaded (and NOT JSON as shown in example)")));
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple deleting of a file
     */
    protected RestDocumentationResultHandler documentFileDeleteMethod(String docPath) {
        return documentFileDeleteMethod(docPath, emptyList(), emptyList());
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple deleting of a file
     */
    protected RestDocumentationResultHandler documentFileDeleteMethod(String docPath,
                                                                      List<Pair<String, String>> additionalRequestParameters,
                                                                      List<Pair<String, String>> additionalHeaders) {

        return documentFileMethodWithEmptyResponseBody(docPath, additionalRequestParameters, additionalHeaders);
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple getting of a file's details (a FileEntryResource)
     */
    protected RestDocumentationResultHandler documentFileGetDetailsMethod(String docPath) {
        return documentFileGetDetailsMethod(docPath, emptyList(), emptyList());
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple getting of a file's details (a FileEntryResource)
     */
    protected RestDocumentationResultHandler documentFileGetDetailsMethod(String docPath,
                                                                          List<Pair<String, String>> additionalRequestParameters,
                                                                          List<Pair<String, String>> additionalHeaders) {

        List<ParameterDescriptor> requestParameters = new ArrayList<>();
        additionalRequestParameters.forEach(nvp -> requestParameters.add(parameterWithName(nvp.getKey()).description(nvp.getValue())));

        List<HeaderDescriptor> headers = new ArrayList<>();
        headers.add(headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user"));
        additionalHeaders.forEach(nvp -> headers.add(headerWithName(nvp.getKey()).description(nvp.getValue())));

        return document(docPath,
                requestParameters(
                        requestParameters.toArray(new ParameterDescriptor[requestParameters.size()])
                ),
                requestHeaders(
                        additionalHeaders.toArray(new HeaderDescriptor[additionalHeaders.size()])
                ),
                responseFields(
                        fieldWithPath("id").description("Id of the FileEntry that was looked up"),
                        fieldWithPath("name").description("Name of the FileEntry that was looked up"),
                        fieldWithPath("mediaType").description("Media type of the FileEntry that was looked up"),
                        fieldWithPath("filesizeBytes").description("File size in bytes of the FileEntry that was looked up")
                ));
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple getting of a file's contents (binary data)
     */
    protected RestDocumentationResultHandler documentFileGetContentsMethod(String docPath) {
        return documentFileGetContentsMethod(docPath, emptyList(), emptyList());
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple getting of a file's contents (binary data)
     */
    protected RestDocumentationResultHandler documentFileGetContentsMethod(String docPath,
                                                                           List<Pair<String, String>> additionalRequestParameters,
                                                                           List<Pair<String, String>> additionalHeaders) {

        return documentFileMethodWithEmptyResponseBody(docPath, additionalRequestParameters, additionalHeaders);
    }

    private RestDocumentationResultHandler documentFileMethodWithEmptyResponseBody(String docPath, List<Pair<String, String>> additionalRequestParameters, List<Pair<String, String>> additionalHeaders) {

        List<ParameterDescriptor> requestParameters = new ArrayList<>();
        additionalRequestParameters.forEach(nvp -> requestParameters.add(parameterWithName(nvp.getKey()).description(nvp.getValue())));

        List<HeaderDescriptor> headers = new ArrayList<>();
        headers.add(headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user"));
        additionalHeaders.forEach(nvp -> headers.add(headerWithName(nvp.getKey()).description(nvp.getValue())));

        return document(docPath,
                requestParameters(
                        requestParameters.toArray(new ParameterDescriptor[requestParameters.size()])
                ),
                requestHeaders(
                        additionalHeaders.toArray(new HeaderDescriptor[additionalHeaders.size()])
                ));
    }

    protected Supplier<InputStream> fileUploadInputStreamExpectations(String expectedContent) {
        return createLambdaMatcher(is -> {
            assertInputStreamContents(is.get(), expectedContent);
        });
    }

    protected Supplier<InputStream> fileUploadInputStreamExpectations() {
        return fileUploadInputStreamExpectations(dummyFileContent);
    }
}
