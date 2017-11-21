package org.innovateuk.ifs.management.controller;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.model.ManagePanelApplicationsModelPopulator;
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

import java.util.Optional;

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

/**
 * Controller for the 'Manage Applications' assessment panel page.
 */
@Controller
@RequestMapping("/assessment/panel/competition/{competitionId}/manage-applications")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class AssessmentPanelManageApplicationsController {
    private static final int PAGE_SIZE  = 20;

    @Autowired
    private ManagePanelApplicationsModelPopulator managePanelApplicationsModelPopulator;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @GetMapping
    public String manageApplications(Model model,
                                     @PathVariable("competitionId") long competitionId,
                                     @RequestParam MultiValueMap<String, String> queryParams,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "filterSearch", defaultValue = "") String filter,
                                     @RequestParam(value = "sort", defaultValue = "") String sortBy) {
        CompetitionResource competitionResource = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccessObjectOrThrowException();
        String originQuery = buildOriginQueryString(CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.MANAGE_APPLICATIONS_PANEL, queryParams);
        ApplicationSummaryPageResource applications = getSummaries(competitionResource.getId(), page, filter, sortBy);
        model.addAttribute("model", managePanelApplicationsModelPopulator.populateModel(competitionResource, applications, filter, sortBy, originQuery));
        model.addAttribute("originQuery", originQuery);

        return "competition/manage-applications-panel";
    }

    private ApplicationSummaryPageResource getSummaries(long competitionId, int page, String filter, String sortBy){
        return applicationSummaryRestService
                .getSubmittedApplications(competitionId, sortBy, page, PAGE_SIZE, Optional.of(filter), Optional.empty())
                .getSuccessObjectOrThrowException();
    }
}
