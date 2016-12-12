package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.CompetitionClosedModelPopulator;
import org.innovateuk.ifs.management.model.CompetitionInAssessmentModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This controller will handle all Competition Management requests that are related to a Competition.
 */
@Controller
@RequestMapping("/competition")
public class CompetitionManagementCompetitionController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionClosedModelPopulator competitionClosedModelPopulator;

    @Autowired
    private CompetitionInAssessmentModelPopulator competitionInAssessmentModelPopulator;

    @RequestMapping(value = "/{competitionId}", method = RequestMethod.GET)
    public String competition(Model model, @PathVariable("competitionId") Long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);

        switch (competition.getCompetitionStatus()) {
            case CLOSED:
                model.addAttribute("model", competitionClosedModelPopulator.populateModel(competition));
                return "competition/competition-closed";
            case IN_ASSESSMENT:
                model.addAttribute("model", competitionInAssessmentModelPopulator.populateModel(competition));
                return "competition/competition-in-assessment";
            default:
                throw new IllegalStateException("Unexpected competition state for competition: " + competitionId);
        }
    }

    @RequestMapping(value = "/{competitionId}/close-assessment", method = RequestMethod.POST)
    public String closeAssessment(@PathVariable("competitionId") Long competitionId) {
        competitionService.closeAssessment(competitionId);
        return "redirect:/dashboard";
    }

    @RequestMapping(value = "/{competitionId}/notify-assessors", method = RequestMethod.POST)
    public String notifyAssessors(@PathVariable("competitionId") Long competitionId) {
        competitionService.notifyAssessors(competitionId);
        return "redirect:/dashboard";
    }
}
