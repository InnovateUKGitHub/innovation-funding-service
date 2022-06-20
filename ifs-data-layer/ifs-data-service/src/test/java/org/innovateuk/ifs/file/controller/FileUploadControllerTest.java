package org.innovateuk.ifs.file.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileUploadService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.InputStreamTestUtil.assertInputStreamContents;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.*;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_INCORRECTLY_REPORTED_FILESIZE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_INCORRECTLY_REPORTED_MEDIA_TYPE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FileUploadControllerTest extends BaseControllerMockMVCTest<FileUploadController> {

    private static final Error generalError = new Error("No files today!", INTERNAL_SERVER_ERROR);

    private static final long maxFileSize = 1234L;

    @Mock
    private FileUploadService fileUploadServiceMock;

    @Mock(name = "mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidatorMock;

    private InputStream stream = null;

    @Override
    protected FileUploadController supplyControllerUnderTest() {
        FileUploadController controller = new FileUploadController();
        ReflectionTestUtils.setField(controller, "maxFilesizeBytesForUpload", maxFileSize);
        return controller;
    }

    @Test
    public void uploadFile() throws Exception {
        // having to "fake" the request body as JSON because Spring Restdocs does not support content types other
        // than JSON and XML
        String dummyContent = "{\"description\":\"The request body is the binary content of the file being uploaded - it is NOT JSON as seen here!\"}";
        FileHeaderAttributes fileAttributesAfterValidation = new FileHeaderAttributes(MediaType.valueOf("application/csv"), 1000L, "original.csv");

        FileEntryResource createdResource = newFileEntryResource()
                .with(id(1111L))
                .build();
        ServiceResult<FileEntryResource> successResponse = serviceSuccess(createdResource);

        when(fileValidatorMock.validateFileHeaders("application/csv", "1000", "original.csv", null, 1234L))
                .thenReturn(serviceSuccess(fileAttributesAfterValidation));
        when(fileUploadServiceMock.uploadFile(eq("AssessmentOnly"), createFileEntryResourceExpectations("original.csv"), createInputStreamExpectations(dummyContent))).thenReturn(successResponse);

        MvcResult response = mockMvc.
                perform(
                        post("/external-system-files/upload-file").
                                param("fileType", "AssessmentOnly").
                                param("fileName", "original.csv").
                                header("Content-Type", "application/csv").
                                header("Content-Length", "1000").
                                content(dummyContent)
                ).
                andExpect(status().isCreated())
        .andReturn();
    }

    private FileEntryResource createFileEntryResourceExpectations(String expectedFilename) {
        return createLambdaMatcher(resource -> {
            assertNull(resource.getId());
            assertEquals(1000, resource.getFilesizeBytes());
            assertEquals("application/csv", resource.getMediaType());
            assertEquals(expectedFilename, resource.getName());
        });
    }

    private Supplier<InputStream> createInputStreamExpectations(String dummyContent) {
        return createLambdaMatcher(is -> {
            if (stream == null) {
                stream = is.get();
            }
            assertInputStreamContents(stream, dummyContent);
        });
    }

    @Test
    public void uploadFileButContentLengthHeaderTooLarge() throws Exception {

        when(fileValidatorMock.validateFileHeaders("application/csv", "99999999", "original.csv", null, maxFileSize))
                .thenReturn(serviceFailure(payloadTooLargeError(5000)));

        MvcResult response = mockMvc.
                perform(
                        post("/external-system-files/upload-file").
                                param("fileType", "AssessmentOnly").
                                param("fileName", "original.csv").
                                header("Content-Type", "application/csv").
                                header("Content-Length", "99999999").
                                content("My CSV content")).
                andExpect(status().isPayloadTooLarge()).
                andReturn();

        assertResponseErrorKeyEqual(PAYLOAD_TOO_LARGE.name(), payloadTooLargeError(5000), response);
    }

    @Test
    public void uploadFileButContentLengthHeaderMissing() throws Exception {

        // Spring updates causes the contentLengthValue to be set even if not specified on post.
        when(fileValidatorMock.validateFileHeaders("application/csv", "14", "original.csv", null, maxFileSize))
                .thenReturn(serviceFailure(lengthRequiredError(5000L)));

        MvcResult response = mockMvc.
                perform(
                        post("/external-system-files/upload-file").
                                param("fileType", "AssessmentOnly").
                                param("fileName", "original.csv").
                                header("Content-Type", "application/csv").
                                content("My CSV content")).
                andExpect(status().isLengthRequired()).
                andReturn();

        assertResponseErrorKeyEqual(LENGTH_REQUIRED.name(), lengthRequiredError(5000), response);
    }

    @Test
    public void uploadFileButContentTypeHeaderInvalid() throws Exception {

        when(fileValidatorMock.validateFileHeaders("text/plain", "1000", "original.csv", null, maxFileSize))
                .thenReturn(serviceFailure(unsupportedMediaTypeByNameError(asList("application/csv", "application/json"))));

        MvcResult response = mockMvc.
                perform(
                        post("/external-system-files/upload-file").
                                param("fileType", "AssessmentOnly").
                                param("fileName", "original.csv").
                                header("Content-Type", "text/plain").
                                header("Content-Length", "1000").
                                content("My CSV content")).
                andExpect(status().isUnsupportedMediaType()).
                andReturn();

        assertResponseErrorKeyEqual(UNSUPPORTED_MEDIA_TYPE.name(), unsupportedMediaTypeByNameError(asList("application/csv", "application/json")), response);
    }

    @Test
    public void uploadFileButContentTypeHeaderMissing() throws Exception {

        when(fileValidatorMock.validateFileHeaders(null, "1000", "original.csv", null, maxFileSize))
                .thenReturn(serviceFailure(unsupportedMediaTypeByNameError(asList("application/pdf", "application/json"))));

        MvcResult response = mockMvc.
                perform(
                        post("/external-system-files/upload-file").
                                param("fileType", "AssessmentOnly").
                                param("fileName", "original.csv").
                                header("Content-Length", "1000").
                                content("My CSV content")).
                andExpect(status().isUnsupportedMediaType()).
                andReturn();

        assertResponseErrorKeyEqual(UNSUPPORTED_MEDIA_TYPE.name(), unsupportedMediaTypeByNameError(asList("application/pdf", "application/json")), response);
    }

    @Test
    public void uploadFileButFileUploadServiceCallFails() throws Exception {
        assertUploadFileButErrorOccurs(generalError, "internalServerError", INTERNAL_SERVER_ERROR);
    }

    @Test
    public void uploadFileButContentLengthHeaderMisreported() throws Exception {
        assertUploadFileButErrorOccurs(new Error(FILES_INCORRECTLY_REPORTED_FILESIZE), "incorrectlyReportedContentLength", BAD_REQUEST);
    }

    @Test
    public void uploadFileButContentTypeHeaderMisreported() throws Exception {
        assertUploadFileButErrorOccurs(new Error(FILES_INCORRECTLY_REPORTED_MEDIA_TYPE), "incorrectlyReportedContentType", UNSUPPORTED_MEDIA_TYPE);
    }

    private void assertUploadFileButErrorOccurs(Error errorToReturn, String documentationSuffix, HttpStatus expectedStatus) throws Exception {

        ServiceResult<FileEntryResource> failureResponse = serviceFailure(errorToReturn);

        when(fileValidatorMock.validateFileHeaders(isA(String.class), isA(String.class), isA(String.class), isNull(), isA(Long.class)))
                .thenReturn(serviceSuccess(new FileHeaderAttributes(MediaType.valueOf("application/csv"), 1000, "original.csv")));
        when(fileUploadServiceMock.uploadFile(isA(String.class), isA(FileEntryResource.class), isSupplierMatcher())).thenReturn(failureResponse);

        MvcResult response = mockMvc.
                perform(
                        post("/external-system-files/upload-file").
                                param("fileType", "AssessmentOnly").
                                param("fileName", "original.csv").
                                header("Content-Type", "application/csv").
                                header("Content-Length", "1000").
                                content("My CSV content")).
                andReturn();

        assertEquals(expectedStatus.value(), response.getResponse().getStatus());
        assertResponseErrorKeyEqual(errorToReturn.getErrorKey(), errorToReturn, response);
    }

    private <T> Supplier<T> isSupplierMatcher() {
        return isA(Supplier.class);
    }
}
