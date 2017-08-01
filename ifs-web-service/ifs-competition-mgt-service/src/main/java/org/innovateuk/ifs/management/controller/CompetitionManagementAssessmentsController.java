package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.ManageAssessmentsModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

/**
 * Controller for the Manage Assessments dashboard.
 */
@Controller
@RequestMapping("/assessment/competition/{competitionId}")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionManagementAssessmentsController {

    @Autowired
    private ManageAssessmentsModelPopulator manageAssessmentsModelPopulator;

    @GetMapping
    public String manageAssessments(@PathVariable("competitionId") long competitionId, Model model,
                                    @RequestParam MultiValueMap<String, String> queryParams) {
        String originQuery = buildOriginQueryString(CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.MANAGE_ASSESSMENTS, queryParams);
        model.addAttribute("model", manageAssessmentsModelPopulator.populateModel(competitionId));
        model.addAttribute("originQuery", originQuery);

        return "competition/manage-assessments";
    }
}
