package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseController;
import com.worth.ifs.assessment.service.CompetitionInviteRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to manage accepting Invites to a Competition.
 */
@Controller
@RequestMapping("/invite-accept")
public class CompetitionInviteAcceptController extends BaseController {

    @Autowired
    private CompetitionInviteRestService inviteRestService;

    @RequestMapping(value = "competition/{inviteHash}/accept", method = RequestMethod.GET)
    public String acceptInvite(@PathVariable("inviteHash") String inviteHash) {
        inviteRestService.acceptInvite(inviteHash).getSuccessObjectOrThrowException();
        return "redirect:/assessor/dashboard";
    }
}