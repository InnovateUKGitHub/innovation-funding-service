package org.innovateuk.ifs.file.controller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileHeaderAttributes;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_EXCEPTION_WHILE_RETRIEVING_FILE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

/**
 * Helpful utility methods for dealing with file uploads within Controllers
 */
public class FileControllerUtils {

    private static final Log LOG = LogFactory.getLog(FileControllerUtils.class);

    /**
     * A convenience method to create a response to a file download request, given a supplier of a FileAndContents
     */
    public ResponseEntity<Object> handleFileDownload(Supplier<ServiceResult<? extends FileAndContents>> fileResultSupplier) {

        try {

            ServiceResult<? extends FileAndContents> getFileResult = fileResultSupplier.get();

            return getFileResult.handleSuccessOrFailure(
                    failure -> {
                        RestErrorResponse errorResponse = new RestErrorResponse(failure.getErrors());
                        return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
                    },
                    fileResult -> {
                        FileEntryResource fileEntry = fileResult.getFileEntry();
                        Supplier<InputStream> inputStreamSupplier = fileResult.getContentsSupplier();
                        InputStream inputStream = inputStreamSupplier.get();
                        ByteArrayResource inputStreamResource = new ByteArrayResource(StreamUtils.copyToByteArray(inputStream));
                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.setContentLength(fileEntry.getFilesizeBytes());
                        httpHeaders.setContentType(MediaType.parseMediaType(fileEntry.getMediaType()));
                        return new ResponseEntity<>(inputStreamResource, httpHeaders, OK);
                    }
            );

        } catch (Exception e) {

            LOG.error("Error retrieving file", e);
            return new ResponseEntity<>(new RestErrorResponse(new Error(FILES_EXCEPTION_WHILE_RETRIEVING_FILE)), INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * A convenience method to process a file upload request given a standard pattern of header validation and processing,
     * given a function that can perform the actual file upload.
     * <p>
     * The {@link MediaTypesContext} generic type refers to a context from which a valid set of Media Types can be established,
     * as used by the supplied {@link FilesizeAndTypeFileValidator}.
     */
    public <T, MediaTypesContext> RestResult<T> handleFileUpload(String contentType, String contentLength, String originalFilename,
                                                                 FilesizeAndTypeFileValidator<MediaTypesContext> fileValidator, MediaTypesContext mediaTypeContext, long maxFileSizeBytes,
                                                                 HttpServletRequest request, BiFunction<FileHeaderAttributes, Supplier<InputStream>, ServiceResult<T>> uploadFileActionFn) {

        return handleFileUploadWithServiceCall(contentType, contentLength, originalFilename, fileValidator, mediaTypeContext, maxFileSizeBytes, request, uploadFileActionFn).toPostCreateResponse();
    }

    /**
     * A convenience method to process a file upload (as an update) request given a standard pattern of header
     * validation and processing, given a function that can perform the actual file upload
     */
    public <MediaTypesContext> RestResult<Void> handleFileUpdate(String contentType, String contentLength, String originalFilename,
                                                                 FilesizeAndTypeFileValidator<MediaTypesContext> fileValidator, MediaTypesContext mediaTypesContext, long maxFileSizeBytes,
                                                                 HttpServletRequest request, BiFunction<FileHeaderAttributes, Supplier<InputStream>, ServiceResult<?>> uploadFileActionFn) {

        BiFunction<FileHeaderAttributes, Supplier<InputStream>, ServiceResult<Void>> voidReturner = (fileAttributes, inputStreamSupplier) ->
                uploadFileActionFn.apply(fileAttributes, inputStreamSupplier).andOnSuccessReturnVoid();

        return handleFileUploadWithServiceCall(contentType, contentLength, originalFilename, fileValidator, mediaTypesContext, maxFileSizeBytes, request, voidReturner).toPutResponse();
    }

    private static <T, MediaTypesContext> ServiceResult<T> handleFileUploadWithServiceCall(String contentType, String contentLength, String originalFilename, FilesizeAndTypeFileValidator<MediaTypesContext> fileValidator, MediaTypesContext mediaTypesContext, long maxFileSizeBytes, HttpServletRequest request, BiFunction<FileHeaderAttributes, Supplier<InputStream>, ServiceResult<T>> uploadFileActionFn) {

        return fileValidator.validateFileHeaders(contentType, contentLength, originalFilename, mediaTypesContext, maxFileSizeBytes).handleSuccessOrFailure(
                failure -> failureView(request, failure),
                success -> uploadFileActionFn.apply(success, inputStreamSupplier(request))
        );
    }

    private static Supplier<InputStream> inputStreamSupplier(HttpServletRequest request) {
        return () -> inputStream(request);
    }

    private static InputStream inputStream(HttpServletRequest request) {
            try {
                byte[] array = IOUtils.toByteArray(request.getInputStream());
                return new ByteArrayInputStream(array);
            } catch (IOException e) {
                LOG.error("Unable to open an input stream from request", e);
                throw new RuntimeException("Unable to open an input stream from request", e);
            }
    }

    private static <T> ServiceResult<T> failureView(HttpServletRequest request, ServiceFailure result) {
        // Must be called on failure to wait for upload to finish otherwise we get a connection reset error
        inputStream(request);
        return serviceFailure(result.getErrors());
    }
}
