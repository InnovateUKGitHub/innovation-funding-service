package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentService;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing application assignments to Interview Panels.
 */
@RestController
@RequestMapping("/interview-panel")
public class InterviewAssignmentController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private InterviewAssignmentService interviewAssignmentService;

    @Autowired
    public InterviewAssignmentController(InterviewAssignmentService interviewAssignmentService) {
        this.interviewAssignmentService = interviewAssignmentService;
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

    @GetMapping("key-statistics/{competitionId}")
    public RestResult<InterviewAssignmentKeyStatisticsResource> getKeyStatistics(@PathVariable long competitionId) {
        return interviewAssignmentService.getKeyStatistics(competitionId).toGetResponse();
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
        return interviewAssignmentService.getEmailTemplate().toGetResponse();
    }

    @PostMapping("/send-invites/{competitionId}")
    public RestResult<Void> sendInvites(@PathVariable long competitionId, @Valid @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return interviewAssignmentService.sendInvites(competitionId, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @GetMapping("/is-assigned/{applicationId}")
    public RestResult<Boolean> isApplicationAssigned(@PathVariable long applicationId) {
        return interviewAssignmentService.isApplicationAssigned(applicationId).toGetResponse();
    }
}