package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.transactional.InterviewPanelInviteService;
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
@RequestMapping("/interviewpanelinvite")
public class InterviewPanelInviteController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private InterviewPanelInviteService interviewPanelInviteService;

    @GetMapping("/getAllInvitesToSend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(@PathVariable long competitionId) {
        return interviewPanelInviteService.getAllInvitesToSend(competitionId).toGetResponse();
    }

    @GetMapping("/getAllInvitesToResend/{competitionId}")
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(@PathVariable long competitionId,
                                                                           @RequestParam List<Long> inviteIds) {
        return interviewPanelInviteService.getAllInvitesToResend(competitionId, inviteIds).toGetResponse();
    }

    @PostMapping("/sendAllInvites/{competitionId}")
    public RestResult<Void> sendAllInvites(@PathVariable long competitionId,
                                           @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return interviewPanelInviteService.sendAllInvites(competitionId, assessorInviteSendResource).toPostResponse();
    }

    @PostMapping("/resendInvites")
    public RestResult<Void> resendInvites(@RequestParam List<Long> inviteIds,
                                          @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return interviewPanelInviteService.resendInvites(inviteIds, assessorInviteSendResource).toPostWithBodyResponse();
    }

    @GetMapping("/getCreatedInvites/{competitionId}")
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return interviewPanelInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @PostMapping("/inviteUsers")
    public RestResult<Void> inviteUsers(@Valid @RequestBody ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return interviewPanelInviteService.inviteUsers(existingUserStagedInvites.getInvites()).toPostWithBodyResponse();
    }

    @GetMapping("/getAvailableAssessors/{competitionId}")
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"user.firstName", "user.lastName"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewPanelInviteService.getAvailableAssessors(competitionId, pageable).toGetResponse();
    }

    @GetMapping(value = "/getAvailableAssessorIds/{competitionId}")
    public RestResult<List<Long>> getAvailableAssessorIds(@PathVariable long competitionId) {
        return interviewPanelInviteService.getAvailableAssessorIds(competitionId).toGetResponse();
    }

    @GetMapping("/getAllInvitesByUser/{userId}")
    public RestResult<List<AssessmentInterviewPanelParticipantResource>> getAllInvitesByUser(@PathVariable long userId) {
        return interviewPanelInviteService.getAllInvitesByUser(userId).toGetResponse();
    }

    @GetMapping(value = "/getNonAcceptedAssessorInviteIds/{competitionId}")
    public RestResult<List<Long>> getNonAcceptedAssessorInviteIds(@PathVariable long competitionId) {
        return interviewPanelInviteService.getNonAcceptedAssessorInviteIds(competitionId).toGetResponse();
    }

    @GetMapping("/getInvitationOverview/{competitionId}")
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(
            @PathVariable long competitionId,
            @RequestParam List<ParticipantStatus> statuses,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "invite.name", direction = Sort.Direction.ASC) Pageable pageable) {
        return interviewPanelInviteService.getInvitationOverview(competitionId, pageable, statuses).toGetResponse();
    }

    @PostMapping("/openInvite/{inviteHash}")
    public RestResult<AssessmentInterviewPanelInviteResource> openInvite(@PathVariable String inviteHash) {
        return interviewPanelInviteService.openInvite(inviteHash).toPostWithBodyResponse();
    }

    @GetMapping("/checkExistingUser/{inviteHash}")
    public RestResult<Boolean> checkExistingUser(@PathVariable String inviteHash) {
        return interviewPanelInviteService.checkExistingUser(inviteHash).toGetResponse();
    }

    @DeleteMapping("/deleteInvite")
    public RestResult<Void> deleteInvite(@RequestParam String email, @RequestParam long competitionId) {
        return interviewPanelInviteService.deleteInvite(email, competitionId).toDeleteResponse();
    }

    @DeleteMapping("/deleteAllInvites")
    public RestResult<Void> deleteAllInvites(@RequestParam long competitionId) {
        return interviewPanelInviteService.deleteAllInvites(competitionId).toDeleteResponse();
    }
}
