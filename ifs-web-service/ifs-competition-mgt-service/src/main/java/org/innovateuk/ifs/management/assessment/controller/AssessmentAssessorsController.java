package org.innovateuk.ifs.management.assessment.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.service.AssessorCountSummaryRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.assessor.populator.ManageAssessorsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/assessment/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can manage assessments", securedType = AssessmentAssessorsController.class)
@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'ASSESSMENT')")
public class AssessmentAssessorsController extends BaseAssessmentController {

    private static final String FILTER_FORM_ATTR_NAME = "filterForm";
    @Autowired
    private AssessorCountSummaryRestService applicationCountSummaryRestService;

    @Autowired
    private ManageAssessorsModelPopulator manageApplicationsPopulator;

    @GetMapping("/assessors")
    public String manageAssessors(Model model,
                                  @PathVariable("competitionId") long competitionId,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "assessorNameFilter", required = false) String assessorNameFilter
    ) {
        CompetitionResource competitionResource = getCompetition(competitionId);

        AssessorCountSummaryPageResource applicationCounts = getCounts(competitionId, assessorNameFilter, page);

        model.addAttribute("model", manageApplicationsPopulator.populateModel(competitionResource, applicationCounts));

        return "competition/manage-assessors";
    }

    private AssessorCountSummaryPageResource getCounts(long competitionId, String assessorNameFilter, int page) {
        return applicationCountSummaryRestService
                .getAssessorCountSummariesByCompetitionId(competitionId, StringUtils.trim(assessorNameFilter), page, PAGE_SIZE)
                .getSuccess();
    }
}