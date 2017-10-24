package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.transactional.CompetitionInviteService;
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
    private CompetitionInviteService competitionInviteService;

    @GetMapping("/getAllInvitesToSend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(@PathVariable long competitionId) {
        return competitionInviteService.getAllInvitesToSend(competitionId).toGetResponse();
    }

    @GetMapping("/getAllInvitesToResend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(
            @PathVariable long competitionId,
            @RequestParam List<Long> inviteIds) {
        return competitionInviteService.getAllInvitesToResend(competitionId, inviteIds).toGetResponse();
    }

    @GetMapping("/getInviteToSend/{inviteId}")
    public RestResult<AssessorInvitesToSendResource> getInviteToSend(@PathVariable long inviteId) {
        return competitionInviteService.getInviteToSend(inviteId).toGetResponse();
    }

    @GetMapping("/getInvite/{inviteHash}")
    public RestResult<CompetitionInviteResource> getInvite(@PathVariable String inviteHash) {
        return competitionInviteService.getInvite(inviteHash).toGetResponse();
    }

    @PostMapping("/openInvite/{inviteHash}")
    public RestResult<CompetitionInviteResource> openInvite(@PathVariable String inviteHash) {
        return competitionInviteService.openInvite(inviteHash).toPostWithBodyResponse();
    }

    @PostMapping("/acceptInvite/{inviteHash}")
    public RestResult<Void> acceptInvite(@PathVariable String inviteHash) {
        final UserResource currentUser = (UserResource) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return competitionInviteService.acceptInvite(inviteHash, currentUser).toPostResponse();
    }

    @PostMapping("/rejectInvite/{inviteHash}")
    public RestResult<Void> rejectInvite(@PathVariable String inviteHash, @Valid @RequestBody CompetitionRejectionResource rejection) {
        return competitionInviteService.rejectInvite(inviteHash, rejection.getRejectReason(), Optional.ofNullable(rejection.getRejectComment())).toPostResponse();
    }

    @GetMapping("/checkExistingUser/{inviteHash}")
    public RestResult<Boolean> checkExistingUser(@PathVariable String inviteHash) {
        return competitionInviteService.checkExistingUser(inviteHash).toGetResponse();
    }

    @GetMapping("/getAvailableAssessors/{competitionId}")
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"firstName", "lastName"}, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam Optional<Long> innovationArea
    ) {
        return competitionInviteService.getAvailableAssessors(competitionId, pageable, innovationArea).toGetResponse();
    }

    @GetMapping(value = "/getAvailableAssessors/{competitionId}", params = "all")
    public RestResult<List<Long>> getAvailableAssessorIds(
            @PathVariable long competitionId,
            @RequestParam Optional<Long> innovationArea
    ) {
        return competitionInviteService.getAvailableAssessorIds(competitionId, innovationArea).toGetResponse();
    }

    @GetMapping(value = "/getAssessorsNotAcceptedInviteIds/{competitionId}")
    public RestResult<List<Long>> getAssessorsNotAcceptedInviteIds(
            @PathVariable long competitionId,
            @RequestParam Optional<Long> innovationArea,
            @RequestParam List<ParticipantStatus> statuses,
            @RequestParam Optional<Boolean> compliant) {
        return competitionInviteService.getAssessorsNotAcceptedInviteIds(competitionId, innovationArea, statuses, compliant).toGetResponse();
    }

    @GetMapping("/getCreatedInvites/{competitionId}")
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return competitionInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @GetMapping("/getInvitationOverview/{competitionId}")
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam Optional<Long> innovationArea,
            @RequestParam List<ParticipantStatus> statuses,
            @RequestParam Optional<Boolean> compliant
    ) {
        return competitionInviteService.getInvitationOverview(competitionId, pageable, innovationArea, statuses, compliant).toGetResponse();
    }

    @GetMapping("/getInviteStatistics/{competitionId}")
    public RestResult<CompetitionInviteStatisticsResource> getInviteStatistics(@PathVariable Long competitionId) {
        return competitionInviteService.getInviteStatistics(competitionId).toGetResponse();
    }

    @PostMapping("/inviteUser")
    public RestResult<CompetitionInviteResource> inviteUser(@Valid @RequestBody ExistingUserStagedInviteResource existingUserStagedInvite) {
        return competitionInviteService.inviteUser(existingUserStagedInvite).toPostWithBodyResponse();
    }

    @PostMapping("/inviteUsers")
    public RestResult<Void> inviteUsers(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return competitionInviteService.inviteUsers(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }

    @PostMapping("/inviteNewUser")
    public RestResult<CompetitionInviteResource> inviteNewUser(@Valid @RequestBody NewUserStagedInviteResource newUserStagedInvite) {
        return competitionInviteService.inviteUser(newUserStagedInvite).toPostWithBodyResponse();
    }

    @PostMapping("/inviteNewUsers/{competitionId}")
    public RestResult<Void> inviteNewUsers(@Valid @RequestBody NewUserStagedInviteListResource newUserStagedInvites,
                                           @PathVariable Long competitionId) {
        return competitionInviteService.inviteNewUsers(newUserStagedInvites.getInvites(), competitionId).toPostWithBodyResponse();
    }

    @DeleteMapping("/deleteInvite")
    public RestResult<Void> deleteInvite(@RequestParam String email, @RequestParam long competitionId) {
        return competitionInviteService.deleteInvite(email, competitionId).toDeleteResponse();
    }

    @DeleteMapping("/deleteAllInvites")
    public RestResult<Void> deleteAllInvites(@RequestParam long competitionId) {
        return competitionInviteService.deleteAllInvites(competitionId).toDeleteResponse();
    }

    @PostMapping("/sendAllInvites/{competitionId}")
    public RestResult<Void> sendAllInvites(@PathVariable long competitionId,
                                           @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return competitionInviteService.sendAllInvites(competitionId, assessorInviteSendResource).toPostResponse();
    }

    @PostMapping("/resendInvite/{inviteId}")
    public RestResult<Void> resendInvite(@PathVariable long inviteId, @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return competitionInviteService.resendInvite(inviteId, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @PostMapping("/resendInvites")
    public RestResult<Void> resendInvites(@RequestParam List<Long> inviteIds, @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return competitionInviteService.resendInvites(inviteIds, assessorInviteSendResource).toPostWithBodyResponse();
    }
}
