package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.transactional.CompetitionInviteService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.CompetitionRejectionResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Controller for managing Invites to Competitions.
 */
@RestController
@RequestMapping("/competitioninvite")
public class CompetitionInviteController {

    @Autowired
    private CompetitionInviteService competitionInviteService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

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
}
