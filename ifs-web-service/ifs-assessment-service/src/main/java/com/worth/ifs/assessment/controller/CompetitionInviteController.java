package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseController;
import com.worth.ifs.assessment.service.CompetitionInviteRestService;
import com.worth.ifs.assessment.viewmodel.CompetitionInviteViewModel;
import com.worth.ifs.commons.error.exception.InvalidURLException;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller to manage Invites to a Competition.
 */
@Controller
@RequestMapping("/invite")
public class CompetitionInviteController extends BaseController {

    @Autowired
    private CompetitionInviteRestService inviteRestService;

    @RequestMapping(value = "competition/{inviteHash}", method= RequestMethod.GET)
    public String openInvite(@PathVariable("inviteHash") String inviteHash, HttpServletResponse response,
                             HttpServletRequest request,
                             Model model) {

        RestResult<CompetitionInviteResource> invite = inviteRestService.openInvite(inviteHash);

        if (invite.isFailure()) {
            throw new InvalidURLException("Invite url is not valid", null);
        }
        else {
            CompetitionInviteResource inviteResource = invite.getSuccessObject();

            model.addAttribute("model", new CompetitionInviteViewModel(inviteResource.getCompetitionName()));

            return "assessor-competition-invite";
        }
    }
}
