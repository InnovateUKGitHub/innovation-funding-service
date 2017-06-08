package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.ManageAssessmentsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/assessment/competition/{competitionId}")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementAssessmentsController {

    @Autowired
    private ManageAssessmentsModelPopulator manageAssessmentsModelPopulator;

    @GetMapping
    public String manageAssessments(@PathVariable("competitionId") long competitionId, Model model) {
        model.addAttribute("model", manageAssessmentsModelPopulator.populateModel(competitionId));
        return "competition/manage-assessments";
    }
}
