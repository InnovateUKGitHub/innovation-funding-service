package com.worth.ifs.application.controller;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.application.transactional.AssessorFeedbackService;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.transactional.FileHttpHeadersValidator;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.file.controller.FileUploadControllerUtils.inputStreamSupplier;
import static org.hibernate.jpa.internal.QueryImpl.LOG;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/assessorfeedback")
public class AssessorFeedbackController {

    @Autowired
    private AssessorFeedbackService assessorFeedbackService;

    @Autowired
    @Qualifier("assessorFeedbackFileValidator")
    private FileHttpHeadersValidator fileValidator;

    @RequestMapping("/{id}")
    public RestResult<AssessorFeedbackResource> findById(@PathVariable("id") final Long id) {
        return assessorFeedbackService.findOne(id).toGetResponse();
    }

    @RequestMapping("/findByAssessor/{id}")
    public RestResult<AssessorFeedbackResource> findByAssessorId(@PathVariable("id") final Long assessorId) {
        return assessorFeedbackService.findByAssessorId(assessorId).toGetResponse();
    }

    @RequestMapping(value = "/assessorFeedbackDocument", method = POST, produces = "application/json")
    public RestResult<FileEntryResource> addAssessorFeedbackDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationId") long applicationId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        ServiceResult<FileEntryResource> fileAddedResult =
                fileValidator.validateFileHeaders(contentType, contentLength, originalFilename).andOnSuccess(fileAttributes ->
                assessorFeedbackService.createAssessorFeedbackFileEntry(applicationId, fileAttributes.toFileEntryResource(), inputStreamSupplier(request)));

        return fileAddedResult.toPostCreateResponse();
    }

    @RequestMapping(value = "/assessorFeedbackDocument", method = GET)
    public @ResponseBody
    ResponseEntity<Object> getFileContents(
            @RequestParam("applicationId") long applicationId) throws IOException {

        // TODO DW - INFUND-854 - remove try-catch - possibly handle this ResponseEntity with CustomHttpMessageConverter
        try {

            ServiceResult<Pair<FileEntryResource, Supplier<InputStream>>> getFileResult = assessorFeedbackService.getAssessorFeedbackFileEntryContents(applicationId);

            return getFileResult.handleSuccessOrFailure(
                    failure -> {
                        RestErrorResponse errorResponse = new RestErrorResponse(failure.getErrors());
                        return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
                    },
                    fileResult -> {
                        FileEntryResource fileEntry = fileResult.getKey();
                        Supplier<InputStream> inputStreamSupplier = fileResult.getValue();
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
            return new ResponseEntity<>(new RestErrorResponse(internalServerErrorError("Error retrieving file")), INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/assessorFeedbackDocument/fileentry", method = GET, produces = "application/json")
    public RestResult<FileEntryResource> getFileEntryDetails(
            @RequestParam("applicationId") long applicationId) throws IOException {

        return assessorFeedbackService.getAssessorFeedbackFileEntryDetails(applicationId).toGetResponse();
    }


    @RequestMapping(value = "/assessorFeedbackDocument", method = PUT, produces = "application/json")
    public RestResult<Void> updateAssessorFeedbackDocument(
            @RequestHeader(value = "Content-Type", required = false) String contentType,
            @RequestHeader(value = "Content-Length", required = false) String contentLength,
            @RequestParam(value = "applicationId") long applicationId,
            @RequestParam(value = "filename", required = false) String originalFilename,
            HttpServletRequest request) {

        ServiceResult<FileEntryResource> updateResult = fileValidator.validateFileHeaders(contentType, contentLength, originalFilename).andOnSuccess(fileAttributes ->
                assessorFeedbackService.updateAssessorFeedbackFileEntry(applicationId, fileAttributes.toFileEntryResource(), inputStreamSupplier(request)));

        return updateResult.toPutResponse();
    }

    @RequestMapping(value = "/assessorFeedbackDocument", method = DELETE, produces = "application/json")
    public RestResult<Void> deleteAssessorFeedbackDocument(
            @RequestParam("applicationId") long applicationId) throws IOException {

        ServiceResult<Void> deleteResult = assessorFeedbackService.deleteAssessorFeedbackFileEntry(applicationId);
        return deleteResult.toDeleteResponse();
    }
    
    @RequestMapping(value = "/assessorFeedbackUploaded", method = GET, produces = "application/json")
    public RestResult<Boolean> assessorFeedbackUploaded(
            @RequestParam("competitionId") long competitionId) {

        ServiceResult<Boolean> uploadedResult = assessorFeedbackService.assessorFeedbackUploaded(competitionId);
        return uploadedResult.toGetResponse();
    }
    
    @RequestMapping(value = "/submitAssessorFeedback/{competitionId}", method = POST, produces = "application/json")
    public RestResult<Void> submitAssessorFeedback(
            @PathVariable("competitionId") long competitionId) {

        return assessorFeedbackService.submitAssessorFeedback(competitionId).andOnSuccess(() ->
               assessorFeedbackService.notifyLeadApplicantsOfAssessorFeedback(competitionId)).
               toPostResponse();
    }
}