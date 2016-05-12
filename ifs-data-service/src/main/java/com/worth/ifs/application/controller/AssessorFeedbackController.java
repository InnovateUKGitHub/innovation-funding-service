package com.worth.ifs.application.controller;

import com.worth.ifs.application.resource.AssessorFeedbackResource;
import com.worth.ifs.application.transactional.AssessorFeedbackService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.transactional.FileHttpHeadersValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.worth.ifs.file.controller.FileUploadControllerUtils.inputStreamSupplier;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

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
}