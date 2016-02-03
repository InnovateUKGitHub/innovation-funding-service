package com.worth.ifs.application.controller;

import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.commons.rest.RestErrorEnvelope;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.RestSuccesses;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static com.worth.ifs.commons.rest.RestResultBuilder.newRestResult;
import static com.worth.ifs.commons.error.Errors.*;
import static com.worth.ifs.commons.rest.RestFailures.internalServerErrorRestFailure;
import static com.worth.ifs.commons.rest.RestSuccesses.createdRestSuccess;
import static com.worth.ifs.commons.rest.RestSuccesses.okRestSuccess;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.ParsingFunctions.validLong;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 *
 */
@RestController
@RequestMapping("/forminputresponse")
public class FormInputResponseFileUploadController {

    private static final Log LOG = LogFactory.getLog(FormInputResponseFileUploadController.class);

    @Value("${ifs.data.service.file.storage.fileinputresponse.max.filesize.bytes}")
    private Long maxFilesizeBytes;

    @Value("${ifs.data.service.file.storage.fileinputresponse.valid.media.types}")
    private List<String> validMediaTypes;

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(value = "/file", method = POST, produces = "application/json")
    public RestResult<FormInputResponseFileEntryCreatedResponse> createFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) throws IOException {

        return newRestResult(FormInputResponseFileEntryResource.class, FormInputResponseFileEntryCreatedResponse.class).
                andOnSuccess(resource -> createdRestSuccess(new FormInputResponseFileEntryCreatedResponse(resource.getFileEntryResource().getId()))).
                andWithDefaultFailure(internalServerErrorRestFailure("Error creating file")).perform(() -> {

                    return validContentLengthHeader(contentLength).
                        map(lengthFromHeader -> validContentTypeHeader(contentType).
                        map(typeFromHeader -> validFilename(originalFilename).
                        map(filenameParameter -> validContentLength(lengthFromHeader).
                        map(validLength -> validMediaType(typeFromHeader).
                        map(validType -> createFormInputResponseFile(validType, lengthFromHeader, originalFilename, formInputId, applicationId, processRoleId, request).
                        map(fileEntryPair -> serviceSuccess(fileEntryPair.getValue())))))));
                });
    }

    @RequestMapping(value = "/file", method = PUT, produces = "application/json")
    public RestResult<Void> updateFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) throws IOException {

        return newRestResult(FormInputResponseFileEntryResource.class, Void.class).
               andOnSuccess(okRestSuccess()).
               andWithDefaultFailure(internalServerErrorRestFailure("Error updating file")).
               perform(() -> {

            return validContentLengthHeader(contentLength).
                   map(lengthFromHeader -> validContentTypeHeader(contentType).
                   map(typeFromHeader -> validFilename(originalFilename).
                   map(filenameParameter -> validContentLength(lengthFromHeader).
                   map(validLength -> validMediaType(typeFromHeader).
                   map(validType -> updateFormInputResponseFile(validType, lengthFromHeader, originalFilename, formInputId, applicationId, processRoleId, request).
                   map(ServiceResult::serviceSuccess)
            )))));
        });
    }

    @RequestMapping(value = "/file", method = GET)
    public @ResponseBody ResponseEntity<?> getFileContents(
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId) throws IOException {

        // TODO DW - INFUND-854 - remove try-catch - possibly handle this ResponseEntity with CustomHttpMessageConverter
        try {

            ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> result = doGetFile(formInputId, applicationId, processRoleId);

            return result.mapLeftOrRight(
                    failure -> {
                        RestErrorEnvelope errorResponse = new RestErrorEnvelope(failure.getErrors());
                        return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
                    },
                    success -> {
                        FormInputResponseFileEntryResource fileEntry = success.getKey();
                        InputStream inputStream = success.getValue().get();
                        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.setContentLength(fileEntry.getFileEntryResource().getFilesizeBytes());
                        httpHeaders.setContentType(MediaType.parseMediaType(fileEntry.getFileEntryResource().getMediaType()));
                        return new ResponseEntity<>(inputStreamResource, httpHeaders, OK);
                    }
            );

        } catch (Exception e) {

            LOG.error("Error retrieving file", e);
            return new ResponseEntity<>(new RestErrorEnvelope(internalServerErrorError("Error retrieving file")), INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/fileentry", method = GET, produces = "application/json")
    public @ResponseBody ResponseEntity<?> getFileEntryDetails(
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId) throws IOException {

        // TODO DW - INFUND-854 - remove try-catch - possibly handle this ResponseEntity with CustomHttpMessageConverter
        try {

            ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> result = doGetFile(formInputId, applicationId, processRoleId);

            return result.mapLeftOrRight(
                    failure -> {
                        RestErrorEnvelope errorResponse = new RestErrorEnvelope(failure.getErrors());
                        return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
                    },
                    success -> {
                        FormInputResponseFileEntryResource fileEntry = success.getKey();
                        return new ResponseEntity<>(fileEntry, OK);
                    }
            );

        } catch (Exception e) {

            LOG.error("Error retrieving file details", e);
            return new ResponseEntity<>(new RestErrorEnvelope(internalServerErrorError("Error retrieving file details")), INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/file", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteFileEntry(
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId) throws IOException {

        return newRestResult(FormInputResponse.class, Void.class).
               andOnSuccess(RestSuccesses.noContentRestSuccess()).
               andWithDefaultFailure(internalServerErrorRestFailure("Error deleting file")).
               perform(() -> {

            FormInputResponseFileEntryId compoundId = new FormInputResponseFileEntryId(formInputId, applicationId, processRoleId);
            return applicationService.deleteFormInputResponseFileUpload(compoundId);
        });
    }

    private ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> doGetFile(long formInputId, long applicationId, long processRoleId) {

        FormInputResponseFileEntryId formInputResponseFileEntryId = new FormInputResponseFileEntryId(formInputId, applicationId, processRoleId);
        return applicationService.getFormInputResponseFileUpload(formInputResponseFileEntryId);
    }

    private ServiceResult<Pair<File, FormInputResponseFileEntryResource>> createFormInputResponseFile(MediaType mediaType, long length, String originalFilename, long formInputId, long applicationId, long processRoleId, HttpServletRequest request) {

        LOG.debug("Creating file with filename - " + originalFilename + "; Content Type - " + mediaType + "; Content Length - " + length);

        FileEntryResource fileEntry = new FileEntryResource(null, originalFilename, mediaType, length);
        FormInputResponseFileEntryResource formInputResponseFile = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);
        return applicationService.createFormInputResponseFileUpload(formInputResponseFile, inputStreamSupplier(request));
    }

    private ServiceResult<FormInputResponseFileEntryResource> updateFormInputResponseFile(MediaType mediaType, long length, String originalFilename, long formInputId, long applicationId, long processRoleId, HttpServletRequest request) {

        LOG.debug("Updating file with filename - " + originalFilename + "; Content Type - " + mediaType + "; Content Length - " + length);

        FileEntryResource fileEntry = new FileEntryResource(null, originalFilename, mediaType, length);
        FormInputResponseFileEntryResource formInputResponseFile = new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);
        return applicationService.updateFormInputResponseFileUpload(formInputResponseFile, inputStreamSupplier(request)).map(result -> serviceSuccess(result.getRight()));
    }

    private Supplier<InputStream> inputStreamSupplier(HttpServletRequest request) {
        return () -> {
            try {
                return request.getInputStream();
            } catch (IOException e) {
                LOG.error("Unable to open an input stream from request", e);
                throw new RuntimeException("Unable to open an input stream from request", e);
            }
        };
    }

    private ServiceResult<Long> validContentLengthHeader(String contentLengthHeader) {

        return validLong(contentLengthHeader).map(ServiceResult::serviceSuccess).
                orElseGet(() -> serviceFailure(lengthRequiredError(maxFilesizeBytes)));
    }

    private ServiceResult<String> validContentTypeHeader(String contentTypeHeader) {
        return !StringUtils.isBlank(contentTypeHeader) ? serviceSuccess(contentTypeHeader) : serviceFailure(unsupportedMediaTypeError(validMediaTypes));
    }

    private ServiceResult<Long> validContentLength(long length) {
        if (length > maxFilesizeBytes) {
            return serviceFailure(payloadTooLargeError(maxFilesizeBytes));
        }
        return serviceSuccess(length);
    }

    private ServiceResult<String> validFilename(String filename) {
        return checkParameterIsPresent(filename, "Please supply an original filename as a \"filename\" HTTP Request Parameter");
    }

    private ServiceResult<MediaType> validMediaType(String contentType) {
        if (!validMediaTypes.contains(contentType)) {
            return serviceFailure(unsupportedMediaTypeError(validMediaTypes));
        }
        return serviceSuccess(MediaType.valueOf(contentType));
    }

    private ServiceResult<String> checkParameterIsPresent(String parameterValue, String failureMessage) {
        return !StringUtils.isBlank(parameterValue) ? serviceSuccess(parameterValue) : serviceFailure(badRequestError(failureMessage));
    }

    void setMaxFilesizeBytes(Long maxFilesizeBytes) {
        this.maxFilesizeBytes = maxFilesizeBytes;
    }

    void setValidMediaTypes(List<String> validMediaTypes) {
        this.validMediaTypes = validMediaTypes;
    }
}
