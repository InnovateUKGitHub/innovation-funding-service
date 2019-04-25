package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.transactional.AssessmentInviteService;
import org.innovateuk.ifs.commons.ZeroDowntime;
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

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getAllInvitesToSend/{competitionId}", "/get-all-invites-to-send/{competitionId}"})
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(@PathVariable long competitionId) {
        return assessmentInviteService.getAllInvitesToSend(competitionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getAllInvitesToResend/{competitionId}", "/get-all-invites-to-resend/{competitionId}"})
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(
            @PathVariable long competitionId,
            @RequestParam List<Long> inviteIds) {
        return assessmentInviteService.getAllInvitesToResend(competitionId, inviteIds).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getInviteToSend/{inviteId}", "/get-invite-to-send/{inviteId}"})
    public RestResult<AssessorInvitesToSendResource> getInviteToSend(@PathVariable long inviteId) {
        return assessmentInviteService.getInviteToSend(inviteId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getInvite/{inviteHash}", "/get-invite/{inviteHash}"})
    public RestResult<CompetitionInviteResource> getInvite(@PathVariable String inviteHash) {
        return assessmentInviteService.getInvite(inviteHash).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PostMapping({"/openInvite/{inviteHash}", "/open-invite/{inviteHash}"})
    public RestResult<CompetitionInviteResource> openInvite(@PathVariable String inviteHash) {
        return assessmentInviteService.openInvite(inviteHash).toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PostMapping({"/acceptInvite/{inviteHash}", "/accept-invite/{inviteHash}"})
    public RestResult<Void> acceptInvite(@PathVariable String inviteHash) {
        final UserResource currentUser = (UserResource) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return assessmentInviteService.acceptInvite(inviteHash, currentUser).toPostResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PostMapping({"/rejectInvite/{inviteHash}", "/reject-invite/{inviteHash}"})
    public RestResult<Void> rejectInvite(@PathVariable String inviteHash, @Valid @RequestBody CompetitionRejectionResource rejection) {
        return assessmentInviteService.rejectInvite(inviteHash, rejection.getRejectReason(), Optional.ofNullable(rejection.getRejectComment())).toPostResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/checkExistingUser/{inviteHash}", "/check-existing-user/{inviteHash}"})
    public RestResult<Boolean> checkExistingUser(@PathVariable String inviteHash) {
        return assessmentInviteService.checkUserExistsForInvite(inviteHash).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getAvailableAssessors/{competitionId}", "/get-available-assessors/{competitionId}"})
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"firstName", "lastName"}, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam Optional<Long> innovationArea
    ) {
        return assessmentInviteService.getAvailableAssessors(competitionId, pageable, innovationArea).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping(value = {"/getAvailableAssessors/{competitionId}", "/get-available-assessors/{competitionId}"}, params = "all")
    public RestResult<List<Long>> getAvailableAssessorIds(
            @PathVariable long competitionId,
            @RequestParam Optional<Long> innovationArea
    ) {
        return assessmentInviteService.getAvailableAssessorIds(competitionId, innovationArea).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping(value = {"/getAssessorsNotAcceptedInviteIds/{competitionId}", "/get-assessors-not-accepted-invite-ids/{competitionId}"})
    public RestResult<List<Long>> getAssessorsNotAcceptedInviteIds(
            @PathVariable long competitionId,
            @RequestParam Optional<Long> innovationArea,
            @RequestParam List<ParticipantStatus> statuses,
            @RequestParam Optional<Boolean> compliant) {
        return assessmentInviteService.getAssessorsNotAcceptedInviteIds(competitionId, innovationArea, statuses, compliant).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getCreatedInvites/{competitionId}", "/get-created-invites/{competitionId}"})
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return assessmentInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getInvitationOverview/{competitionId}", "/get-invitation-overview/{competitionId}"})
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam Optional<Long> innovationArea,
            @RequestParam List<ParticipantStatus> statuses,
            @RequestParam Optional<Boolean> compliant
    ) {
        return assessmentInviteService.getInvitationOverview(competitionId, pageable, innovationArea, statuses, compliant).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @GetMapping({"/getInviteStatistics/{competitionId}", "/get-invite-statistics/{competitionId}"})
    public RestResult<CompetitionInviteStatisticsResource> getInviteStatistics(@PathVariable Long competitionId) {
        return assessmentInviteService.getInviteStatistics(competitionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PostMapping({"/inviteUser", "/invite-user"})
    public RestResult<CompetitionInviteResource> inviteUser(@Valid @RequestBody ExistingUserStagedInviteResource existingUserStagedInvite) {
        return assessmentInviteService.inviteUser(existingUserStagedInvite).toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PostMapping({"/inviteUsers", "/invite-users"})
    public RestResult<Void> inviteUsers(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return assessmentInviteService.inviteUsers(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PostMapping({"/inviteNewUser", "/invite-new-user"})
    public RestResult<CompetitionInviteResource> inviteNewUser(@Valid @RequestBody NewUserStagedInviteResource newUserStagedInvite) {
        return assessmentInviteService.inviteUser(newUserStagedInvite).toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PostMapping({"/inviteNewUsers/{competitionId}", "/invite-new-users/{competitionId}"})
    public RestResult<Void> inviteNewUsers(@Valid @RequestBody NewUserStagedInviteListResource newUserStagedInvites,
                                           @PathVariable Long competitionId) {
        return assessmentInviteService.inviteNewUsers(newUserStagedInvites.getInvites(), competitionId).toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @DeleteMapping({"/deleteInvite", "/delete-invite"})
    public RestResult<Void> deleteInvite(@RequestParam String email, @RequestParam long competitionId) {
        return assessmentInviteService.deleteInvite(email, competitionId).toDeleteResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @DeleteMapping({"/deleteAllInvites", "/delete-all-invites"})
    public RestResult<Void> deleteAllInvites(@RequestParam long competitionId) {
        return assessmentInviteService.deleteAllInvites(competitionId).toDeleteResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PostMapping({"/sendAllInvites/{competitionId}", "send-all-invites/{competitionId}"})
    public RestResult<Void> sendAllInvites(@PathVariable long competitionId,
                                           @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInviteService.sendAllInvites(competitionId, assessorInviteSendResource).toPostResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PostMapping({"/resendInvite/{inviteId}", "/resend-invite/{inviteId}"})
    public RestResult<Void> resendInvite(@PathVariable long inviteId, @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInviteService.resendInvite(inviteId, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "remove camelCase mapping in h2020 sprint 6")
    @PostMapping({"/resendInvites", "/resend-invites"})
    public RestResult<Void> resendInvites(@RequestParam List<Long> inviteIds, @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInviteService.resendInvites(inviteIds, assessorInviteSendResource).toPostWithBodyResponse();
    }
}
