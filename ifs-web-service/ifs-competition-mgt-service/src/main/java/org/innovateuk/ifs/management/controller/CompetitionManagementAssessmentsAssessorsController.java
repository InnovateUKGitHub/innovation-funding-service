package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.service.AssessorCountSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.form.AssessmentAssessorsFilterForm;
import org.innovateuk.ifs.management.model.ManageAssessorsModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

@Controller
@RequestMapping("/assessment/competition/{competitionId}")
public class CompetitionManagementAssessmentsAssessorsController extends BaseCompetitionManagementAssessmentsController<AssessorCountSummaryPageResource> {

    private static final String FILTER_FORM_ATTR_NAME = "filterForm";
    @Autowired
    private AssessorCountSummaryRestService applicationCountSummaryRestService;

    @Autowired
    private ManageAssessorsModelPopulator manageApplicationsPopulator;


    @GetMapping("/assessors")
    public String manageAssessors(Model model,
                                     @ModelAttribute(FILTER_FORM_ATTR_NAME) AssessmentAssessorsFilterForm filterForm,
                                     @PathVariable("competitionId") long competitionId,
                                     @RequestParam MultiValueMap<String, String> queryParams,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "filterInnovationSector", required = false) Long filterId,
                                     @RequestParam(value = "filterBusinessType", required = false) BusinessType businessType
                                  ) {
        CompetitionResource competitionResource = getCompetition(competitionId);

        AssessorCountSummaryPageResource applicationCounts = getCounts(competitionId, filterForm.getInnovationSector(), filterForm.getBusinessType(), page );

        String originQuery = buildOriginQueryString(ApplicationOverviewOrigin.MANAGE_ASSESSORS, queryParams);

        model.addAttribute("model", manageApplicationsPopulator.populateModel(competitionResource, applicationCounts, originQuery));
        model.addAttribute("originQuery", originQuery);

        return "competition/manage-assessors";
    }

    protected AssessorCountSummaryPageResource getCounts(long competitionId, Long innovationSectorId, BusinessType businessType, int page) {
        return applicationCountSummaryRestService
                .getAssessorCountSummariesByCompetitionId(competitionId, Optional.ofNullable(innovationSectorId), Optional.ofNullable(businessType), page, PAGE_SIZE)
                .getSuccessObjectOrThrowException();
    }
}