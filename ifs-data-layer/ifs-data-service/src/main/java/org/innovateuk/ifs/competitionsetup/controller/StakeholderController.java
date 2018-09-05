package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competitionsetup.transactional.StakeholderService;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Stakeholder controller to handle RESTful services related to stakeholders
 */

@RestController
@RequestMapping("/competition/setup/stakeholder")
public class StakeholderController {

    @Autowired
    private StakeholderService stakeholderService;

    @PostMapping("/invite")
    public RestResult<Void> inviteStakeholder(@RequestBody InviteUserResource inviteUserResource) {

        return stakeholderService.inviteStakeholder(inviteUserResource.getInvitedUser()).toPostResponse();
    }
}


