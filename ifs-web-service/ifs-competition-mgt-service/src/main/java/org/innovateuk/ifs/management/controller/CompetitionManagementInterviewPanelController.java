package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.model.InterviewPanelModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for the Manage Interview Panel dashboard.
 */
@Controller
@RequestMapping("/interview/panel/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can view the Manage Interview Panel dashboard", securedType = CompetitionManagementInterviewPanelController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementInterviewPanelController {

    @Autowired
    private InterviewPanelModelPopulator interviewPanelModelPopulator;

    @GetMapping
    public String interviewPanel(@PathVariable("competitionId") long competitionId, Model model) {
        model.addAttribute("model", interviewPanelModelPopulator.populateModel(competitionId));

        return "competition/manage-interview-panel";
    }
}
