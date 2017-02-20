package org.innovateuk.ifs.nonifs.controller;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.service.CompetitionDashboardSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller for all Non-IFS competition actions.
 */
@Controller
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class NonIfsCompetitionController {
    public static final String TEMPLATE_PATH = "dashboard/";

    @Autowired
    private CompetitionDashboardSearchService competitionDashboardSearchService;

    @Autowired
    private CompetitionService competitionService;

    @GetMapping("/non-ifs-competition/create")
    public String create(){
        CompetitionResource competition = competitionService.createNonIfs();
        return String.format("redirect:/non-ifs-competition/setup/%s", competition.getId());
    }

    @GetMapping("/non-ifs-competition/setup/{competitionId}")
    public String setup(Model model, @PathVariable("competitionId") Long competitionId) {
        return "competition/non-ifs-details";
    }

}
