package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.management.form.SendInviteForm;
import org.innovateuk.ifs.management.model.SendInvitePopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * This controller will handle all Competition Management requests related to sending competition invites to assessors
 */
@Controller
@RequestMapping("/competition/assessors/invite/{inviteId}")
public class CompetitionManagementSendInviteController {

    @Autowired
    private CompetitionInviteRestService competitionInviteRestService;

    @Autowired
    private SendInvitePopulator sendInvitePopulator;

    @RequestMapping(method = RequestMethod.GET)
    public String inviteEmail(Model model, @PathVariable("inviteId") long inviteId) {
        CompetitionInviteResource invite = competitionInviteRestService.getCreated(inviteId).getSuccessObjectOrThrowException();
        model.addAttribute("model", sendInvitePopulator.populateModel(invite));
        return "assessors/send-invites";
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public String sendEmail(Model model,
                            @PathVariable("inviteId") long inviteId,
                            @ModelAttribute("form") @Valid SendInviteForm form) {
        RestResult<Void> result = competitionInviteRestService.sendInvite(inviteId);
        return "redirect:/competition/assessors/invite/" + inviteId;
    }
}
