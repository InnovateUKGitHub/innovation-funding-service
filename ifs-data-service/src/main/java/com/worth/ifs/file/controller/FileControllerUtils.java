package com.worth.ifs.file.controller;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.file.transactional.FileHeaderAttributes;
import com.worth.ifs.file.transactional.FileHttpHeadersValidator;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonFailureKeys.FILES_EXCEPTION_WHILE_RETRIEVING_FILE;
import static org.hibernate.jpa.internal.QueryImpl.LOG;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

/**
 * Helpful utility methods for dealing with file uploads within Controllers
 */
public class FileControllerUtils {

    /**
     * A convenience method to create a response to a file download request, given a supplier of a FileAndContents
     */
    public static ResponseEntity<Object> handleFileDownload(Supplier<ServiceResult<? extends FileAndContents>> fileResultSupplier) {

        // TODO DW - INFUND-854 - remove try-catch - possibly handle this ResponseEntity with CustomHttpMessageConverter
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
     * given a function that can perform the actual file upload
     */
    public static <T> RestResult<T> handleFileUpload(String contentType, String contentLength, String originalFilename,
                                                 FileHttpHeadersValidator fileValidator, HttpServletRequest request,
                                                 BiFunction<FileHeaderAttributes, Supplier<InputStream>, ServiceResult<T>> uploadFileActionFn) {

        return handleFileUploadWithServiceCall(contentType, contentLength, originalFilename, fileValidator, request, uploadFileActionFn).toPostCreateResponse();
    }

    /**
     * A convenience method to process a file upload (as an update) request given a standard pattern of header
     * validation and processing, given a function that can perform the actual file upload
     */
    public static RestResult<Void> handleFileUpdate(String contentType, String contentLength, String originalFilename,
                                                                 FileHttpHeadersValidator fileValidator, HttpServletRequest request,
                                                                 BiFunction<FileHeaderAttributes, Supplier<InputStream>, ServiceResult<?>> uploadFileActionFn) {

        BiFunction<FileHeaderAttributes, Supplier<InputStream>, ServiceResult<Void>> voidReturner = (fileAttributes, inputStreamSupplier) ->
            uploadFileActionFn.apply(fileAttributes, inputStreamSupplier).andOnSuccessReturnVoid();

        return handleFileUploadWithServiceCall(contentType, contentLength, originalFilename, fileValidator, request, voidReturner).toPutResponse();
    }

    private static <T> ServiceResult<T> handleFileUploadWithServiceCall(String contentType, String contentLength, String originalFilename, FileHttpHeadersValidator fileValidator, HttpServletRequest request, BiFunction<FileHeaderAttributes, Supplier<InputStream>, ServiceResult<T>> uploadFileActionFn) {
        return fileValidator.validateFileHeaders(contentType, contentLength, originalFilename).andOnSuccess(
                fileAttributes -> uploadFileActionFn.apply(fileAttributes, inputStreamSupplier(request)));
    }

    private static <T> ServiceResult<T> handleFileConversionWithServiceCall( HttpServletResponse response, Function<Supplier<OutputStream>, ServiceResult<T>> convertFileActionFn) {
        return convertFileActionFn.apply(outputStreamSupplier(response));
    }


    private static Supplier<InputStream> inputStreamSupplier(HttpServletRequest request) {
        return () -> {
            try {
                return request.getInputStream();
            } catch (IOException e) {
                LOG.error("Unable to open an input stream from request", e);
                throw new RuntimeException("Unable to open an input stream from request", e);
            }
        };
    }

    private static Supplier<OutputStream> outputStreamSupplier(HttpServletResponse response) {
        return () -> {
            try {
                return response.getOutputStream();
            } catch (IOException e) {
                LOG.error("Unable to open an output stream from response", e);
                throw new RuntimeException("Unable to open an output stream from response", e);
            }
        };
    }


}
