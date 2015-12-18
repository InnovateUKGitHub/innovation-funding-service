package com.worth.ifs.competition;

import com.worth.ifs.application.form.Form;
import com.worth.ifs.commons.security.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * This controller will handle all requests that are related to a competition.
 */

@Controller
@RequestMapping("/competition")
public class CompetitionController {
    @Autowired
    UserAuthenticationService userAuthenticationService;

    @RequestMapping("/{competitionId}/details")
    public String competitionDetails(Form form, Model model, @PathVariable("competitionId") final Long competitionId,
                                     HttpServletRequest request) {
        boolean userIsLoggedIn = userIsLoggedIn(request);
        model.addAttribute("userIsLoggedIn", userIsLoggedIn);
        model.addAttribute("competitionId", competitionId);

        return "competition-details";
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

