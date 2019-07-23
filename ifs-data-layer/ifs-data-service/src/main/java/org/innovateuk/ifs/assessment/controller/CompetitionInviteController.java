package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.transactional.AssessmentInviteService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing Invites to Competitions.
 */
@RestController
@RequestMapping("/competitioninvite")
public class CompetitionInviteController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private AssessmentInviteService assessmentInviteService;

    @GetMapping("/get-all-invites-to-send/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(@PathVariable long competitionId) {
        return assessmentInviteService.getAllInvitesToSend(competitionId).toGetResponse();
    }

    @GetMapping("/get-all-invites-to-resend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(
            @PathVariable long competitionId,
            @RequestParam List<Long> inviteIds) {
        return assessmentInviteService.getAllInvitesToResend(competitionId, inviteIds).toGetResponse();
    }

    @GetMapping("/get-invite-to-send/{inviteId}")
    public RestResult<AssessorInvitesToSendResource> getInviteToSend(@PathVariable long inviteId) {
        return assessmentInviteService.getInviteToSend(inviteId).toGetResponse();
    }

    @GetMapping("/get-invite/{inviteHash}")
    public RestResult<CompetitionInviteResource> getInvite(@PathVariable String inviteHash) {
        return assessmentInviteService.getInvite(inviteHash).toGetResponse();
    }

    @PostMapping("/open-invite/{inviteHash}")
    public RestResult<CompetitionInviteResource> openInvite(@PathVariable String inviteHash) {
        return assessmentInviteService.openInvite(inviteHash).toPostWithBodyResponse();
    }

    @PostMapping("/accept-invite/{inviteHash}")
    public RestResult<Void> acceptInvite(@PathVariable String inviteHash) {
        final UserResource currentUser = (UserResource) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return assessmentInviteService.acceptInvite(inviteHash, currentUser).toPostResponse();
    }

    @PostMapping("/reject-invite/{inviteHash}")
    public RestResult<Void> rejectInvite(@PathVariable String inviteHash, @Valid @RequestBody CompetitionRejectionResource rejection) {
        return assessmentInviteService.rejectInvite(inviteHash, rejection.getRejectReason(), Optional.ofNullable(rejection.getRejectComment())).toPostResponse();
    }

    @GetMapping("/check-existing-user/{inviteHash}")
    public RestResult<Boolean> checkExistingUser(@PathVariable String inviteHash) {
        return assessmentInviteService.checkUserExistsForInvite(inviteHash).toGetResponse();
    }

    @GetMapping("/get-available-assessors/{competitionId}")
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"firstName", "lastName"}, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam String assessorNameFilter
    ) {
        return assessmentInviteService.getAvailableAssessors(competitionId, pageable, assessorNameFilter).toGetResponse();
    }

    @GetMapping(value = "/get-available-assessors/{competitionId}", params = "all")
    public RestResult<List<Long>> getAvailableAssessorIds(
            @PathVariable long competitionId,
            @RequestParam String assessorNameFilter
    ) {
        return assessmentInviteService.getAvailableAssessorIds(competitionId, assessorNameFilter).toGetResponse();
    }

    @GetMapping(value = "/get-assessors-not-accepted-invite-ids/{competitionId}")
    public RestResult<List<Long>> getAssessorsNotAcceptedInviteIds(
            @PathVariable long competitionId,
            @RequestParam Optional<Long> innovationArea,
            @RequestParam List<ParticipantStatus> statuses,
            @RequestParam Optional<Boolean> compliant) {
        return assessmentInviteService.getAssessorsNotAcceptedInviteIds(competitionId, innovationArea, statuses, compliant).toGetResponse();
    }

    @GetMapping("/get-created-invites/{competitionId}")
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return assessmentInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/get-invitation-overview/{competitionId}")
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam Optional<Long> innovationArea,
            @RequestParam List<ParticipantStatus> statuses,
            @RequestParam Optional<Boolean> compliant
    ) {
        return assessmentInviteService.getInvitationOverview(competitionId, pageable, innovationArea, statuses, compliant).toGetResponse();
    }

    @GetMapping("/get-invite-statistics/{competitionId}")
    public RestResult<CompetitionInviteStatisticsResource> getInviteStatistics(@PathVariable Long competitionId) {
        return assessmentInviteService.getInviteStatistics(competitionId).toGetResponse();
    }

    @PostMapping("/invite-user")
    public RestResult<CompetitionInviteResource> inviteUser(@Valid @RequestBody ExistingUserStagedInviteResource existingUserStagedInvite) {
        return assessmentInviteService.inviteUser(existingUserStagedInvite).toPostWithBodyResponse();
    }

    @PostMapping("/invite-users")
    public RestResult<Void> inviteUsers(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return assessmentInviteService.inviteUsers(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }

    @PostMapping("/invite-new-user")
    public RestResult<CompetitionInviteResource> inviteNewUser(@Valid @RequestBody NewUserStagedInviteResource newUserStagedInvite) {
        return assessmentInviteService.inviteUser(newUserStagedInvite).toPostWithBodyResponse();
    }

    @PostMapping("/invite-new-users/{competitionId}")
    public RestResult<Void> inviteNewUsers(@Valid @RequestBody NewUserStagedInviteListResource newUserStagedInvites,
                                           @PathVariable Long competitionId) {
        return assessmentInviteService.inviteNewUsers(newUserStagedInvites.getInvites(), competitionId).toPostWithBodyResponse();
    }

    @DeleteMapping("/delete-invite")
    public RestResult<Void> deleteInvite(@RequestParam String email, @RequestParam long competitionId) {
        return assessmentInviteService.deleteInvite(email, competitionId).toDeleteResponse();
    }

    @DeleteMapping("/delete-all-invites")
    public RestResult<Void> deleteAllInvites(@RequestParam long competitionId) {
        return assessmentInviteService.deleteAllInvites(competitionId).toDeleteResponse();
    }

    @PostMapping("send-all-invites/{competitionId}")
    public RestResult<Void> sendAllInvites(@PathVariable long competitionId,
                                           @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInviteService.sendAllInvites(competitionId, assessorInviteSendResource).toPostResponse();
    }

    @PostMapping("/resend-invite/{inviteId}")
    public RestResult<Void> resendInvite(@PathVariable long inviteId, @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInviteService.resendInvite(inviteId, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @PostMapping("/resend-invites")
    public RestResult<Void> resendInvites(@RequestParam List<Long> inviteIds, @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInviteService.resendInvites(inviteIds, assessorInviteSendResource).toPostWithBodyResponse();
    }
}
