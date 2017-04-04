package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.controller.CompetitionManagementApplicationController.ApplicationOverviewOrigin;
import org.innovateuk.ifs.management.model.ManageApplicationsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

/**
 * Controller for the manage application dashboard
 */
@Controller
@RequestMapping("assessment/competition/{competitionId}")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class ApplicationAssessmentManagementController {

    @Autowired
    private ApplicationCountSummaryRestService  applicationCountSummaryRestService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ManageApplicationsModelPopulator manageApplicationsPopulator;


    @GetMapping
    public String manageApplications(Model model,
                                     @PathVariable("competitionId") long competitionId,
                                     @RequestParam MultiValueMap<String, String> queryParams,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "filterSearch", defaultValue = "") String filter) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        ApplicationCountSummaryPageResource applicationCounts = applicationCountSummaryRestService.getApplicationCountSummariesByCompetitionId(competitionId, page,20, filter)
                .getSuccessObjectOrThrowException();
        String originQuery = buildOriginQueryString(ApplicationOverviewOrigin.MANAGE_APPLICATIONS, queryParams);
        model.addAttribute("model", manageApplicationsPopulator.populateModel(competitionResource, applicationCounts, filter, originQuery));
        model.addAttribute("originQuery", originQuery);

        return "competition/manage-applications";
    }

}
