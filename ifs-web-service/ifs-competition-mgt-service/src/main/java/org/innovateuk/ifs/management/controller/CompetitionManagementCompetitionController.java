package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.CompetitionInFlightModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.EnumSet;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;

/**
 * This controller will handle all Competition Management requests that are related to a Competition.
 */
@Controller
@RequestMapping("/competition")
public class CompetitionManagementCompetitionController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionInFlightModelPopulator competitionInFlightModelPopulator;

    @RequestMapping(value = "/{competitionId}", method = RequestMethod.GET)
    public String competition(Model model, @PathVariable("competitionId") Long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        if (EnumSet.of(READY_TO_OPEN, OPEN, CLOSED, IN_ASSESSMENT, FUNDERS_PANEL, ASSESSOR_FEEDBACK).contains(competition.getCompetitionStatus())) {
            model.addAttribute("model", competitionInFlightModelPopulator.populateModel(competition));
            return "competition/competition-in-flight";
        } else {
            throw new IllegalStateException("Unexpected competition state for competition: " + competitionId);
        }
    }

    @RequestMapping(value = "/{competitionId}/close-assessment", method = RequestMethod.POST)
    public String closeAssessment(@PathVariable("competitionId") Long competitionId) {
        competitionService.closeAssessment(competitionId);
        return "redirect:/competition/" + competitionId;
    }

    @RequestMapping(value = "/{competitionId}/notify-assessors", method = RequestMethod.POST)
    public String notifyAssessors(@PathVariable("competitionId") Long competitionId) {
        competitionService.notifyAssessors(competitionId);
        return "redirect:/competition/" + competitionId;
    }
}
