package com.worth.ifs.competition;

import javax.servlet.http.HttpServletRequest;

import com.worth.ifs.application.form.Form;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.security.UserAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all requests that are related to a competition.
 */

@Controller
@RequestMapping("/competition")
public class CompetitionController {
    public static final String TEMPLATE_PATH = "competition/";
    @Autowired
    UserAuthenticationService userAuthenticationService;

    @Autowired
    CompetitionService competitionService;

    @RequestMapping("/{competitionId}/details")
    public String competitionDetails(Form form, Model model, @PathVariable("competitionId") final Long competitionId,
                                     HttpServletRequest request) {
        addUserToModel(model, request);
        addCompetitionToModel(model, competitionId);
        return TEMPLATE_PATH + "details";
    }

    @RequestMapping("/{competitionId}/info/{templateName}")
    public String getInfoPage(Model model, @PathVariable("competitionId") final Long competitionId,
                              HttpServletRequest request, @PathVariable("templateName") String templateName) {
        addUserToModel(model, request);
        addCompetitionToModel(model, competitionId);
        return TEMPLATE_PATH+"info/"+ templateName;
    }

    private void addUserToModel(Model model, HttpServletRequest request) {
        boolean userIsLoggedIn = userIsLoggedIn(request);
        model.addAttribute("userIsLoggedIn", userIsLoggedIn);
    }

    private void addCompetitionToModel(Model model, Long competitionId) {
        model.addAttribute("currentCompetition", competitionService.getById(competitionId));
    }

    private boolean userIsLoggedIn(HttpServletRequest request) {
        Authentication authentication = userAuthenticationService.getAuthentication(request);

        if(authentication != null) {
            return true;
        } else {
            return false;
        }
    }
}

