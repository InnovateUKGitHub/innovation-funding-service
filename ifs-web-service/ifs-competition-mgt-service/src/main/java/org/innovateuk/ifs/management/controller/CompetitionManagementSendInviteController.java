package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.invite.resource.InviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller will handle all Competition Management requests related to sending competition invites to assessors
 */
@Controller
@RequestMapping("/competition/assessors/invite/{inviteId}")
public class CompetitionManagementSendInviteController {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @RequestMapping(method = RequestMethod.GET)
    public String inviteEmail(Model model, @PathVariable("competitionId") Long competitionId,
                              @PathVariable("inviteId") Long inviteId) {
        CompetitionInviteResource invite = competitionInviteRestService.getCreated(inviteId).getSuccessObjectOrThrowException();
        return "assessors/send-invites";
    }
}
