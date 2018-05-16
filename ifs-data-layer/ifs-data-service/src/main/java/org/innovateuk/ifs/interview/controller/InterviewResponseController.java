package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.transactional.InterviewResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Controller for responding to interview feedback.
 */
@RestController
@RequestMapping("/interview-response")
public class InterviewResponseController {

    private InterviewResponseService interviewResponseService;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @Autowired
    public InterviewResponseController(InterviewResponseService interviewResponseService) {
        this.interviewResponseService = interviewResponseService;
    }

    @PostMapping(value = "/{applicationId}", produces = "application/json")
    public RestResult<Void> uploadResponse(@RequestHeader(value = "Content-Type", required = false) String contentType,
                                           @RequestHeader(value = "Content-Length", required = false) String contentLength,
                                           @RequestParam(value = "filename", required = false) String originalFilename,
                                           @PathVariable("applicationId") long applicationId,
                                           HttpServletRequest request)
    {
        return interviewResponseService.uploadResponse(contentType, contentLength, originalFilename, applicationId, request).toPostCreateResponse();
    }

    @DeleteMapping(value = "/{applicationId}", produces = "application/json")
    public RestResult<Void> deleteFile(@PathVariable("applicationId") long applicationId) {
        return interviewResponseService.deleteResponse(applicationId).toDeleteResponse();
    }

    @GetMapping(value = "/{applicationId}", produces = "application/json")
    public @ResponseBody
    ResponseEntity<Object> downloadFile(@PathVariable("applicationId") long applicationId) throws IOException {
        return fileControllerUtils.handleFileDownload(() -> interviewResponseService.downloadResponse(applicationId));
    }

    @GetMapping(value = "/details/{applicationId}", produces = "application/json")
    public RestResult<FileEntryResource> findFile(@PathVariable("applicationId") long applicationId) throws IOException {
        return interviewResponseService.findResponse(applicationId).toGetResponse();
    }

}