package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.populator.CompetitionOverviewPopulator;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("permitAll")
public class CompetitionController {
    public static final String TEMPLATE_PATH = "competition/";

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionOverviewPopulator overviewPopulator;

    @RequestMapping("/{competitionId}/overview")
    public String competitionOverview(Model model, @PathVariable("competitionId") final Long competitionId,
                                     HttpServletRequest request) {
        PublicContentItemResource publicContentItem = competitionService.getPublicContentOfCompetition(competitionId).getSuccessObjectOrThrowException();
        model.addAttribute("model", overviewPopulator.populateViewModel(publicContentItem));
        return TEMPLATE_PATH + "overview";
    }

    @RequestMapping("/{competitionId}/details")
    public String competitionDetails(Model model, @PathVariable("competitionId") final Long competitionId,
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
        model.addAttribute("currentCompetition", competitionService.getPublishedById(competitionId));
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

