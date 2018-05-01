package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.interview.resource.AssessorInterviewAllocationPageResource;
import org.innovateuk.ifs.interview.resource.AssessorInterviewAllocationResource;
import org.innovateuk.ifs.interview.transactional.InterviewInviteService;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing Invites to Interview Panels.
 */
@RestController
@RequestMapping("/interview-panel-invite")
public class InterviewInviteController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private InterviewInviteService interviewInviteService;

    @GetMapping("/get-all-invites-to-send/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(@PathVariable long competitionId) {
        return interviewInviteService.getAllInvitesToSend(competitionId).toGetResponse();
    }

    @GetMapping("/get-all-invites-to-resend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(@PathVariable long competitionId,
                                                                           @RequestParam List<Long> inviteIds) {
        return interviewInviteService.getAllInvitesToResend(competitionId, inviteIds).toGetResponse();
    }

    @PostMapping("/send-all-invites/{competitionId}")
    public RestResult<Void> sendAllInvites(@PathVariable long competitionId,
                                           @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return interviewInviteService.sendAllInvites(competitionId, assessorInviteSendResource).toPostResponse();
    }

    @PostMapping("/resend-invites")
    public RestResult<Void> resendInvites(@RequestParam List<Long> inviteIds,
                                          @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return interviewInviteService.resendInvites(inviteIds, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @GetMapping("/get-created-invites/{competitionId}")
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return interviewInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @PostMapping("/invite-users")
    public RestResult<Void> inviteUsers(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return interviewInviteService.inviteUsers(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }

    @GetMapping("/get-available-assessors/{competitionId}")
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"user.firstName", "user.lastName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewInviteService.getAvailableAssessors(competitionId, pageable).toGetResponse();
    }

    @GetMapping(value = "/get-available-assessor-ids/{competitionId}")
    public RestResult<List<Long>> getAvailableAssessorIds(@PathVariable long competitionId) {
        return interviewInviteService.getAvailableAssessorIds(competitionId).toGetResponse();
    }

    @GetMapping("/get-all-invites-by-user/{userId}")
    public RestResult<List<InterviewParticipantResource>> getAllInvitesByUser(@PathVariable long userId) {
        return interviewInviteService.getAllInvitesByUser(userId).toGetResponse();
    }

    @GetMapping(value = "/get-non-accepted-assessor-invite-ids/{competitionId}")
    public RestResult<List<Long>> getNonAcceptedAssessorInviteIds(@PathVariable long competitionId) {
        return interviewInviteService.getNonAcceptedAssessorInviteIds(competitionId).toGetResponse();
    }

    @GetMapping("/get-invitation-overview/{competitionId}")
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(
            @PathVariable long competitionId,
            @RequestParam List<ParticipantStatus> statuses,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewInviteService.getInvitationOverview(competitionId, pageable, statuses).toGetResponse();
    }

    @GetMapping("/get-allocate-overview/{competitionId}")
    public RestResult<AssessorInterviewAllocationPageResource> getAllocateApplicationsOverview(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewInviteService.getAllocateApplicationsOverview(competitionId, pageable).toGetResponse();
    }

    @PostMapping("/open-invite/{inviteHash}")
    public RestResult<InterviewInviteResource> openInvite(@PathVariable String inviteHash) {
        return interviewInviteService.openInvite(inviteHash).toPostWithBodyResponse();
    }

    @PostMapping("/accept-invite/{inviteHash}")
    public RestResult<Void> acceptInvite(@PathVariable String inviteHash) {
        return interviewInviteService.acceptInvite(inviteHash).toPostResponse();
    }

    @PostMapping("/reject-invite/{inviteHash}")
    public RestResult<Void> rejectInvite(@PathVariable String inviteHash) {
        return interviewInviteService.rejectInvite(inviteHash).toPostResponse();
    }

    @GetMapping("/check-existing-user/{inviteHash}")
    public RestResult<Boolean> checkExistingUser(@PathVariable String inviteHash) {
        return interviewInviteService.checkUserExistsForInvite(inviteHash).toGetResponse();
    }

    @DeleteMapping("/delete-invite")
    public RestResult<Void> deleteInvite(@RequestParam String email, @RequestParam long competitionId) {
        return interviewInviteService.deleteInvite(email, competitionId).toDeleteResponse();
    }

    @DeleteMapping("/delete-all-invites")
    public RestResult<Void> deleteAllInvites(@RequestParam long competitionId) {
        return interviewInviteService.deleteAllInvites(competitionId).toDeleteResponse();
    }
}
