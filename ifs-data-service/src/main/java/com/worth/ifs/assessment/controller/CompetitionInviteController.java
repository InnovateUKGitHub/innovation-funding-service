package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.resource.CompetitionRejectionReasonResource;
import com.worth.ifs.assessment.transactional.CompetitionInviteService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing Invites to Competitions.
 */
@RestController
@RequestMapping("/competitioninvite")
public class CompetitionInviteController {

    @Autowired
    private CompetitionInviteService competitionInviteService;

    @RequestMapping(value = "/openInvite/{inviteHash}", method = RequestMethod.POST)
    public RestResult<CompetitionInviteResource> openInvite(@PathVariable String inviteHash) {
        return competitionInviteService.openInvite(inviteHash).toGetResponse();
    }

    @RequestMapping(value = "/acceptInvite/{inviteHash}", method = RequestMethod.POST)
    public RestResult<Void> acceptInvite(@PathVariable String inviteHash) {
        return competitionInviteService.acceptInvite(inviteHash).toGetResponse();
    }

    @RequestMapping(value = "/rejectInvite/{inviteHash}", method = RequestMethod.POST)
    public RestResult<Void> rejectInvite(@PathVariable String inviteHash, @RequestBody CompetitionRejectionReasonResource rejectionReason, String rejectionComment) {
        return competitionInviteService.rejectInvite(inviteHash, rejectionReason, rejectionComment).toGetResponse();
    }
}
