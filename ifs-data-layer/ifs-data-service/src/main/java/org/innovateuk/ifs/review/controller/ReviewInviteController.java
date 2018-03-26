package org.innovateuk.ifs.review.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.review.transactional.ReviewInviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing Invites to Assessment Panels.
 */
@RestController
@RequestMapping(value = "/assessment-panel-invite")
public class ReviewInviteController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private ReviewInviteService reviewInviteService;

    @GetMapping("/get-all-invites-to-send/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(@PathVariable long competitionId) {
        return reviewInviteService.getAllInvitesToSend(competitionId).toGetResponse();
    }

    @GetMapping("/get-all-invites-to-resend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(@PathVariable long competitionId,
                                                                           @RequestParam List<Long> inviteIds) {
        return reviewInviteService.getAllInvitesToResend(competitionId, inviteIds).toGetResponse();
    }

    @PostMapping("/send-all-invites/{competitionId}")
    public RestResult<Void> sendAllInvites(@PathVariable long competitionId,
                                           @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return reviewInviteService.sendAllInvites(competitionId, assessorInviteSendResource).toPostResponse();
    }

    @PostMapping("/resend-invites")
    public RestResult<Void> resendInvites(@RequestParam List<Long> inviteIds,
                                          @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return reviewInviteService.resendInvites(inviteIds, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @GetMapping("/get-created-invites/{competitionId}")
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return reviewInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @PostMapping("/invite-users")
    public RestResult<Void> inviteUsers(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return reviewInviteService.inviteUsers(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }

    @GetMapping("/get-available-assessors/{competitionId}")
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"user.firstName", "user.lastName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return reviewInviteService.getAvailableAssessors(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/get-available-assessor-ids/{competitionId}")
    public RestResult<List<Long>> getAvailableAssessorIds(@PathVariable long competitionId) {
        return reviewInviteService.getAvailableAssessorIds(competitionId).toGetResponse();
    }

    @GetMapping("/get-all-invites-by-user/{userId}")
    public RestResult<List<ReviewParticipantResource>> getAllInvitesByUser(@PathVariable long userId) {
        return reviewInviteService.getAllInvitesByUser(userId).toGetResponse();
    }

    @GetMapping("/get-non-accepted-assessor-invite-ids/{competitionId}")
    public RestResult<List<Long>> getNonAcceptedAssessorInviteIds(@PathVariable long competitionId) {
        return reviewInviteService.getNonAcceptedAssessorInviteIds(competitionId).toGetResponse();
    }

    @GetMapping(
            "/get-invitation-overview/{competitionId}")
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(
            @PathVariable long competitionId,
            @RequestParam List<ParticipantStatus> statuses,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable) {
        return reviewInviteService.getInvitationOverview(competitionId, pageable, statuses).toGetResponse();
    }

    @PostMapping("/open-invite/{inviteHash}")
    public RestResult<ReviewInviteResource> openInvite(@PathVariable String inviteHash) {
        return reviewInviteService.openInvite(inviteHash).toPostWithBodyResponse();
    }

    @PostMapping("/accept-invite/{inviteHash}")
    public RestResult<Void> acceptInvite(@PathVariable String inviteHash) {
        return reviewInviteService.acceptInvite(inviteHash).toPostResponse();
    }

    @PostMapping("/reject-invite/{inviteHash}")
    public RestResult<Void> rejectInvite(@PathVariable String inviteHash) {
        return reviewInviteService.rejectInvite(inviteHash).toPostResponse();
    }

    @GetMapping("/check-existing-user/{inviteHash}")
    public RestResult<Boolean> checkExistingUser(@PathVariable String inviteHash) {
        return reviewInviteService.checkUserExistsForInvite(inviteHash).toGetResponse();
    }

    @DeleteMapping("/delete-invite")
    public RestResult<Void> deleteInvite(@RequestParam String email, @RequestParam long competitionId) {
        return reviewInviteService.deleteInvite(email, competitionId).toDeleteResponse();
    }

    @DeleteMapping("/delete-all-invites")
    public RestResult<Void> deleteAllInvites(@RequestParam long competitionId) {
        return reviewInviteService.deleteAllInvites(competitionId).toDeleteResponse();
    }
}
