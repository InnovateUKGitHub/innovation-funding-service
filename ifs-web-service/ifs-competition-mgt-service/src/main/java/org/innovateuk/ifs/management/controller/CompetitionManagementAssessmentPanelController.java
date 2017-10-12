package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.AssessmentPanelModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the Manage Assessment Panel dashboard.
 */
@Controller
@RequestMapping("/assessment/panel/competition/{competitionId}")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementAssessmentPanelController {

    @Autowired
    private AssessmentPanelModelPopulator assessmentPanelModelPopulator;

    @GetMapping
    public String assessmentPanel(@PathVariable("competitionId") long competitionId, Model model) {
        model.addAttribute("model", assessmentPanelModelPopulator.populateModel(competitionId));

        return "competition/manage-assessment-panel";
    }
}
