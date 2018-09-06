package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competitionsetup.transactional.CompetitionSetupStakeholderService;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}


