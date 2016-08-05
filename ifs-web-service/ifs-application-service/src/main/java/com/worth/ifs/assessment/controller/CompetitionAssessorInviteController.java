package com.worth.ifs.assessment.controller;

import com.worth.ifs.assessment.viewmodel.CompetitionAssessorInviteViewModel;
import com.worth.ifs.commons.error.exception.InvalidURLException;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionAssessorInviteResource;
import com.worth.ifs.invite.service.CompetitionAssessorInviteRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/invite")
public class CompetitionAssessorInviteController {

    @Autowired
    private CompetitionAssessorInviteRestService inviteRestService;

    // this gets the invitation and marks it as accessed
    @RequestMapping(value = "competition/{hash}", method= RequestMethod.GET)
    public String accessInvite(@PathVariable String hash, HttpServletResponse response,
                            HttpServletRequest request,
                            Model model) {

        RestResult<CompetitionAssessorInviteResource> invite = inviteRestService.accessInvite(hash);

        if (!invite.isSuccess()) {
            throw new InvalidURLException("Invite url is not valid", null);
        }
        else {
            CompetitionAssessorInviteResource inviteResource = invite.getSuccessObject();

            model.addAttribute("model", new CompetitionAssessorInviteViewModel(inviteResource.getCompetitionName()));

            return "access-competition-invite"; // timeleaf view template name
        }

    }

}
