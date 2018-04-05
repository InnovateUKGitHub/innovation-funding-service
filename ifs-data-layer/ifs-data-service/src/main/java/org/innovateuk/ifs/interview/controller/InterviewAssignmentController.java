package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentInviteService;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing application Invites to Interview Panels.
 */
@RestController
@RequestMapping("/interview-panel")
public class InterviewAssignmentController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private InterviewAssignmentInviteService interviewAssignmentInviteService;

    @GetMapping("/available-applications/{competitionId}")
    public RestResult<AvailableApplicationPageResource> getAvailableApplications(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAssignmentInviteService.getAvailableApplications(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/staged-applications/{competitionId}")
    public RestResult<InterviewAssignmentStagedApplicationPageResource> getStagedApplications(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"target.id"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewAssignmentInviteService.getStagedApplications(competitionId, pageable).toGetResponse();
    }

    @GetMapping(value = "/available-application-ids/{competitionId}")
    public RestResult<List<Long>> getAvailableApplicationIds(@PathVariable long competitionId) {
        return interviewAssignmentInviteService.getAvailableApplicationIds(competitionId).toGetResponse();
    }

    @PostMapping("/assign-applications")
    public RestResult<Void> assignApplications(@Valid @RequestBody StagedApplicationListResource stagedApplicationListResource) {
        return interviewAssignmentInviteService.assignApplications(stagedApplicationListResource.getInvites()).toPostWithBodyResponse();
    }

    @GetMapping("/email-template")
    public RestResult<ApplicantInterviewInviteResource> getEmailTemplate() {
        return interviewAssignmentInviteService.getEmailTemplate().toGetResponse();
    }

    @PostMapping("/send-invites/{competitionId}")
    public RestResult<Void> sendInvites(@PathVariable long competitionId, @Valid @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return interviewAssignmentInviteService.sendInvites(competitionId, assessorInviteSendResource).toPostWithBodyResponse();
    }
}