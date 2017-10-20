package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.transactional.AssessmentPanelInviteService;
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
 * Controller for managing Invites to Assessment Panels.
 */

@RestController
@RequestMapping("/assessmentpanelinvite")
public class AssessmentPanelInviteController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private AssessmentPanelInviteService assessmentPanelInviteService;

    @GetMapping("/getAllInvitesToSend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(@PathVariable long competitionId) {
        return assessmentPanelInviteService.getAllInvitesToSend(competitionId).toGetResponse();
    }

    @GetMapping("/getAllInvitesToResend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(
            @PathVariable long competitionId,
            @RequestParam List<Long> inviteIds) {
        return assessmentPanelInviteService.getAllInvitesToResend(competitionId, inviteIds).toGetResponse();
    }

    @PostMapping("/sendAllInvites/{competitionId}")
    public RestResult<Void> sendAllInvites(@PathVariable long competitionId,
                                           @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentPanelInviteService.sendAllInvites(competitionId, assessorInviteSendResource).toPostResponse();
    }

    @PostMapping("/resendInvites")
    public RestResult<Void> resendInvites(@RequestParam List<Long> inviteIds, @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentPanelInviteService.resendInvites(inviteIds, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @GetMapping("/getCreatedInvites/{competitionId}")
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return assessmentPanelInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @PostMapping("/inviteUsers")
    public RestResult<Void> inviteUsers(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return assessmentPanelInviteService.inviteUsers(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }

    @GetMapping("/getAvailableAssessors/{competitionId}")
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"user.firstName", "user.lastName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return assessmentPanelInviteService.getAvailableAssessors(competitionId, pageable).toGetResponse();
    }

    @GetMapping(value = "/getAvailableAssessorIds/{competitionId}")
    public RestResult<List<Long>> getAvailableAssessorIds(
            @PathVariable long competitionId) {
        return assessmentPanelInviteService.getAvailableAssessorIds(competitionId).toGetResponse();
    }

    @GetMapping(value = "/getNonAcceptedAssessorInviteIds/{competitionId}")
    public RestResult<List<Long>> getNonAcceptedAssessorInviteIds(
            @PathVariable long competitionId) {
        return assessmentPanelInviteService.getNonAcceptedAssessorInviteIds(competitionId).toGetResponse();
    }

    @GetMapping("/getInvitationOverview/{competitionId}")
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(
            @PathVariable long competitionId,
            @RequestParam List<ParticipantStatus> statuses,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return assessmentPanelInviteService.getInvitationOverview(competitionId, pageable, statuses).toGetResponse();
    }

    @PostMapping("/openInvite/{inviteHash}")
    public RestResult<AssessmentPanelInviteResource> openInvite(@PathVariable String inviteHash) {
        return assessmentPanelInviteService.openInvite(inviteHash).toPostWithBodyResponse();
    }

    @PostMapping("/acceptInvite/{inviteHash}")
    public RestResult<Void> acceptInvite(@PathVariable String inviteHash) {
        final UserResource currentUser = (UserResource) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return assessmentPanelInviteService.acceptInvite(inviteHash, currentUser).toPostResponse();
    }

    @PostMapping("/rejectInvite/{inviteHash}")
    public RestResult<Void> rejectInvite(@PathVariable String inviteHash, @Valid @RequestBody CompetitionRejectionResource rejection) {
        return assessmentPanelInviteService.rejectInvite(inviteHash, rejection.getRejectReason(), Optional.ofNullable(rejection.getRejectComment())).toPostResponse();

    }
}
