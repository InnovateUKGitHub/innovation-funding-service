package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.transactional.AssessmentInterviewPanelInviteService;
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
 * Controller for managing Invites to Interview Panels.
 */
@RestController
@RequestMapping("/interview-panel-invite")
public class AssessmentInterviewPanelInviteController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private AssessmentInterviewPanelInviteService assessmentInterviewPanelInviteService;

    @GetMapping("/get-all-invites-to-send/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(@PathVariable long competitionId) {
        return assessmentInterviewPanelInviteService.getAllInvitesToSend(competitionId).toGetResponse();
    }

    @GetMapping("/get-all-invites-to-resend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(@PathVariable long competitionId,
                                                                           @RequestParam List<Long> inviteIds) {
        return assessmentInterviewPanelInviteService.getAllInvitesToResend(competitionId, inviteIds).toGetResponse();
    }

    @PostMapping("/send-all-invites/{competitionId}")
    public RestResult<Void> sendAllInvites(@PathVariable long competitionId,
                                           @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInterviewPanelInviteService.sendAllInvites(competitionId, assessorInviteSendResource).toPostResponse();
    }

    @PostMapping("/resend-invites")
    public RestResult<Void> resendInvites(@RequestParam List<Long> inviteIds,
                                          @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return assessmentInterviewPanelInviteService.resendInvites(inviteIds, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @GetMapping("/get-created-invites/{competitionId}")
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return assessmentInterviewPanelInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @PostMapping("/invite-users")
    public RestResult<Void> inviteUsers(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return assessmentInterviewPanelInviteService.inviteUsers(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }

    @GetMapping("/get-available-assessors/{competitionId}")
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"user.firstName", "user.lastName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return assessmentInterviewPanelInviteService.getAvailableAssessors(competitionId, pageable).toGetResponse();
    }

    @GetMapping(value = "/get-available-application-ids/{competitionId}")
    public RestResult<List<Long>> getAvailableAssessorIds(@PathVariable long competitionId) {
        return assessmentInterviewPanelInviteService.getAvailableAssessorIds(competitionId).toGetResponse();
    }

    @GetMapping("/get-all-invites-by-user/{userId}")
    public RestResult<List<AssessmentInterviewPanelParticipantResource>> getAllInvitesByUser(@PathVariable long userId) {
        return assessmentInterviewPanelInviteService.getAllInvitesByUser(userId).toGetResponse();
    }

    @GetMapping(value = "/get-non-accepted-assessor-invite-ids/{competitionId}")
    public RestResult<List<Long>> getNonAcceptedAssessorInviteIds(@PathVariable long competitionId) {
        return assessmentInterviewPanelInviteService.getNonAcceptedAssessorInviteIds(competitionId).toGetResponse();
    }

    @GetMapping("/get-invitation-overview/{competitionId}")
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(
            @PathVariable long competitionId,
            @RequestParam List<ParticipantStatus> statuses,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable) {
        return assessmentInterviewPanelInviteService.getInvitationOverview(competitionId, pageable, statuses).toGetResponse();
    }

    @PostMapping("/open-invite/{inviteHash}")
    public RestResult<AssessmentInterviewPanelInviteResource> openInvite(@PathVariable String inviteHash) {
        return assessmentInterviewPanelInviteService.openInvite(inviteHash).toPostWithBodyResponse();
    }

    @GetMapping("/check-existing-user/{inviteHash}")
    public RestResult<Boolean> checkExistingUser(@PathVariable String inviteHash) {
        return assessmentInterviewPanelInviteService.checkExistingUser(inviteHash).toGetResponse();
    }

    @DeleteMapping("/delete-invite")
    public RestResult<Void> deleteInvite(@RequestParam String email, @RequestParam long competitionId) {
        return assessmentInterviewPanelInviteService.deleteInvite(email, competitionId).toDeleteResponse();
    }

    @DeleteMapping("/delete-all-invites")
    public RestResult<Void> deleteAllInvites(@RequestParam long competitionId) {
        return assessmentInterviewPanelInviteService.deleteAllInvites(competitionId).toDeleteResponse();
    }
}
