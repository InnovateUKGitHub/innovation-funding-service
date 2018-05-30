package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.interview.resource.InterviewApplicationSentInviteResource;
import org.innovateuk.ifs.interview.transactional.InterviewApplicationFeedbackService;
import org.innovateuk.ifs.interview.transactional.InterviewApplicationInviteService;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentService;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * Controller for managing application assignments to Interview Panels.
 */
@RestController
@RequestMapping("/interview-panel")
public class InterviewAssignmentController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private InterviewAssignmentService interviewAssignmentService;

    private InterviewApplicationFeedbackService interviewApplicationFeedbackService;

    private InterviewApplicationInviteService interviewApplicationInviteService;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @Autowired
    public InterviewAssignmentController(InterviewAssignmentService interviewAssignmentService,
                                         InterviewApplicationFeedbackService interviewApplicationFeedbackService,
                                         InterviewApplicationInviteService interviewApplicationInviteService) {
        this.interviewAssignmentService = interviewAssignmentService;
        this.interviewApplicationFeedbackService = interviewApplicationFeedbackService;
        this.interviewApplicationInviteService = interviewApplicationInviteService;
    }

    @GetMapping("/available-applications/{competitionId}")
    public RestResult<AvailableApplicationPageResource> getAvailableApplications(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAssignmentService.getAvailableApplications(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/staged-applications/{competitionId}")
    public RestResult<InterviewAssignmentStagedApplicationPageResource> getStagedApplications(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"target.id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAssignmentService.getStagedApplications(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/assigned-applications/{competitionId}")
    public RestResult<InterviewAssignmentApplicationPageResource> getAssignedApplications(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"target.id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAssignmentService.getAssignedApplications(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/available-application-ids/{competitionId}")
    public RestResult<List<Long>> getAvailableApplicationIds(@PathVariable long competitionId) {
        return interviewAssignmentService.getAvailableApplicationIds(competitionId).toGetResponse();
    }

    @PostMapping("/assign-applications")
    public RestResult<Void> assignApplications(@Valid @RequestBody StagedApplicationListResource stagedApplicationListResource) {
        return interviewAssignmentService.assignApplications(stagedApplicationListResource.getInvites()).toPostWithBodyResponse();
    }

    @PostMapping("/unstage-application/{applicationId}")
    public RestResult<Void> unstageApplication(@PathVariable long applicationId) {
        return interviewAssignmentService.unstageApplication(applicationId).toPostWithBodyResponse();
    }

    @PostMapping("/unstage-applications/{competitionId}")
    public RestResult<Void> unstageApplications(@PathVariable long competitionId) {
        return interviewAssignmentService.unstageApplications(competitionId).toPostWithBodyResponse();
    }

    @GetMapping("/email-template")
    public RestResult<ApplicantInterviewInviteResource> getEmailTemplate() {
        return interviewApplicationInviteService.getEmailTemplate().toGetResponse();
    }

    @PostMapping("/send-invites/{competitionId}")
    public RestResult<Void> sendInvites(@PathVariable long competitionId, @Valid @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return interviewApplicationInviteService.sendInvites(competitionId, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @GetMapping("/is-assigned/{applicationId}")
    public RestResult<Boolean> isApplicationAssigned(@PathVariable long applicationId) {
        return interviewAssignmentService.isApplicationAssigned(applicationId).toGetResponse();
    }

    @PostMapping(value = "/feedback/{applicationId}", produces = "application/json")
    public RestResult<Void> uploadFeedback(@RequestHeader(value = "Content-Type", required = false) String contentType,
                                    @RequestHeader(value = "Content-Length", required = false) String contentLength,
                                    @RequestParam(value = "filename", required = false) String originalFilename,
                                    @PathVariable("applicationId") long applicationId,
                                    HttpServletRequest request)
    {
        return interviewApplicationFeedbackService.uploadFeedback(contentType, contentLength, originalFilename, applicationId, request).toPostCreateResponse();
    }

    @DeleteMapping(value = "/feedback/{applicationId}", produces = "application/json")
    public RestResult<Void> deleteFile(@PathVariable("applicationId") long applicationId) {
        return interviewApplicationFeedbackService.deleteFeedback(applicationId).toDeleteResponse();
    }

    @GetMapping(value = "/feedback/{applicationId}", produces = "application/json")
    public @ResponseBody
    ResponseEntity<Object> downloadFile(@PathVariable("applicationId") long applicationId) throws IOException {
        return fileControllerUtils.handleFileDownload(() -> interviewApplicationFeedbackService.downloadFeedback(applicationId));
    }

    @GetMapping(value = "/feedback-details/{applicationId}", produces = "application/json")
    public RestResult<FileEntryResource> findFile(@PathVariable("applicationId") long applicationId) throws IOException {
        return interviewApplicationFeedbackService.findFeedback(applicationId).toGetResponse();
    }

    @GetMapping(value = "/sent-invite/{applicationId}", produces = "application/json")
    public RestResult<InterviewApplicationSentInviteResource> getSentInvite(@PathVariable("applicationId") long applicationId) throws IOException {
        return interviewApplicationInviteService.getSentInvite(applicationId).toGetResponse();
    }
}