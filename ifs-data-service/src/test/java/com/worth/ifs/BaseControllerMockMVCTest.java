package com.worth.ifs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.BasicFileAndContents;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.file.transactional.FileHeaderAttributes;
import com.worth.ifs.rest.ErrorControllerAdvice;
import com.worth.ifs.rest.RestResultHandlingHttpMessageConverter;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.InjectMocks;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.InputStreamTestUtil.assertInputStreamContents;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This is the base class for testing Controllers using MockMVC in addition to standard Mockito mocks.  Using MockMVC
 * allows Controllers to be tested via their routes and their responses' HTTP responses tested also.
 *
 * Created by dwatson on 02/10/15.
 */
public abstract class BaseControllerMockMVCTest<ControllerType> extends BaseUnitTestMocksTest {

    @InjectMocks
    protected ControllerType controller = supplyControllerUnderTest();

    protected MockMvc mockMvc;

    protected abstract ControllerType supplyControllerUnderTest();

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/generated-snippets");// having to "fake" the request body as JSON because Spring Restdocs does not support other content types other

    // than JSON and XML
    private final String dummyFileContent = "{\"description\":\"The request body is the binary content of the file being uploaded - it is NOT JSON as seen here!\"}";

    @Before
    public void setupMockMvc() {

        MockMvc originalMvc = MockMvcBuilders.standaloneSetup(controller).build();

        //
        // We need to register custom MessageConverters with MockMVC manually.  Unfortunately there's no way to simply add a new
        // one to the custom set of MessageConverters that comes out of the box.  Therefore we need to get the original set as
        // a list, add our custom one to the list, and then set this list as the full set of MessageConverters
        //
        List<HandlerAdapter> defaultHandlerAdapters = (List<HandlerAdapter>) getField(getField(originalMvc, "servlet"), "handlerAdapters");
        RequestMappingHandlerAdapter requestMappingHandlerAdapter = (RequestMappingHandlerAdapter) defaultHandlerAdapters.stream().filter(handler -> handler instanceof RequestMappingHandlerAdapter).collect(toList()).get(0);
        List<HttpMessageConverter<?>> originalMessageConverters = requestMappingHandlerAdapter.getMessageConverters();
        List<HttpMessageConverter<?>> nonMappingJackson2Converters = originalMessageConverters.stream().filter(converter -> !(converter instanceof MappingJackson2HttpMessageConverter)).collect(toList());

        RestResultHandlingHttpMessageConverter customHttpMessageConverter = new RestResultHandlingHttpMessageConverter();
        List<HttpMessageConverter<?>> newListOfConverters = combineLists(nonMappingJackson2Converters, customHttpMessageConverter);
        HttpMessageConverter[] newListOfConvertersArray = newListOfConverters.toArray(new HttpMessageConverter[newListOfConverters.size()]);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(newListOfConvertersArray)
                .setControllerAdvice(new ErrorControllerAdvice())
                .apply(documentationConfiguration(this.restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8090))
                .build();
    }

    private static ResultMatcher contentObject(final Object json) throws JsonProcessingException {
        return content().string(new ObjectMapper().writeValueAsString(json));
    }

    protected static ResultMatcher contentError(final Error error) throws JsonProcessingException {
        return contentObject(new RestErrorResponse(error));
    }

    protected void assertResponseErrorKeyEqual(String expectedKey, Error expectedError, MvcResult mvcResult) throws IOException {
        String content = mvcResult.getResponse().getContentAsString();
        RestErrorResponse restErrorResponse = new ObjectMapper().readValue(content, RestErrorResponse.class);
        assertErrorKeyEqual(expectedKey, expectedError, restErrorResponse);
    }

    private void assertErrorKeyEqual(String expectedErrorKey, Error expectedError, RestErrorResponse restErrorResponse) {
        assertEquals(expectedErrorKey, restErrorResponse.getErrors().get(0).getErrorKey());
        assertEqualsUpNoIncludingStatusCode(expectedErrorKey, restErrorResponse, expectedError);
    }

    protected void assertEqualsUpNoIncludingStatusCode(final RestErrorResponse restErrorResponse, final Error expectedError){
        assertEqualsUpNoIncludingStatusCode(restErrorResponse.getErrors().get(0).getErrorKey(), restErrorResponse, expectedError);
    }

