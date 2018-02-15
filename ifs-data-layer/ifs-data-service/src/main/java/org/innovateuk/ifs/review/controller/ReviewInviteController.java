package org.innovateuk.ifs.review.controller;

import org.innovateuk.ifs.assessment.transactional.AssessmentReviewPanelInviteService;
import org.innovateuk.ifs.commons.rest.RestResult;
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
 * Controller for managing Invites to Assessment Panels.
 */
@RestController
@RequestMapping(value = {
        "/assessmentpanelinvite",    // TODO IFS-2850 zdd contract
        "/assessment-panel-invite"}) // TODO IFS-2850 zdd expand
public class ReviewInviteController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private AssessmentReviewPanelInviteService assessmentReviewPanelInviteService;

    @GetMapping({
            "/getAllInvitesToSend/{competitionId}",    // TODO IFS-2850 zdd contract
            "/get-all-invites-to-send/{competitionId}" // TODO IFS-2850 zdd expand
    })
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(@PathVariable long competitionId) {
        return assessmentReviewPanelInviteService.getAllInvitesToSend(competitionId).toGetResponse();
    }

    @GetMapping({
            "/getAllInvitesToResend/{competitionId}",     // TODO IFS-2850 zdd contract
            "/get-all-invites-to-resend/{competitionId}"  // TODO IFS-2850 zdd expand
    })
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(@PathVariable long competitionId,
                                                                           @RequestParam List<Long> inviteIds) {
        return assessmentReviewPanelInviteService.getAllInvitesToResend(competitionId, inviteIds).toGetResponse();
    }

    @PostMapping({
            "/sendAllInvites/{competitionId}",  // TODO IFS-2850 zdd contract
            "/send-all-invites/{competitionId}" // TODO IFS-2850 zdd expand
    })
    public RestResult<Void> sendAllInvites(@PathVariable long competitionId,
                                           @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentReviewPanelInviteService.sendAllInvites(competitionId, assessorInviteSendResource).toPostResponse();
    }

    @PostMapping({
            "/resendInvites",  // TODO IFS-2850 zdd contract
            "/resend-invites"  // TODO IFS-2850 zdd expand
    })
    public RestResult<Void> resendInvites(@RequestParam List<Long> inviteIds,
                                          @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentReviewPanelInviteService.resendInvites(inviteIds, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @GetMapping({
            "/getCreatedInvites/{competitionId}",  // TODO IFS-2850 zdd contract
            "/get-created-invites/{competitionId}" // TODO IFS-2850 zdd expand
    })
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return assessmentReviewPanelInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @PostMapping({
            "/inviteUsers",  // TODO IFS-2850 zdd contract
            "/invite-users"  // TODO IFS-2850 zdd expand
    })
    public RestResult<Void> inviteUsers(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return assessmentReviewPanelInviteService.inviteUsers(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }

    @GetMapping({
            "/getAvailableAssessors/{competitionId}",   // TODO IFS-2850 zdd contract
            "/get-available-assessors/{competitionId}"  // TODO IFS-2850 zdd expand
    })
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"user.firstName", "user.lastName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return assessmentReviewPanelInviteService.getAvailableAssessors(competitionId, pageable).toGetResponse();
    }

    @GetMapping({
            "/getAvailableAssessorIds/{competitionId}",    // TODO IFS-2850 zdd contract
            "/get-available-assessor-ids/{competitionId}"  // TODO IFS-2850 zdd expand
    })
    public RestResult<List<Long>> getAvailableAssessorIds(@PathVariable long competitionId) {
        return assessmentReviewPanelInviteService.getAvailableAssessorIds(competitionId).toGetResponse();
    }

    @GetMapping({
            "/getAllInvitesByUser/{userId}",    // TODO IFS-2850 zdd contract
            "/get-all-invites-by-user/{userId}" // TODO IFS-2850 zdd expand
    })
    public RestResult<List<ReviewParticipantResource>> getAllInvitesByUser(@PathVariable long userId) {
        return assessmentReviewPanelInviteService.getAllInvitesByUser(userId).toGetResponse();
    }

    @GetMapping({
            "/getNonAcceptedAssessorInviteIds/{competitionId}",     // TODO IFS-2850 zdd contract
            "/get-non-accepted-assessor-invite-ids/{competitionId}" // TODO IFS-2850 zdd expand
    })
    public RestResult<List<Long>> getNonAcceptedAssessorInviteIds(@PathVariable long competitionId) {
        return assessmentReviewPanelInviteService.getNonAcceptedAssessorInviteIds(competitionId).toGetResponse();
    }

    @GetMapping({
            "/getInvitationOverview/{competitionId}",   // TODO IFS-2850 zdd contract
            "/get-invitation-overview/{competitionId}"  // TODO IFS-2850 zdd expand
    })
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(
            @PathVariable long competitionId,
            @RequestParam List<ParticipantStatus> statuses,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable) {
        return assessmentReviewPanelInviteService.getInvitationOverview(competitionId, pageable, statuses).toGetResponse();
    }

    @PostMapping({
            "/openInvite/{inviteHash}",   // TODO IFS-2850 zdd contract
            "/open-invite/{inviteHash}"   // TODO IFS-2850 zdd expand
    })
    public RestResult<ReviewInviteResource> openInvite(@PathVariable String inviteHash) {
        return assessmentReviewPanelInviteService.openInvite(inviteHash).toPostWithBodyResponse();
    }

    @PostMapping({
            "/acceptInvite/{inviteHash}",  // TODO IFS-2850 zdd contract
            "/accept-invite/{inviteHash}"  // TODO IFS-2850 zdd expand
    })
    public RestResult<Void> acceptInvite(@PathVariable String inviteHash) {
        return assessmentReviewPanelInviteService.acceptInvite(inviteHash).toPostResponse();
    }

    @PostMapping({
            "/rejectInvite/{inviteHash}",   // TODO IFS-2850 zdd contract
            "/reject-invite/{inviteHash}"   // TODO IFS-2850 zdd expand
    })
    public RestResult<Void> rejectInvite(@PathVariable String inviteHash) {
        return assessmentReviewPanelInviteService.rejectInvite(inviteHash).toPostResponse();
    }

    @GetMapping({
            "/checkExistingUser/{inviteHash}",  // TODO IFS-2850 zdd contract
            "/check-existing-user/{inviteHash}" // TODO IFS-2850 zdd expand
    })
    public RestResult<Boolean> checkExistingUser(@PathVariable String inviteHash) {
        return assessmentReviewPanelInviteService.checkExistingUser(inviteHash).toGetResponse();
    }

    @DeleteMapping({
            "/deleteInvite",  // TODO IFS-2850 zdd contract
            "/delete-invite"  // TODO IFS-2850 zdd expand
    })
    public RestResult<Void> deleteInvite(@RequestParam String email, @RequestParam long competitionId) {
        return assessmentReviewPanelInviteService.deleteInvite(email, competitionId).toDeleteResponse();
    }

    @DeleteMapping({
            "/deleteAllInvites",   // TODO IFS-2850 zdd contract
            "/delete-all-invites"  // TODO IFS-2850 zdd expand
    })
    public RestResult<Void> deleteAllInvites(@RequestParam long competitionId) {
        return assessmentReviewPanelInviteService.deleteAllInvites(competitionId).toDeleteResponse();
    }
}
