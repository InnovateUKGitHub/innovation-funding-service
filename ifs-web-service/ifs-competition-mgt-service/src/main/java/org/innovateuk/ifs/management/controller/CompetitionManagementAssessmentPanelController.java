package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.assessment.service.AssessmentPanelRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.model.AssessmentPanelModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.String.format;

/**
 * Controller for the Manage Assessment Panel dashboard.
 */
@Controller
@RequestMapping("/assessment/panel/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can view the Manage Assessment Panel dashboard", securedType = CompetitionManagementAssessmentPanelController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementAssessmentPanelController {

    @Autowired
    private AssessmentPanelModelPopulator assessmentPanelModelPopulator;

    @Autowired
    private AssessmentPanelRestService assessmentPanelRestService;

    @GetMapping
    public String assessmentPanel(@PathVariable("competitionId") long competitionId, Model model) {
        model.addAttribute("model", assessmentPanelModelPopulator.populateModel(competitionId));

        return "competition/manage-assessment-panel";
    }

    @PostMapping("/notify-assessors")
    public String notifyAssessors(@PathVariable("competitionId") long competitionId) {
        assessmentPanelRestService.notifyAssessors(competitionId).getSuccessObjectOrThrowException();
        return format("redirect:/assessment/panel/competition/%d", competitionId);
    }
}
