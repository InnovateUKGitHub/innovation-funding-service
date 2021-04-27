package org.innovateuk.ifs.management.assessment.controller;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.service.AssessorCountSummaryRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.management.assessment.populator.AssessorAssessmentPeriodChoiceModelPopulator;
import org.innovateuk.ifs.management.assessor.populator.ManageAssessorsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.String.format;

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

    @Autowired
    private AssessmentPeriodRestService assessmentPeriodRestService;

    @Autowired
    private AssessorAssessmentPeriodChoiceModelPopulator assessorAssessmentPeriodChoiceModelPopulator;

    @GetMapping("/assessors")
    public String manageAssessors(Model model,
                                  @PathVariable("competitionId") long competitionId,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "assessorNameFilter", required = false) String assessorNameFilter
    ) {
        List<AssessmentPeriodResource> assessmentPeriods = assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId).getSuccess();

        if (assessmentPeriods.size() > 1) {
            model.addAttribute("model", assessorAssessmentPeriodChoiceModelPopulator.populate(competitionId));
            return "competition/assessor-progress-choose-period";
        }

        AssessmentPeriodResource assessmentPeriod = assessmentPeriods.get(0);

        return format("redirect:/assessment/competition/%s/assessors/period?assessmentPeriodId=%s", competitionId, assessmentPeriod.getId());
    }

    @GetMapping("/assessors/period")
    public String manageAssessorsForPeriod(Model model,
                                           @PathVariable("competitionId") long competitionId,
                                           @RequestParam("assessmentPeriodId") long assessmentPeriodId,
                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "assessorNameFilter", required = false) String assessorNameFilter
    ) {

        CompetitionResource competitionResource = getCompetition(competitionId);
        AssessorCountSummaryPageResource applicationCounts = getCounts(competitionId, assessmentPeriodId, assessorNameFilter, page);

        model.addAttribute("model", manageApplicationsPopulator.populateModel(competitionResource, applicationCounts, assessmentPeriodId));

        return "competition/manage-assessors";
    }

    private AssessorCountSummaryPageResource getCounts(long competitionId, long assessmentPeriodId, String assessorNameFilter, int page) {
        return applicationCountSummaryRestService
                .getAssessorCountSummariesByCompetitionIdAndAssessmentPeriodId(competitionId, assessmentPeriodId, StringUtils.trim(assessorNameFilter), page, PAGE_SIZE)
                .getSuccess();
    }
}