    protected void assertEqualsUpNoIncludingStatusCode(String expectedErrorKey, final RestErrorResponse restErrorResponse, final Error expectedError){
        assertTrue(restErrorResponse.getErrors().size() == 1);
        assertEquals(restErrorResponse.getErrors().get(0).getArguments() , expectedError.getArguments());
        assertEquals(expectedErrorKey , expectedError.getErrorKey());
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
    protected <T> ResultActions assertFileUploadProcess(
            String url,
            T serviceToCall,
            BiFunction<T, FileEntryResource, ServiceResult<FileEntryResource>> createFileServiceCall)
            throws Exception {

        return assertFileUploadProcess(url, new Object[] {}, emptyMap(), serviceToCall, createFileServiceCall);
    }

    protected <T> ResultActions assertFileUpdateProcess(
            String url,
            T serviceToCall,
            BiFunction<T, FileEntryResource, ServiceResult<Void>> createFileServiceCall)
            throws Exception {

        return assertFileUpdateProcess(url, new Object[] {}, emptyMap(), serviceToCall, createFileServiceCall);
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
    protected <T> ResultActions assertFileUpdateProcess(
            String url,
            Object[] urlParams,
            Map<String, String> requestParams,
            T serviceToCall,
            BiFunction<T, FileEntryResource, ServiceResult<Void>> updateFileServiceCall) throws Exception {



        FileEntryResource expectedTemporaryFile = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(dummyFileContent.length()).
                withName("filename.txt").
                withMediaType("text/plain").
                build();

        FileHeaderAttributes fileAttributes = new FileHeaderAttributes(TEXT_PLAIN, dummyFileContent.length(), "filename.txt");
        when(fileValidatorMock.validateFileHeaders("text/plain", dummyFileContent.length() + "", "filename.txt")).thenReturn(serviceSuccess(fileAttributes));

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

        verify(fileValidatorMock).validateFileHeaders("text/plain", dummyFileContent.length() + "", "filename.txt");
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
    protected <T> ResultActions assertFileUploadProcess(
        String url,
        Object[] urlParams,
        Map<String, String> requestParams,
        T serviceToCall,
        BiFunction<T, FileEntryResource, ServiceResult<FileEntryResource>> createFileServiceCall) throws Exception {

        FileEntryResource expectedTemporaryFile = newFileEntryResource().
                with(id(null)).
                withFilesizeBytes(dummyFileContent.length()).
                withName("filename.txt").
                withMediaType("text/plain").
                build();

        FileHeaderAttributes fileAttributes = new FileHeaderAttributes(TEXT_PLAIN, dummyFileContent.length(), "filename.txt");
        when(fileValidatorMock.validateFileHeaders("text/plain", dummyFileContent.length() + "", "filename.txt")).thenReturn(serviceSuccess(fileAttributes));

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

        verify(fileValidatorMock).validateFileHeaders("text/plain", dummyFileContent.length() + "", "filename.txt");
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
    protected RestDocumentationResultHandler documentFileUploadMethod(RestDocumentationResultHandler document) {
        return documentFileUploadMethod(document, emptyList(), emptyList());
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple uploading of a file (binary data and metadata)
     */
    protected RestDocumentationResultHandler documentFileUploadMethod(RestDocumentationResultHandler document,
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

        return document.snippets(
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
    protected RestDocumentationResultHandler documentFileUpdateMethod(RestDocumentationResultHandler document) {
        return documentFileUpdateMethod(document, emptyList(), emptyList());
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple updating of a file (binary data and metadata)
     */
    protected RestDocumentationResultHandler documentFileUpdateMethod(RestDocumentationResultHandler document,
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

        return document.snippets(
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
    protected RestDocumentationResultHandler documentFileDeleteMethod(RestDocumentationResultHandler document) {
        return documentFileDeleteMethod(document, emptyList(), emptyList());
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple deleting of a file
     */
    protected RestDocumentationResultHandler documentFileDeleteMethod(RestDocumentationResultHandler document,
                                                                    List<Pair<String, String>> additionalRequestParameters,
                                                                    List<Pair<String, String>> additionalHeaders) {

        return documentFileMethodWithEmptyResponseBody(document, additionalRequestParameters, additionalHeaders);
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple getting of a file's details (a FileEntryResource)
     */
    protected RestDocumentationResultHandler documentFileGetDetailsMethod(RestDocumentationResultHandler document) {
        return documentFileGetDetailsMethod(document, emptyList(), emptyList());
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple getting of a file's details (a FileEntryResource)
     */
    protected RestDocumentationResultHandler documentFileGetDetailsMethod(RestDocumentationResultHandler document,
                                                                          List<Pair<String, String>> additionalRequestParameters,
                                                                          List<Pair<String, String>> additionalHeaders) {

        List<ParameterDescriptor> requestParameters = new ArrayList<>();
        additionalRequestParameters.forEach(nvp -> requestParameters.add(parameterWithName(nvp.getKey()).description(nvp.getValue())));

        List<HeaderDescriptor> headers = new ArrayList<>();
        headers.add(headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user"));
        additionalHeaders.forEach(nvp -> headers.add(headerWithName(nvp.getKey()).description(nvp.getValue())));

        return document.snippets(
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
    protected RestDocumentationResultHandler documentFileGetContentsMethod(RestDocumentationResultHandler document) {
        return documentFileGetContentsMethod(document, emptyList(), emptyList());
    }

    /**
     * A useful shorthand way of documenting a Controller method for simple getting of a file's contents (binary data)
     */
    protected RestDocumentationResultHandler documentFileGetContentsMethod(RestDocumentationResultHandler document,
                                                                           List<Pair<String, String>> additionalRequestParameters,
                                                                           List<Pair<String, String>> additionalHeaders) {

        return documentFileMethodWithEmptyResponseBody(document, additionalRequestParameters, additionalHeaders);
    }

    private RestDocumentationResultHandler documentFileMethodWithEmptyResponseBody(RestDocumentationResultHandler document, List<Pair<String, String>> additionalRequestParameters, List<Pair<String, String>> additionalHeaders) {

        List<ParameterDescriptor> requestParameters = new ArrayList<>();
        additionalRequestParameters.forEach(nvp -> requestParameters.add(parameterWithName(nvp.getKey()).description(nvp.getValue())));

        List<HeaderDescriptor> headers = new ArrayList<>();
        headers.add(headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user"));
        additionalHeaders.forEach(nvp -> headers.add(headerWithName(nvp.getKey()).description(nvp.getValue())));

        return document.snippets(
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

    protected static void login(UserResource userResource) {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(userResource));
    }
}