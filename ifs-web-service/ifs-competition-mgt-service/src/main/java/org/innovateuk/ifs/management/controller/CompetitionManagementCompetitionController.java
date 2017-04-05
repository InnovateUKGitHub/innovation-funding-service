package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.CompetitionInFlightModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller will handle all Competition Management requests that are related to a Competition.
 */
@Controller
@RequestMapping("/competition")
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance')")
public class CompetitionManagementCompetitionController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionInFlightModelPopulator competitionInFlightModelPopulator;

    @GetMapping("/{competitionId}")
    public String competition(Model model, @PathVariable("competitionId") Long competitionId) {
        CompetitionResource competition = competitionService.getById(competitionId);
        if (competition.getCompetitionStatus().isInFlight()) {
            model.addAttribute("model", competitionInFlightModelPopulator.populateModel(competition));
            return "competition/competition-in-flight";
        } else {
            throw new IllegalStateException("Unexpected competition state for competition: " + competitionId);
        }
    }

    @PostMapping("/{competitionId}/close-assessment")
    public String closeAssessment(@PathVariable("competitionId") Long competitionId) {
        competitionService.closeAssessment(competitionId).getSuccessObjectOrThrowException();
        return "redirect:/competition/" + competitionId;
    }

    @PostMapping("/{competitionId}/notify-assessors")
    public String notifyAssessors(@PathVariable("competitionId") Long competitionId) {
        competitionService.notifyAssessors(competitionId).getSuccessObjectOrThrowException();
        return "redirect:/competition/" + competitionId;
    }

    @PostMapping("/{competitionId}/release-feedback")
    public String releaseFeedback(@PathVariable("competitionId") Long competitionId) {
        competitionService.releaseFeedback(competitionId);
        return "redirect:/dashboard/project-setup";
    }
}
