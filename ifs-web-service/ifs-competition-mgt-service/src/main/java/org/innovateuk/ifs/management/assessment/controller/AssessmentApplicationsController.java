package org.innovateuk.ifs.management.assessment.controller;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.application.list.populator.ManageApplicationsModelPopulator;
import org.innovateuk.ifs.management.navigation.ManagementApplicationOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;

@Controller
@RequestMapping("/assessment/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can manage assessment applications", securedType = AssessmentApplicationsController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT_APPLICATIONS')")
public class AssessmentApplicationsController extends BaseAssessmentController {

    @Autowired
    private ApplicationCountSummaryRestService applicationCountSummaryRestService;

    @Autowired
    private ManageApplicationsModelPopulator manageApplicationsPopulator;

    @GetMapping("/applications")
    public String manageApplications(Model model,
                                     @PathVariable("competitionId") long competitionId,
                                     @RequestParam MultiValueMap<String, String> queryParams,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "filterSearch", defaultValue = "") String filter) {
        CompetitionResource competitionResource = getCompetition(competitionId);

        ApplicationCountSummaryPageResource applicationCounts = getCounts(competitionId, page, filter);

        String originQuery = buildOriginQueryString(ManagementApplicationOrigin.MANAGE_APPLICATIONS, queryParams);

        model.addAttribute("model", manageApplicationsPopulator.populateModel(competitionResource, applicationCounts, filter, originQuery));
        model.addAttribute("originQuery", originQuery);

        return "competition/manage-applications";
    }

    protected ApplicationCountSummaryPageResource getCounts(long competitionId, int page, String filter) {
        return applicationCountSummaryRestService
                .getApplicationCountSummariesByCompetitionId(competitionId, page, PAGE_SIZE, filter)
                .getSuccess();
    }
}
