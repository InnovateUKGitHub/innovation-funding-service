package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.ManageApplicationsModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

@Controller
@RequestMapping("/assessment/competition/{competitionId}")
public class CompetitionManagementAssessmentsApplicationsController extends BaseCompetitionManagementAssessmentsController<ApplicationCountSummaryPageResource> {

    @Autowired
    private ApplicationCountSummaryRestService applicationCountSummaryRestService;

    @Autowired
    private ManageApplicationsModelPopulator manageApplicationsPopulator;

    @GetMapping("/applications")
    public String manageApplications(Model model,
                                     @PathVariable("competitionId") long competitionId,
                                     @RequestParam MultiValueMap<String, String> queryParams,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "filterSearch", defaultValue = "") String filter,
                                     @RequestParam(value = "origin", defaultValue = "MANAGE_ASSESSMENTS") String origin) {
        CompetitionResource competitionResource = getCompetition(competitionId);

        ApplicationCountSummaryPageResource applicationCounts = getCounts(competitionId, page, filter);

        String backUrl = buildBackUrl(origin, competitionId, queryParams);
        String originQuery = buildOriginQueryString(ApplicationOverviewOrigin.MANAGE_APPLICATIONS, queryParams);

        model.addAttribute("model", manageApplicationsPopulator.populateModel(competitionResource, applicationCounts, filter, originQuery));
        model.addAttribute("originQuery", originQuery);
        model.addAttribute("backUrl", backUrl);

        return "competition/manage-applications";
    }

    protected ApplicationCountSummaryPageResource getCounts(long competitionId, int page, String filter) {
        return applicationCountSummaryRestService
                .getApplicationCountSummariesByCompetitionId(competitionId, page, PAGE_SIZE, filter)
                .getSuccessObjectOrThrowException();
    }
}
