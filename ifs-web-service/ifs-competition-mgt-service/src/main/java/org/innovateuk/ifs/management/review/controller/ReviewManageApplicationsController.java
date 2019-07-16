package org.innovateuk.ifs.management.review.controller;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.navigation.ManagementApplicationOrigin;
import org.innovateuk.ifs.management.review.model.ManageReviewApplicationsModelPopulator;
import org.innovateuk.ifs.review.service.ReviewRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;

/**
 * Controller for the 'Manage Applications' assessment panel page.
 */
@Controller
@RequestMapping("/assessment/panel/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "Only comp admin and project finance users can setup assessment" +
        " panels if they competition supports them", securedType = ReviewManageApplicationsController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'REVIEW_APPLICATIONS')")
public class ReviewManageApplicationsController {
    private static final int PAGE_SIZE  = 20;

    @Autowired
    private ManageReviewApplicationsModelPopulator manageReviewApplicationsModelPopulator;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ReviewRestService reviewRestService;

    @GetMapping("/manage-applications")
    public String manageApplications(Model model,
                                     @PathVariable("competitionId") long competitionId,
                                     @RequestParam MultiValueMap<String, String> queryParams,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "filterSearch", defaultValue = "") String filter,
                                     @RequestParam(value = "sort", defaultValue = "") String sortBy) {
        CompetitionResource competitionResource = competitionRestService
                .getCompetitionById(competitionId)
                .getSuccess();
        String originQuery = buildOriginQueryString(ManagementApplicationOrigin.MANAGE_APPLICATIONS_PANEL, queryParams);
        ApplicationSummaryPageResource applications = getSummaries(competitionResource.getId(), page, filter, sortBy);
        List<ApplicationSummaryResource> assignedApplications = getAssignedSummaries(competitionId);
        model.addAttribute("model", manageReviewApplicationsModelPopulator.populateModel(competitionResource, applications, assignedApplications, filter, sortBy, originQuery));
        model.addAttribute("originQuery", originQuery);

        return "competition/manage-applications-panel";
    }

    @GetMapping("/assign/{applicationId}")
    public String assignApplication(@PathVariable("competitionId") long competitionId, @PathVariable("applicationId") long applicationId) {
        reviewRestService.assignToPanel(applicationId);
        return format("redirect:/assessment/panel/competition/%d/manage-applications", competitionId);
    }

    @GetMapping("/unassign/{applicationId}")
    public String unassignApplication(@PathVariable("competitionId") long competitionId, @PathVariable("applicationId") long applicationId) {
        reviewRestService.unassignFromPanel(applicationId);
        return format("redirect:/assessment/panel/competition/%d/manage-applications", competitionId);
    }

    private ApplicationSummaryPageResource getSummaries(long competitionId, int page, String filter, String sortBy){
        return applicationSummaryRestService
                .getSubmittedApplicationsWithPanelStatus(competitionId, sortBy, page, PAGE_SIZE, Optional.of(filter), Optional.empty(), Optional.of(false))
                .getSuccess();
    }

    private List<ApplicationSummaryResource> getAssignedSummaries(long competitionId) {
        return applicationSummaryRestService
                .getSubmittedApplicationsWithPanelStatus(competitionId, null, 0, Integer.MAX_VALUE, Optional.empty(), Optional.empty(), Optional.of(true))
                .getSuccess().getContent();
    }
}
