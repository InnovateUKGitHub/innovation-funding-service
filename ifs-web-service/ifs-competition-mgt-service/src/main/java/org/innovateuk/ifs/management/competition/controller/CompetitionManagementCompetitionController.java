package org.innovateuk.ifs.management.competition.controller;

import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.commons.exception.IncorrectStateForPageException;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.populator.CompetitionInFlightModelPopulator;
import org.innovateuk.ifs.user.resource.UserResource;
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
@SecuredBySpring(value = "Controller", description = "Comp Admins, Project Finance users," +
        "Innovation Leads and Stakeholders can view the competition dashboard", securedType = CompetitionManagementCompetitionController.class)
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance','innovation_lead', 'stakeholder')")
public class CompetitionManagementCompetitionController {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private AssessorRestService assessorRestService;

    @Autowired
    private CompetitionPostSubmissionRestService competitionPostSubmissionRestService;

    @Autowired
    private CompetitionInFlightModelPopulator competitionInFlightModelPopulator;

    @GetMapping("/{competitionId}")
    public String competition(Model model, @PathVariable("competitionId") Long competitionId, UserResource user) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if (competition.getCompetitionStatus().isInFlight()) {
            model.addAttribute("model", competitionInFlightModelPopulator.populateModel(competition, user));
            return "competition/competition-in-flight";
        } if (competition.getCompetitionStatus().equals(CompetitionStatus.PROJECT_SETUP)) {
            throw new ObjectNotFoundException();
        } else {
            throw new IncorrectStateForPageException("Unexpected competition state for competition: " + competitionId);
        }
    }

    @PostMapping("/{competitionId}/close-assessment")
    public String closeAssessment(@PathVariable("competitionId") Long competitionId) {
        competitionPostSubmissionRestService.closeAssessment(competitionId).getSuccess();
        return "redirect:/competition/" + competitionId;
    }

    @PostMapping("/{competitionId}/notify-assessors")
    public String notifyAssessors(@PathVariable("competitionId") Long competitionId) {
        assessorRestService.notifyAssessors(competitionId).getSuccess();
        return "redirect:/competition/" + competitionId;
    }

    @PostMapping("/{competitionId}/release-feedback")
    public String releaseFeedback(@PathVariable("competitionId") Long competitionId) {
        competitionPostSubmissionRestService.releaseFeedback(competitionId);

        if (isCompetitionTypeEOI(competitionId)) {
            return "redirect:/dashboard/previous";
        } else {
            return "redirect:/dashboard/project-setup";
        }
    }

    private boolean isCompetitionTypeEOI(Long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        return "Expression of interest".equals(competition.getCompetitionTypeName());
    }
}
