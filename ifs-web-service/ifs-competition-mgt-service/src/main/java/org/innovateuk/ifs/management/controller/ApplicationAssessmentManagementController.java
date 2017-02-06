package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.innovateuk.ifs.management.model.ManageApplicationsModelPopulator;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Controller for the manage application dashboard
 */
@Controller
@RequestMapping("assessment/competition/{competitionId}")
@PreAuthorize("hasAuthority('comp_admin')")
public class ApplicationAssessmentManagementController {

    @Autowired
    private ApplicationCountSummaryRestService  applicationCountSummaryRestService;

    @Autowired
    private CompetitionsRestService competitionsRestService;

    @Autowired
    private ManageApplicationsModelPopulator manageApplicationsPopulator;

    @Autowired
    private CookieUtil cookieUtil;

    @RequestMapping(method = RequestMethod.GET)
    public String manageApplications(Model model, @PathVariable("competitionId") long competitionId, HttpServletRequest request, HttpServletResponse response) {
        CompetitionResource competitionResource = competitionsRestService.getCompetitionById(competitionId).getSuccessObject();
        List<ApplicationCountSummaryResource> applicationCounts = applicationCountSummaryRestService.getApplicationCountSummariesByCompetitionId(competitionId)
                .getSuccessObjectOrThrowException();
        model.addAttribute("model", manageApplicationsPopulator.populateModel(competitionResource, applicationCounts));

        cookieUtil.saveToCookie(response, CompetitionManagementApplicationController.APPLICATION_OVERVIEW_ORIGIN_URL_KEY, HttpUtils.getFullRequestUrl(request));

        return "competition/manage-applications";
    }

}
