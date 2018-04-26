package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.interview.model.InterviewModelPopulator;
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
@RequestMapping("/assessment/interview/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can view the Manage Interview Panel dashboard", securedType = InterviewController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'INTERVIEW')")
public class InterviewController {

    @Autowired
    private InterviewModelPopulator interviewModelPopulator;

    @GetMapping
    public String interviewPanel(@PathVariable("competitionId") long competitionId, Model model) {
        model.addAttribute("model", interviewModelPopulator.populateModel(competitionId));
        return "competition/manage-interview-panel";
    }
}
