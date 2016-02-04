package com.worth.ifs.application;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.service.InviteRestService;
import com.worth.ifs.login.LoginForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class AcceptInviteController extends AbstractApplicationController {
    private final Log log = LogFactory.getLog(getClass());
    @Autowired
    private InviteRestService inviteRestService;



    @RequestMapping(value = "/accept-invite/{applicationId}/{hash}", method = RequestMethod.GET)
    public String displayContributors(@PathVariable("applicationId") final Long applicationId, @PathVariable("hash") final String hash, HttpServletRequest request, Model model) {
        ApplicationResource application = applicationService.getById(applicationId);
        Competition competition = competitionService.getById(application.getCompetition());
        Optional<InviteResource> invite = inviteRestService.getInviteByHash(hash);


        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("currentApplication", application);
        model.addAttribute("currentCompetition", competition);

        if(invite.isPresent()){
            InviteResource inviteResource = invite.get();
            log.debug("Found the invite " + inviteResource.getEmail());
            log.debug("Found the invite " + inviteResource.getName());
            log.debug("Found the invite " + inviteResource.getStatus().name());
        }else {
            log.error("INVITE NOT FOUND");
            return "redirect:/login";
        }
        return "accept-invite";
    }
}
