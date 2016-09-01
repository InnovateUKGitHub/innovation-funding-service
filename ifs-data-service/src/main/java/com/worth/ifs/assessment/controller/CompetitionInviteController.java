package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.transactional.CompetitionInviteService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.CompetitionRejectionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for managing Invites to Competitions.
 */
@RestController
@RequestMapping("/competitioninvite")
public class CompetitionInviteController {

    @Autowired
    private CompetitionInviteService competitionInviteService;

    @RequestMapping(value = "/getInvite/{inviteHash}", method = RequestMethod.GET)
    public RestResult<CompetitionInviteResource> getInvite(@PathVariable String inviteHash) {
        return competitionInviteService.getInvite(inviteHash).toGetResponse();
    }

    @RequestMapping(value = "/openInvite/{inviteHash}", method = RequestMethod.POST)
    public RestResult<CompetitionInviteResource> openInvite(@PathVariable String inviteHash) {
        return competitionInviteService.openInvite(inviteHash).toGetResponse();
    }

    @RequestMapping(value = "/acceptInvite/{inviteHash}", method = RequestMethod.POST)
    public RestResult<Void> acceptInvite(@PathVariable String inviteHash) {
        return competitionInviteService.acceptInvite(inviteHash).toPostResponse();
    }

    @RequestMapping(value = "/rejectInvite/{inviteHash}", method = RequestMethod.POST)
    public RestResult<Void> rejectInvite(@PathVariable String inviteHash, @RequestBody CompetitionRejectionResource rejection) {
        return competitionInviteService.rejectInvite(inviteHash, rejection.getRejectionReasonResource(), Optional.ofNullable(rejection.getRejectionComment())).toPostResponse();
    }

    @RequestMapping(value = "/checkExistingUser/{inviteHash}", method = RequestMethod.GET)
    public RestResult<Boolean> checkExistingUser(@PathVariable String inviteHash) {
        return competitionInviteService.checkExistingUser(inviteHash).toGetResponse();
    }
}
