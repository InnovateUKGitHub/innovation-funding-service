package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.service.AssessorCountSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.ManageAssessorsModelPopulator;
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
public class CompetitionManagementAssessmentsAssessorsController extends BaseCompetitionManagementAssessmentsController<AssessorCountSummaryPageResource> {

    @Autowired
    private AssessorCountSummaryRestService applicationCountSummaryRestService;

    @Autowired
    private ManageAssessorsModelPopulator manageApplicationsPopulator;

    @GetMapping("/assessors")
    public String manageAssessors(Model model,
                                     @PathVariable("competitionId") long competitionId,
                                     @RequestParam MultiValueMap<String, String> queryParams,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "filterSearch", defaultValue = "") String filter,
                                     @RequestParam(value = "origin", defaultValue = "MANAGE_ASSESSMENTS") String origin) {
        CompetitionResource competitionResource = getCompetition(competitionId);

        AssessorCountSummaryPageResource applicationCounts = getCounts(competitionId, page);

        String manageApplicationsOriginQuery = buildBackUrl(origin, competitionId, queryParams);
        String originQuery = buildOriginQueryString(ApplicationOverviewOrigin.MANAGE_ASSESSORS, queryParams);

        model.addAttribute("model", manageApplicationsPopulator.populateModel(competitionResource, applicationCounts, originQuery));
        model.addAttribute("originQuery", originQuery);
        model.addAttribute("manageAssessorsOriginQuery", manageApplicationsOriginQuery);

        return "competition/manage-assessors";
    }

    protected AssessorCountSummaryPageResource getCounts(long competitionId, int page) {
        return applicationCountSummaryRestService
                .getAssessorCountSummariesByCompetitionId(competitionId, page, PAGE_SIZE)
                .getSuccessObjectOrThrowException();
    }
}