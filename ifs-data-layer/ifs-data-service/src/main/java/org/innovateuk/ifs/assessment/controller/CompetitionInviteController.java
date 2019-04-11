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

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/getAllInvitesToSend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSendOld(@PathVariable long competitionId) {
        return assessmentInviteService.getAllInvitesToSend(competitionId).toGetResponse();
    }

    @GetMapping("/get-all-invites-to-send/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(@PathVariable long competitionId) {
        return assessmentInviteService.getAllInvitesToSend(competitionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/getAllInvitesToResend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResendOld(
            @PathVariable long competitionId,
            @RequestParam List<Long> inviteIds) {
        return assessmentInviteService.getAllInvitesToResend(competitionId, inviteIds).toGetResponse();
    }

    @GetMapping("/get-all-invites-to-resend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(
            @PathVariable long competitionId,
            @RequestParam List<Long> inviteIds) {
        return assessmentInviteService.getAllInvitesToResend(competitionId, inviteIds).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/getInviteToSend/{inviteId}")
    public RestResult<AssessorInvitesToSendResource> getInviteToSendOld(@PathVariable long inviteId) {
        return assessmentInviteService.getInviteToSend(inviteId).toGetResponse();
    }

    @GetMapping("/get-invite-to-send/{inviteId}")
    public RestResult<AssessorInvitesToSendResource> getInviteToSend(@PathVariable long inviteId) {
        return assessmentInviteService.getInviteToSend(inviteId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/getInvite/{inviteHash}")
    public RestResult<CompetitionInviteResource> getInviteOld(@PathVariable String inviteHash) {
        return assessmentInviteService.getInvite(inviteHash).toGetResponse();
    }

    @GetMapping("/get-invite/{inviteHash}")
    public RestResult<CompetitionInviteResource> getInvite(@PathVariable String inviteHash) {
        return assessmentInviteService.getInvite(inviteHash).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/openInvite/{inviteHash}")
    public RestResult<CompetitionInviteResource> openInviteOld(@PathVariable String inviteHash) {
        return assessmentInviteService.openInvite(inviteHash).toPostWithBodyResponse();
    }

    @PostMapping("/open-invite/{inviteHash}")
    public RestResult<CompetitionInviteResource> openInvite(@PathVariable String inviteHash) {
        return assessmentInviteService.openInvite(inviteHash).toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/acceptInvite/{inviteHash}")
    public RestResult<Void> acceptInviteOld(@PathVariable String inviteHash) {
        final UserResource currentUser = (UserResource) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return assessmentInviteService.acceptInvite(inviteHash, currentUser).toPostResponse();
    }

    @PostMapping("/accept-invite/{inviteHash}")
    public RestResult<Void> acceptInvite(@PathVariable String inviteHash) {
        final UserResource currentUser = (UserResource) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return assessmentInviteService.acceptInvite(inviteHash, currentUser).toPostResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/rejectInvite/{inviteHash}")
    public RestResult<Void> rejectInviteOld(@PathVariable String inviteHash, @Valid @RequestBody CompetitionRejectionResource rejection) {
        return assessmentInviteService.rejectInvite(inviteHash, rejection.getRejectReason(), Optional.ofNullable(rejection.getRejectComment())).toPostResponse();
    }

    @PostMapping("/reject-invite/{inviteHash}")
    public RestResult<Void> rejectInvite(@PathVariable String inviteHash, @Valid @RequestBody CompetitionRejectionResource rejection) {
        return assessmentInviteService.rejectInvite(inviteHash, rejection.getRejectReason(), Optional.ofNullable(rejection.getRejectComment())).toPostResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/checkExistingUser/{inviteHash}")
    public RestResult<Boolean> checkExistingUserOld(@PathVariable String inviteHash) {
        return assessmentInviteService.checkUserExistsForInvite(inviteHash).toGetResponse();
    }

    @GetMapping("/check-existing-user/{inviteHash}")
    public RestResult<Boolean> checkExistingUser(@PathVariable String inviteHash) {
        return assessmentInviteService.checkUserExistsForInvite(inviteHash).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/getAvailableAssessors/{competitionId}")
    public RestResult<AvailableAssessorPageResource> getAvailableAssessorsOld(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"firstName", "lastName"}, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam Optional<Long> innovationArea
    ) {
        return assessmentInviteService.getAvailableAssessors(competitionId, pageable, innovationArea).toGetResponse();
    }

    @GetMapping("/get-available-assessors/{competitionId}")
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"firstName", "lastName"}, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam Optional<Long> innovationArea
    ) {
        return assessmentInviteService.getAvailableAssessors(competitionId, pageable, innovationArea).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping(value = "/getAvailableAssessors/{competitionId}", params = "all")
    public RestResult<List<Long>> getAvailableAssessorIdsOld(
            @PathVariable long competitionId,
            @RequestParam Optional<Long> innovationArea
    ) {
        return assessmentInviteService.getAvailableAssessorIds(competitionId, innovationArea).toGetResponse();
    }

    @GetMapping(value = "/get-available-assessors/{competitionId}", params = "all")
    public RestResult<List<Long>> getAvailableAssessorIds(
            @PathVariable long competitionId,
            @RequestParam Optional<Long> innovationArea
    ) {
        return assessmentInviteService.getAvailableAssessorIds(competitionId, innovationArea).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping(value = "/getAssessorsNotAcceptedInviteIds/{competitionId}")
    public RestResult<List<Long>> getAssessorsNotAcceptedInviteIdsOld(
            @PathVariable long competitionId,
            @RequestParam Optional<Long> innovationArea,
            @RequestParam List<ParticipantStatus> statuses,
            @RequestParam Optional<Boolean> compliant) {
        return assessmentInviteService.getAssessorsNotAcceptedInviteIds(competitionId, innovationArea, statuses, compliant).toGetResponse();
    }

    @GetMapping(value = "/get-assessors-not-accepted-invite-ids/{competitionId}")
    public RestResult<List<Long>> getAssessorsNotAcceptedInviteIds(
            @PathVariable long competitionId,
            @RequestParam Optional<Long> innovationArea,
            @RequestParam List<ParticipantStatus> statuses,
            @RequestParam Optional<Boolean> compliant) {
        return assessmentInviteService.getAssessorsNotAcceptedInviteIds(competitionId, innovationArea, statuses, compliant).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/getCreatedInvites/{competitionId}")
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvitesOld(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return assessmentInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/get-created-invites/{competitionId}")
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return assessmentInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/getInvitationOverview/{competitionId}")
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverviewOld(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam Optional<Long> innovationArea,
            @RequestParam List<ParticipantStatus> statuses,
            @RequestParam Optional<Boolean> compliant
    ) {
        return assessmentInviteService.getInvitationOverview(competitionId, pageable, innovationArea, statuses, compliant).toGetResponse();
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

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @GetMapping("/getInviteStatistics/{competitionId}")
    public RestResult<CompetitionInviteStatisticsResource> getInviteStatisticsOld(@PathVariable Long competitionId) {
        return assessmentInviteService.getInviteStatistics(competitionId).toGetResponse();
    }

    @GetMapping("/get-invite-statistics/{competitionId}")
    public RestResult<CompetitionInviteStatisticsResource> getInviteStatistics(@PathVariable Long competitionId) {
        return assessmentInviteService.getInviteStatistics(competitionId).toGetResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/inviteUser")
    public RestResult<CompetitionInviteResource> inviteUserOld(@Valid @RequestBody ExistingUserStagedInviteResource existingUserStagedInvite) {
        return assessmentInviteService.inviteUser(existingUserStagedInvite).toPostWithBodyResponse();
    }

    @PostMapping("/invite-user")
    public RestResult<CompetitionInviteResource> inviteUser(@Valid @RequestBody ExistingUserStagedInviteResource existingUserStagedInvite) {
        return assessmentInviteService.inviteUser(existingUserStagedInvite).toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/inviteUsers")
    public RestResult<Void> inviteUsersOld(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return assessmentInviteService.inviteUsers(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }

    @PostMapping("/invite-users")
    public RestResult<Void> inviteUsers(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return assessmentInviteService.inviteUsers(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/inviteNewUser")
    public RestResult<CompetitionInviteResource> inviteNewUserOld(@Valid @RequestBody NewUserStagedInviteResource newUserStagedInvite) {
        return assessmentInviteService.inviteUser(newUserStagedInvite).toPostWithBodyResponse();
    }

    @PostMapping("/invite-new-user")
    public RestResult<CompetitionInviteResource> inviteNewUser(@Valid @RequestBody NewUserStagedInviteResource newUserStagedInvite) {
        return assessmentInviteService.inviteUser(newUserStagedInvite).toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/inviteNewUsers/{competitionId}")
    public RestResult<Void> inviteNewUsersOld(@Valid @RequestBody NewUserStagedInviteListResource newUserStagedInvites,
                                           @PathVariable Long competitionId) {
        return assessmentInviteService.inviteNewUsers(newUserStagedInvites.getInvites(), competitionId).toPostWithBodyResponse();
    }

    @PostMapping("/invite-new-users/{competitionId}")
    public RestResult<Void> inviteNewUsers(@Valid @RequestBody NewUserStagedInviteListResource newUserStagedInvites,
                                           @PathVariable Long competitionId) {
        return assessmentInviteService.inviteNewUsers(newUserStagedInvites.getInvites(), competitionId).toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @DeleteMapping("/deleteInvite")
    public RestResult<Void> deleteInviteOld(@RequestParam String email, @RequestParam long competitionId) {
        return assessmentInviteService.deleteInvite(email, competitionId).toDeleteResponse();
    }

    @DeleteMapping("/delete-invite")
    public RestResult<Void> deleteInvite(@RequestParam String email, @RequestParam long competitionId) {
        return assessmentInviteService.deleteInvite(email, competitionId).toDeleteResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @DeleteMapping("/deleteAllInvites")
    public RestResult<Void> deleteAllInvitesOld(@RequestParam long competitionId) {
        return assessmentInviteService.deleteAllInvites(competitionId).toDeleteResponse();
    }

    @DeleteMapping("/delete-all-invites")
    public RestResult<Void> deleteAllInvites(@RequestParam long competitionId) {
        return assessmentInviteService.deleteAllInvites(competitionId).toDeleteResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/sendAllInvites/{competitionId}")
    public RestResult<Void> sendAllInvitesOld(@PathVariable long competitionId,
                                           @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInviteService.sendAllInvites(competitionId, assessorInviteSendResource).toPostResponse();
    }

    @PostMapping("/send-all-invites/{competitionId}")
    public RestResult<Void> sendAllInvites(@PathVariable long competitionId,
                                           @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInviteService.sendAllInvites(competitionId, assessorInviteSendResource).toPostResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/resendInvite/{inviteId}")
    public RestResult<Void> resendInviteOld(@PathVariable long inviteId, @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInviteService.resendInvite(inviteId, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @PostMapping("/resend-invite/{inviteId}")
    public RestResult<Void> resendInvite(@PathVariable long inviteId, @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInviteService.resendInvite(inviteId, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @ZeroDowntime(reference = "IFS-430", description = "delete in h2020 sprint 6")
    @PostMapping("/resendInvites")
    public RestResult<Void> resendInvitesOld(@RequestParam List<Long> inviteIds, @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInviteService.resendInvites(inviteIds, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @PostMapping("/resend-invites")
    public RestResult<Void> resendInvites(@RequestParam List<Long> inviteIds, @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInviteService.resendInvites(inviteIds, assessorInviteSendResource).toPostWithBodyResponse();
    }
}
