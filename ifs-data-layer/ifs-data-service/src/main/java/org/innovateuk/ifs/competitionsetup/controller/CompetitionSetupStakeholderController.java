package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupStakeholderService;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Stakeholder controller to handle RESTful services related to stakeholders
 */
@RestController
@RequestMapping("/competition/setup/{competitionId}/stakeholder")
public class CompetitionSetupStakeholderController {

    @Autowired
    private CompetitionSetupStakeholderService competitionSetupStakeholderService;

    @PostMapping("/invite")
    public RestResult<Void> inviteStakeholder(@PathVariable("competitionId") final long competitionId, @RequestBody InviteUserResource inviteUserResource) {

        return competitionSetupStakeholderService.inviteStakeholder(inviteUserResource.getInvitedUser(), competitionId).toPostResponse();
    }

    @GetMapping("/find-all")
    public RestResult<List<UserResource>> findStakeholders(@PathVariable("competitionId") final long competitionId) {

        return competitionSetupStakeholderService.findStakeholders(competitionId).toGetResponse();
    }

    @PostMapping("/{stakeholderUserId}/add")
    public RestResult<Void> addStakeholder(@PathVariable("competitionId") final long competitionId, @PathVariable("stakeholderUserId") final long stakeholderUserId) {

        return competitionSetupStakeholderService.addStakeholder(competitionId, stakeholderUserId).toPostResponse();
    }

    @PostMapping("/{stakeholderUserId}/remove")
    public RestResult<Void> removeStakeholder(@PathVariable("competitionId") final long competitionId, @PathVariable("stakeholderUserId") final long stakeholderUserId) {

        return competitionSetupStakeholderService.removeStakeholder(competitionId, stakeholderUserId).toPostResponse();
    }

    @GetMapping("/pending-invites")
    public RestResult<List<UserResource>> findPendingStakeholderInvites(@PathVariable("competitionId") final long competitionId) {

        return competitionSetupStakeholderService.findPendingStakeholderInvites(competitionId).toGetResponse();
    }

    @GetMapping("/findCompetitionsByStakeholderId/{stakeholderUserId}")
    public RestResult<List<Long>> findCompetitionsByStakeholderId(@PathVariable("stakeholderUserId") final long stakeholderUserId) {
        return competitionSetupStakeholderService.findCompetitionByStakeholderId(stakeholderUserId).toGetResponse();
    }
}


