package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseController;
import org.innovateuk.ifs.assessment.model.UpcomingCompetitionModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to view summary details of upcoming competitions.
 */
@Controller
@RequestMapping("/competition")
public class UpcomingCompetitionController extends BaseController {

    @Autowired
    private UpcomingCompetitionModelPopulator upcomingCompetitionModelPopulator;

    @RequestMapping(value = "/{id}/upcoming", method = RequestMethod.GET)
    public String upcomingCompetitionSummary(@PathVariable("id") final Long competitionId,
                                             final Model model) {
        model.addAttribute("model", upcomingCompetitionModelPopulator.populateModel(competitionId));
        return "assessor-competition-upcoming";
    }
}
