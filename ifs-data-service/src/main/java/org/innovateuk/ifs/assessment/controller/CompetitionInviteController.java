package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.assessment.transactional.CompetitionInviteService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.email.resource.EmailContent;
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

    @Autowired
    private CompetitionInviteService competitionInviteService;

    @RequestMapping(value = "/getCreated/{inviteId}", method = RequestMethod.GET)
    public RestResult<AssessorInviteToSendResource> getCreatedInvite(@PathVariable long inviteId) {
        return competitionInviteService.getCreatedInvite(inviteId).toGetResponse();
    }

    @RequestMapping(value = "/getInvite/{inviteHash}", method = RequestMethod.GET)
    public RestResult<CompetitionInviteResource> getInvite(@PathVariable String inviteHash) {
        return competitionInviteService.getInvite(inviteHash).toGetResponse();
    }

    @RequestMapping(value = "/openInvite/{inviteHash}", method = RequestMethod.POST)
    public RestResult<CompetitionInviteResource> openInvite(@PathVariable String inviteHash) {
        return competitionInviteService.openInvite(inviteHash).toPostWithBodyResponse();
    }

    @RequestMapping(value = "/acceptInvite/{inviteHash}", method = RequestMethod.POST)
    public RestResult<Void> acceptInvite(@PathVariable String inviteHash) {
        final UserResource currentUser = (UserResource) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return competitionInviteService.acceptInvite(inviteHash, currentUser).toPostResponse();
    }

    @RequestMapping(value = "/rejectInvite/{inviteHash}", method = RequestMethod.POST)
    public RestResult<Void> rejectInvite(@PathVariable String inviteHash, @Valid @RequestBody CompetitionRejectionResource rejection) {
        return competitionInviteService.rejectInvite(inviteHash, rejection.getRejectReason(), Optional.ofNullable(rejection.getRejectComment())).toPostResponse();
    }

    @RequestMapping(value = "/checkExistingUser/{inviteHash}", method = RequestMethod.GET)
    public RestResult<Boolean> checkExistingUser(@PathVariable String inviteHash) {
        return competitionInviteService.checkExistingUser(inviteHash).toGetResponse();
    }

    @RequestMapping(value = "/getAvailableAssessors/{competitionId}", method = RequestMethod.GET)
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(
            @PathVariable long competitionId,
            @PageableDefault(size = 20, sort = {"firstName", "lastName"}, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam Optional<Long> innovationArea
    ) {
        return competitionInviteService.getAvailableAssessors(competitionId, pageable, innovationArea).toGetResponse();
    }

    @RequestMapping(value = "/getCreatedInvites/{competitionId}", method = RequestMethod.GET)
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(
            @PathVariable long competitionId,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return competitionInviteService.getCreatedInvites(competitionId, pageable).toGetResponse();
    }

    @RequestMapping(value = "/getInvitationOverview/{competitionId}", method = RequestMethod.GET)
    public RestResult<List<AssessorInviteOverviewResource>> getInvitationOverview(@PathVariable Long competitionId) {
        return competitionInviteService.getInvitationOverview(competitionId).toGetResponse();
    }

    @RequestMapping(value = "/getInviteStatistics/{competitionId}", method = RequestMethod.GET)
    public RestResult<CompetitionInviteStatisticsResource> getInviteStatistics(@PathVariable Long competitionId) {
        return competitionInviteService.getInviteStatistics(competitionId).toGetResponse();
    }

    @RequestMapping(value = "/inviteUser", method = RequestMethod.POST)
    public RestResult<CompetitionInviteResource> inviteUser(@Valid @RequestBody ExistingUserStagedInviteResource existingUserStagedInvite) {
        return competitionInviteService.inviteUser(existingUserStagedInvite).toPostWithBodyResponse();
    }

    @RequestMapping(value = "/inviteNewUser", method = RequestMethod.POST)
    public RestResult<CompetitionInviteResource> inviteNewUser(@Valid @RequestBody NewUserStagedInviteResource newUserStagedInvite) {
        return competitionInviteService.inviteUser(newUserStagedInvite).toPostWithBodyResponse();
    }

    @RequestMapping(value = "/inviteNewUsers/{competitionId}", method = RequestMethod.POST)
    public RestResult<Void> inviteNewUsers(@Valid @RequestBody NewUserStagedInviteListResource newUserStagedInvites,
                                           @PathVariable Long competitionId) {
        return competitionInviteService.inviteNewUsers(newUserStagedInvites.getInvites(), competitionId).toPostWithBodyResponse();
    }

    @RequestMapping(value = "/deleteInvite", method = RequestMethod.DELETE)
    public RestResult<Void> deleteInvite(@RequestParam String email, @RequestParam Long competitionId) {
        return competitionInviteService.deleteInvite(email, competitionId).toDeleteResponse();
    }

    @RequestMapping(value = "/sendInvite/{inviteId}", method = RequestMethod.POST)
    public RestResult<Void> sendInvite(@PathVariable long inviteId, @RequestBody AssessorInviteSendResource assessorInviteSendResource) {
        return competitionInviteService.sendInvite(inviteId, assessorInviteSendResource).toPostWithBodyResponse();
    }
}
