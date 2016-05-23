package com.worth.ifs.application.controller;

import com.worth.ifs.application.resource.FormInputResponseFileEntryId;
import com.worth.ifs.application.resource.FormInputResponseFileEntryResource;
import com.worth.ifs.application.transactional.ApplicationService;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.transactional.FileHeaderAttributes;
import com.worth.ifs.file.transactional.FileHttpHeadersValidator;
import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.file.controller.FileUploadControllerUtils.inputStreamSupplier;
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

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    @Qualifier("formInputResponseFileValidator")
    private FileHttpHeadersValidator fileValidator;

    @RequestMapping(value = "/file", method = POST, produces = "application/json")
    public RestResult<FormInputResponseFileEntryCreatedResponse> createFile(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) throws IOException {

        ServiceResult<FormInputResponseFileEntryCreatedResponse> creationResult =
                fileValidator.validateFileHeaders(contentType, contentLength, originalFilename).andOnSuccess(fileAttributes ->
                createFormInputResponseFile(fileAttributes, formInputId, applicationId, processRoleId, request)).
                andOnSuccessReturn(entry -> new FormInputResponseFileEntryCreatedResponse(entry.getValue().getFileEntryResource().getId()));

        return creationResult.toPostCreateResponse();
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

        ServiceResult<FormInputResponseFileEntryResource> updateResult =
                fileValidator.validateFileHeaders(contentType, contentLength, originalFilename).andOnSuccess(fileAttributes ->
                updateFormInputResponseFile(fileAttributes, formInputId, applicationId, processRoleId, request));

        return updateResult.toPutResponse();
    }

    @RequestMapping(value = "/file", method = GET)
    public @ResponseBody ResponseEntity<Object> getFileContents(
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId) throws IOException {

        // TODO DW - INFUND-854 - remove try-catch - possibly handle this ResponseEntity with CustomHttpMessageConverter
        try {

            ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> result = doGetFile(formInputId, applicationId, processRoleId);

            return result.handleSuccessOrFailure(
                    failure -> {
                        RestErrorResponse errorResponse = new RestErrorResponse(failure.getErrors());
                        return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
                    },
                    success -> {
                        FormInputResponseFileEntryResource fileEntry = success.getKey();
                        InputStream inputStream = success.getValue().get();
                        ByteArrayResource inputStreamResource = new ByteArrayResource(StreamUtils.copyToByteArray(inputStream));
                        HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.setContentLength(fileEntry.getFileEntryResource().getFilesizeBytes());
                        httpHeaders.setContentType(MediaType.parseMediaType(fileEntry.getFileEntryResource().getMediaType()));
                        return new ResponseEntity<>(inputStreamResource, httpHeaders, OK);
                    }
            );

        } catch (Exception e) {

            LOG.error("Error retrieving file", e);
            return new ResponseEntity<>(new RestErrorResponse(internalServerErrorError("Error retrieving file")), INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/fileentry", method = GET, produces = "application/json")
    public @ResponseBody ResponseEntity<Object> getFileEntryDetails(
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId) throws IOException {

        // TODO DW - INFUND-854 - remove try-catch - possibly handle this ResponseEntity with CustomHttpMessageConverter
        try {

            ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> result = doGetFile(formInputId, applicationId, processRoleId);

            return result.handleSuccessOrFailure(
                    failure -> {
                        RestErrorResponse errorResponse = new RestErrorResponse(failure.getErrors());
                        return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
                    },
                    success -> {
                        FormInputResponseFileEntryResource fileEntry = success.getKey();
                        return new ResponseEntity<>(fileEntry, OK);
                    }
            );

        } catch (Exception e) {

            LOG.error("Error retrieving file details", e);
            return new ResponseEntity<>(new RestErrorResponse(internalServerErrorError("Error retrieving file details")), INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/file", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteFileEntry(
            @RequestParam("formInputId") long formInputId,
            @RequestParam("applicationId") long applicationId,
            @RequestParam("processRoleId") long processRoleId) throws IOException {

        FormInputResponseFileEntryId compoundId = new FormInputResponseFileEntryId(formInputId, applicationId, processRoleId);
        ServiceResult<FormInputResponse> deleteResult = applicationService.deleteFormInputResponseFileUpload(compoundId);
        return deleteResult.toDeleteResponse();
    }

    private ServiceResult<Pair<FormInputResponseFileEntryResource, Supplier<InputStream>>> doGetFile(long formInputId, long applicationId, long processRoleId) {
        FormInputResponseFileEntryId formInputResponseFileEntryId = new FormInputResponseFileEntryId(formInputId, applicationId, processRoleId);
        return applicationService.getFormInputResponseFileUpload(formInputResponseFileEntryId);
    }

    private ServiceResult<Pair<File, FormInputResponseFileEntryResource>> createFormInputResponseFile(FileHeaderAttributes fileAttributes, long formInputId, long applicationId, long processRoleId, HttpServletRequest request) {
        FormInputResponseFileEntryResource formInputResponseFile = createFormInputResponseFileEntry(fileAttributes, formInputId, applicationId, processRoleId);
        return applicationService.createFormInputResponseFileUpload(formInputResponseFile, inputStreamSupplier(request));
    }

    private ServiceResult<FormInputResponseFileEntryResource> updateFormInputResponseFile(FileHeaderAttributes fileAttributes, long formInputId, long applicationId, long processRoleId, HttpServletRequest request) {
        FormInputResponseFileEntryResource formInputResponseFile = createFormInputResponseFileEntry(fileAttributes, formInputId, applicationId, processRoleId);
        return applicationService.updateFormInputResponseFileUpload(formInputResponseFile, inputStreamSupplier(request)).
                andOnSuccessReturn(Pair::getValue);
    }

    private FormInputResponseFileEntryResource createFormInputResponseFileEntry(FileHeaderAttributes fileAttributes, long formInputId, long applicationId, long processRoleId) {
        FileEntryResource fileEntry = fileAttributes.toFileEntryResource();
        return new FormInputResponseFileEntryResource(fileEntry, formInputId, applicationId, processRoleId);
    }
}
